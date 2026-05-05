"""
NeuroGuard — Care Plan Recommendation Service
=============================================
A standalone Flask microservice that loads a trained Random Forest model
(exported from the Alzheimer_CarePlan_Recommendation_ML notebook) and
exposes a REST API to recommend personalised care plans for Alzheimer patients.

Endpoints
---------
GET  /health                   – liveness probe
POST /api/recommendation/predict – predict cluster + return care plan
GET  /api/recommendation/care-plans – list all care plans

The service registers itself with Eureka so Spring Cloud Gateway can
route requests to it automatically.
"""

import os
import logging
import joblib
import numpy as np
import pandas as pd
from flask import Flask, request, jsonify
from flask_cors import CORS
import py_eureka_client.eureka_client as eureka_client

# ─────────────────────────────────────────────
# Logging
# ─────────────────────────────────────────────
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s  %(levelname)-8s  %(name)s  %(message)s",
)
logger = logging.getLogger("recommendation-service")

# ─────────────────────────────────────────────
# Flask app
# ─────────────────────────────────────────────
app = Flask(__name__)
# CORS is handled by the API Gateway, so we do not use CORS(app) here to avoid duplicate headers.

# ─────────────────────────────────────────────
# Care-plan dictionary (mirrors the notebook)
# ─────────────────────────────────────────────
CARE_PLANS = {
    0: {
        "cluster": 0,
        "profile": "Advanced Stage — Total Dependence",
        "description": (
            "Patient shows severe cognitive decline (low MMSE), very low functional "
            "assessment and ADL scores, and is fully dependent on caregivers."
        ),
        "medicalFollowUp": "Daily monitoring + remote telemonitoring",
        "physicalActivity": "Gentle assisted exercises, sensory stimulation",
        "nutrition": "Diet adapted for swallowing difficulties",
        "medication": "3x/day medication intake under supervision",
        "additionalNotes": (
            "Close caregiver involvement required. Regular reassessment every 2 weeks."
        ),
    },
    1: {
        "cluster": 1,
        "profile": "Mild/Moderate Stage — Partially Autonomous Patient",
        "description": (
            "Patient exhibits mild-to-moderate cognitive decline, retains partial "
            "autonomy in daily activities, and has a higher MMSE score."
        ),
        "medicalFollowUp": "Monthly outpatient consultation",
        "physicalActivity": "30-min daily walk, light cognitive activities",
        "nutrition": "Mediterranean diet, regular hydration",
        "medication": "1x/day medication intake (morning)",
        "additionalNotes": (
            "Encourage social engagement. Reassess every 3 months or if symptoms worsen."
        ),
    },
}

# ─────────────────────────────────────────────
# Expected feature columns (same order as training)
# ─────────────────────────────────────────────
# NOTE: 'Diagnosis' is intentionally excluded.
# The notebook trains on: X = df.drop(columns=['Cluster', 'Diagnosis'])
# so the RF classifier expects exactly 32 features.
FEATURE_COLUMNS = [
    "Age", "Gender", "Ethnicity", "EducationLevel", "BMI",
    "Smoking", "AlcoholConsumption", "PhysicalActivity", "DietQuality",
    "SleepQuality", "FamilyHistoryAlzheimers", "CardiovascularDisease",
    "Diabetes", "Depression", "HeadInjury", "Hypertension",
    "SystolicBP", "DiastolicBP", "CholesterolTotal", "CholesterolLDL",
    "CholesterolHDL", "CholesterolTriglycerides", "MMSE",
    "FunctionalAssessment", "MemoryComplaints", "BehavioralProblems",
    "ADL", "Confusion", "Disorientation", "PersonalityChanges",
    "DifficultyCompletingTasks", "Forgetfulness",
]  # 32 features

# ─────────────────────────────────────────────
# Model artefacts
# ─────────────────────────────────────────────
MODEL = None
SCALER = None


def _load_artifacts():
    """Load the trained Random Forest model and scaler from disk."""
    global MODEL, SCALER

    model_path  = os.environ.get("MODEL_PATH",  "models/recommendation_model.pkl")
    scaler_path = os.environ.get("SCALER_PATH", "models/recommendation_scaler.pkl")

    if not os.path.exists(model_path):
        logger.warning(
            "Model file not found at '%s'. "
            "Run the export cell in Colab first, then place the .pkl files in "
            "recommendation-service/models/",
            model_path,
        )
        return False

    if not os.path.exists(scaler_path):
        logger.warning(
            "Scaler file not found at '%s'. "
            "Run the export cell in Colab first, then place the .pkl files in "
            "recommendation-service/models/",
            scaler_path,
        )
        return False

    try:
        MODEL  = joblib.load(model_path)
        SCALER = joblib.load(scaler_path)
        logger.info("Model and scaler loaded successfully.")
        return True
    except Exception as exc:
        logger.error("Failed to load model artefacts: %s", exc)
        return False


