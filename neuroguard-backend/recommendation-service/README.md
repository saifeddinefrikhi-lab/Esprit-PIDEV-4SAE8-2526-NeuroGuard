# recommendation-service

A standalone Python/Flask microservice that provides **personalised Alzheimer
care plan recommendations** using a trained Random Forest classifier.

It is part of the NeuroGuard microservices backend and registers itself with
the Eureka service registry so the Spring Cloud Gateway can route requests to it.

---

## Architecture

```
Angular (port 4200)
    ↓
Spring Cloud Gateway (port 8083)  →  /api/recommendation/**
    ↓ (lb://recommendation-service)
recommendation-service (port 5002)
    ↓
Random Forest model (.pkl) + StandardScaler (.pkl)
```

---

## Step 1 — Export the model from Google Colab

Open your Colab notebook (`Alzheimer_CarePlan_Recommendation_ML(1).ipynb`) and
add a **new cell at the end** with the contents of `colab_export_cell.py`:

```python
import joblib
joblib.dump(rf,     "recommendation_model.pkl")
joblib.dump(scaler, "recommendation_scaler.pkl")
print("Done!")
```

Run the cell. Two files appear in the Colab file browser (left panel 📁):

| File | Description |
|---|---|
| `recommendation_model.pkl` | Trained Random Forest classifier |
| `recommendation_scaler.pkl` | Fitted StandardScaler |

Download **both** files and place them in `recommendation-service/models/`:

```
recommendation-service/
├── models/
│   ├── recommendation_model.pkl   ← paste here
│   └── recommendation_scaler.pkl  ← paste here
├── app.py
├── requirements.txt
└── Dockerfile
```

---

## Step 2 — Run locally

```bash
# Create virtual environment
python -m venv .venv

# Activate (Windows PowerShell)
.\.venv\Scripts\Activate.ps1

# Activate (Linux/macOS)
source .venv/bin/activate

# Install dependencies
pip install -r requirements.txt

# Start the service (Eureka must already be running on localhost:8761)
python app.py
```

The service starts on **http://localhost:5002**.

---

## API Endpoints

### `GET /health`
Returns service status and whether the model is loaded.

```json
{
  "status": "UP",
  "service": "recommendation-service",
  "modelLoaded": true
}
```

---

### `GET /api/recommendation/care-plans`
Returns all defined care plan profiles.

```json
{
  "carePlans": [
    {
      "cluster": 0,
      "profile": "Advanced Stage — Total Dependence",
      ...
    },
    {
      "cluster": 1,
      "profile": "Mild/Moderate Stage — Partially Autonomous Patient",
      ...
    }
  ]
}
```

---

### `POST /api/recommendation/predict`

**Request body** — all 33 patient features (same as in the Colab notebook):

```json
{
  "Age": 75,
  "Gender": 1,
  "Ethnicity": 0,
  "EducationLevel": 1,
  "BMI": 27.5,
  "Smoking": 0,
  "AlcoholConsumption": 1,
  "PhysicalActivity": 2.5,
  "DietQuality": 6.0,
  "SleepQuality": 6.5,
  "FamilyHistoryAlzheimers": 1,
  "CardiovascularDisease": 0,
  "Diabetes": 0,
  "Depression": 0,
  "HeadInjury": 0,
  "Hypertension": 1,
  "SystolicBP": 135,
  "DiastolicBP": 82,
  "CholesterolTotal": 210,
  "CholesterolLDL": 130,
  "CholesterolHDL": 55,
  "CholesterolTriglycerides": 150,
  "MMSE": 18,
  "FunctionalAssessment": 5.0,
  "MemoryComplaints": 1,
  "BehavioralProblems": 0,
  "ADL": 5.5,
  "Confusion": 0,
  "Disorientation": 0,
  "PersonalityChanges": 0,
  "DifficultyCompletingTasks": 1,
  "Forgetfulness": 1,
  "Diagnosis": 1
}
```

**Response:**

```json
{
  "cluster": 1,
  "confidence": 0.92,
  "carePlan": {
    "cluster": 1,
    "profile": "Mild/Moderate Stage — Partially Autonomous Patient",
    "description": "...",
    "medicalFollowUp": "Monthly outpatient consultation",
    "physicalActivity": "30-min daily walk, light cognitive activities",
    "nutrition": "Mediterranean diet, regular hydration",
    "medication": "1x/day medication intake (morning)",
    "additionalNotes": "..."
  }
}
```

---

## Environment Variables

| Variable | Default | Description |
|---|---|---|
| `PORT` | `5002` | Service HTTP port |
| `EUREKA_URL` | `http://localhost:8761/eureka/` | Eureka server URL |
| `INSTANCE_HOST` | `localhost` | Hostname advertised to Eureka |
| `MODEL_PATH` | `models/recommendation_model.pkl` | Path to RF model |
| `SCALER_PATH` | `models/recommendation_scaler.pkl` | Path to scaler |

---

## Docker

```bash
# Build (make sure models/*.pkl exist first!)
docker build -t recommendation-service .

# Run
docker run -p 5002:5002 \
  -e EUREKA_URL=http://host.docker.internal:8761/eureka/ \
  recommendation-service
```

---

## Gateway routing

The Spring Cloud Gateway (`gateway/src/main/resources/application.yaml`) is
already configured to forward all `GET/POST /api/recommendation/**` requests
to this service via Eureka load-balancing.

No additional configuration is required.
