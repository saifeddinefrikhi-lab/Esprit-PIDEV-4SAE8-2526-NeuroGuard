# 🚀 Prescription Service - JaCoCo Quick Start

## ⚡ Commandes Essentielles

### 1️⃣ Exécuter les tests avec rapport JaCoCo
```bash
mvn clean test
```
**Génère:** `target/jacoco-reports/index.html`

---

### 2️⃣ Ouvrir le rapport (Windows)
```powershell
start target\jacoco-reports\index.html
```

### 2️⃣ Ouvrir le rapport (Mac/Linux)
```bash
open target/jacoco-reports/index.html   # Mac
xdg-open target/jacoco-reports/index.html # Linux
```

---

### 3️⃣ Exécuter un test spécifique
```bash
mvn clean test -Dtest=PrescriptionServiceTest
```

---

### 4️⃣ Ignorer les vérifications de couverture minimale
```bash
mvn clean test -Djacoco.skip=true
```

---

### 5️⃣ Générer rapport XML (pour CI/CD)
```bash
mvn clean test jacoco:report
# Fichier généré: target/jacoco-reports/jacoco.xml
```

---

## 📊 Configuration

| Setting | Value |
|---------|-------|
| **Minimum Coverage** | 25% (services & exceptions) |
| **Report Location** | `target/jacoco-reports/index.html` |
| **Data File** | `target/jacoco.exec` |
| **JaCoCo Version** | 0.8.10 |

---

## 🎯 Prochaines Étapes

1. **Consulter le rapport:** `start target\jacoco-reports\index.html`
2. **Identifier les zones à améliorer** (en rouge)
3. **Ajouter des tests** progressivement
4. **Objectif final:** 70%+ couverture

---

**Pour plus d'infos:** Voir `careplan-service/JACOCO_TEST_GUIDE.md` (guide complet)
