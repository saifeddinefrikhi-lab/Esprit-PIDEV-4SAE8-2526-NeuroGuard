# ═══════════════════════════════════════════════════════════════
#  EXPORT CELL — run this at the END of your Colab notebook
#  after all training cells have executed successfully.
#
#  It saves:
#    • recommendation_model.pkl   — trained Random Forest classifier
#    • recommendation_scaler.pkl  — StandardScaler fitted on training data
#
#  Then download both files from the Colab file browser (left panel)
#  and place them in:
#    recommendation-service/models/
# ═══════════════════════════════════════════════════════════════

import joblib

# ── Save the Random Forest classifier ────────────────────────────────────────
# Variable name in notebook: rf
joblib.dump(rf, "recommendation_model.pkl")
print("✅ recommendation_model.pkl saved")

# ── Save the StandardScaler ───────────────────────────────────────────────────
# Variable name in notebook: scaler
joblib.dump(scaler, "recommendation_scaler.pkl")
print("✅ recommendation_scaler.pkl saved")

# ── Quick sanity check ────────────────────────────────────────────────────────
loaded_model  = joblib.load("recommendation_model.pkl")
loaded_scaler = joblib.load("recommendation_scaler.pkl")

# Test with the patient example already in the notebook
import pandas as pd

# NOTE: 'Diagnosis' is intentionally excluded — the notebook drops it before
# fitting the RF:  X = df.drop(columns=['Cluster', 'Diagnosis'])
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

# 'Diagnosis' removed — model was NOT trained on it
patient_exemple = {
    "Age": 82, "Gender": 1, "Ethnicity": 0, "EducationLevel": 0,
    "BMI": 23.0, "Smoking": 1, "AlcoholConsumption": 2,
    "PhysicalActivity": 0.5, "DietQuality": 3.0, "SleepQuality": 3.5,
    "FamilyHistoryAlzheimers": 1, "CardiovascularDisease": 1,
    "Diabetes": 1, "Depression": 1, "HeadInjury": 1, "Hypertension": 1,
    "SystolicBP": 165, "DiastolicBP": 98, "CholesterolTotal": 250,
    "CholesterolLDL": 170, "CholesterolHDL": 35,
    "CholesterolTriglycerides": 220,
    "MMSE": 6, "FunctionalAssessment": 1.5, "MemoryComplaints": 1,
    "BehavioralProblems": 1, "ADL": 1.0, "Confusion": 1,
    "Disorientation": 1, "PersonalityChanges": 1,
    "DifficultyCompletingTasks": 1, "Forgetfulness": 1,
}  # 32 features

test_df = pd.DataFrame([patient_exemple]).reindex(columns=FEATURE_COLUMNS, fill_value=0)
pred    = loaded_model.predict(test_df)[0]
proba   = loaded_model.predict_proba(test_df)[0]

print(f"\n🧪 Sanity check — predicted cluster: {pred}  (confidence: {max(proba):.2f})")
print("✅ Export complete — download both .pkl files from the Colab file browser.\n")
