# ─────────────────────────────────────────────────────────────────────────────
# Dockerfile — recommendation-service
# ─────────────────────────────────────────────────────────────────────────────
FROM python:3.11-slim

WORKDIR /app

# Install dependencies first (cached layer)
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code
COPY app.py .

# Copy model artefacts (must exist before building — see README)
COPY models/ models/

EXPOSE 5002

ENV PORT=5002
ENV EUREKA_URL=http://eureka-server:8761/eureka/
ENV INSTANCE_HOST=recommendation-service

CMD ["python", "app.py"]