# ─────────────────────────────────────────────
# Routes
# ─────────────────────────────────────────────

@app.route("/health", methods=["GET"])
def health():
    """Liveness / readiness probe."""
    ready = MODEL is not None and SCALER is not None
    return jsonify(
        {
            "status": "UP" if ready else "STARTING",
            "service": "recommendation-service",
            "modelLoaded": ready,
        }
    ), 200


@app.route("/api/recommendation/care-plans", methods=["GET"])
def list_care_plans():
    """Return all available care plan definitions."""
    return jsonify({"carePlans": list(CARE_PLANS.values())}), 200


@app.route("/api/recommendation/predict", methods=["POST"])
def predict():
    """
    Accept a JSON body with patient features and return a cluster prediction
    plus the corresponding personalised care plan.

    Expected body (all 33 features used during training):
    {
      "Age": 75,
      "Gender": 1,
      "Ethnicity": 0,
      ...
      "Diagnosis": 1
    }

    Response:
    {
      "cluster": 0,
      "confidence": 0.87,
      "carePlan": { ... }
    }
    """
    if MODEL is None or SCALER is None:
        return (
            jsonify(
                {
                    "error": "Model not loaded. Place recommendation_model.pkl and "
                             "recommendation_scaler.pkl in the models/ directory and restart."
                }
            ),
            503,
        )

    body = request.get_json(force=True, silent=True)
    if not body:
        return jsonify({"error": "Request body must be valid JSON."}), 400

    # ── Build feature DataFrame ──────────────────────────────────────────────
    patient_df = pd.DataFrame([body])

    # Reindex to guarantee column order; fill missing columns with 0
    patient_df = patient_df.reindex(columns=FEATURE_COLUMNS, fill_value=0)

    # ── Validate that all values are numeric ────────────────────────────────
    non_numeric = [
        col for col in patient_df.columns
        if not pd.api.types.is_numeric_dtype(patient_df[col])
    ]
    if non_numeric:
        return (
            jsonify({"error": f"Non-numeric values detected in columns: {non_numeric}"}),
            400,
        )

    # ── Predict ─────────────────────────────────────────────────────────────
    try:
        # The notebook does NOT scale features before the RF classifier
        # (scaling was applied only for KMeans); pass raw features to RF.
        cluster_pred  = int(MODEL.predict(patient_df)[0])
        probabilities = MODEL.predict_proba(patient_df)[0]
        confidence    = float(np.max(probabilities))
    except Exception as exc:
        logger.exception("Prediction failed: %s", exc)
        return jsonify({"error": f"Prediction error: {exc}"}), 500

    care_plan = CARE_PLANS.get(cluster_pred, CARE_PLANS[0])

    return (
        jsonify(
            {
                "cluster":    cluster_pred,
                "confidence": round(confidence, 4),
                "carePlan":   care_plan,
            }
        ),
        200,
    )


# ─────────────────────────────────────────────
# Eureka registration
# ─────────────────────────────────────────────

def _register_with_eureka():
    port         = int(os.environ.get("PORT", 5002))
    eureka_url   = os.environ.get("EUREKA_URL", "http://localhost:8761/eureka/")
    instance_host = os.environ.get("INSTANCE_HOST", "localhost")

    try:
        eureka_client.init(
            eureka_server          = eureka_url,
            app_name               = "recommendation-service",
            instance_port          = port,
            instance_host          = instance_host,
            health_check_url       = f"http://{instance_host}:{port}/health",
            status_page_url        = f"http://{instance_host}:{port}/health",
            renewal_interval_in_secs = 30,
            duration_in_secs       = 90,
        )
        logger.info("Registered with Eureka at %s", eureka_url)
    except Exception as exc:
        logger.warning("Eureka registration failed (service will still run): %s", exc)


# ─────────────────────────────────────────────
# Entry point
# ─────────────────────────────────────────────

if __name__ == "__main__":
    port = int(os.environ.get("PORT", 5002))

    _load_artifacts()
    _register_with_eureka()

    logger.info("Starting recommendation-service on port %d", port)
    app.run(host="0.0.0.0", port=port, debug=False)
