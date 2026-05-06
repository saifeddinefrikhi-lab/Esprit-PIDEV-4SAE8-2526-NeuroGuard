# 🚀 CarePlan Service - JaCoCo Quick Start

## Installation des dépendances
```bash
# Maven installe automatiquement JaCoCo lors de la première exécution des tests
```

## Commandes essentielles

### 1️⃣ Exécuter les tests avec rapport JaCoCo (RECOMMANDÉ)
```bash
mvn clean test
```
**Génère:** `target/jacoco-reports/index.html`

---

### 2️⃣ Exécuter et ouvrir le rapport automatiquement
```bash
# Windows (PowerShell)
mvn clean test; Start-Process target\jacoco-reports\index.html

# Linux/Mac
mvn clean test && open target/jacoco-reports/index.html
```

---

### 3️⃣ Exécuter un test spécifique
```bash
mvn clean test -Dtest=CarePlanServiceTest
mvn clean test -Dtest=StatisticsServiceTest
mvn clean test -Dtest=RiskAnalysisServiceTest
mvn clean test -Dtest=PrescriptionServiceTest
```

---

### 4️⃣ Ignorer les vérifications de couverture minimale
```bash
mvn clean test -Djacoco.skip=true
```
Utile lors du développement rapide, mais l'ajouter au commit final.

---

### 5️⃣ Générer rapport XML (pour CI/CD)
```bash
mvn clean test jacoco:report
# Fichier: target/jacoco-reports/jacoco.xml
```

---

### 6️⃣ Exécuter les tests en mode verbose
```bash
mvn clean test -X
# Affiche tous les détails d'exécution et dépannage
```

---

## 🖥️ Utiliser les scripts prêts à l'emploi

### Windows
```bash
run-tests-jacoco.bat
```
Menu interactif avec 6 options

### Linux/Mac
```bash
chmod +x run-tests-jacoco.sh
./run-tests-jacoco.sh
```
Menu interactif avec 6 options

---

## 📊 Consulter le rapport

1. Après exécution: `target/jacoco-reports/index.html`
2. Ouvrir dans votre navigateur préféré
3. Explorer la couverture par package/classe
4. Cliquer sur les noms de fichiers pour voir les lignes non couvertes (rouge)

---

## ✅ Résumé de la configuration JaCoCo

**Fichier configuré:** `pom.xml`

| Élément | Configuration |
|---------|--------------|
| **Plugin** | `org.jacoco:jacoco-maven-plugin:0.8.10` |
| **Couverture minimale** | 50% (par package) |
| **Rapport HTML** | Généré automatiquement après les tests |
| **Fichier d'exécution** | `target/jacoco.exec` |
| **Rapports** | `target/jacoco-reports/` |

---

## 🔴 Si les tests échouent

### Erreur: "Insufficient code coverage"
→ Ajoutez plus de tests pour atteindre 50% de couverture
→ Ou utilisez `-Djacoco.skip=true` pour temporairement ignorer les vérifications

### Erreur: "Maven not found"
→ Installer Maven: https://maven.apache.org/install.html
→ Ajouter au PATH d'environnement

### Rapport non généré
```bash
mvn clean
mvn test jacoco:report -X
```
→ L'option `-X` montre les détails du problème

---

## 📚 Tests disponibles

- ✅ CarePlanServiceTest
- ✅ StatisticsServiceTest
- ✅ RiskAnalysisServiceTest
- ✅ PrescriptionServiceTest
- ✅ CareplanServiceApplicationTests

---

## 💡 Bonnes pratiques

1. **Avant de committer:** `mvn clean test` (doit passer)
2. **Améliorer la couverture progressivement:** Viser 70%+
3. **Exécuter localement:** Avant de pousser sur la branche
4. **En CI/CD:** Utiliser `mvn clean test jacoco:report` avec publication d'artefacts

---

**Pour plus de détails:** Voir `JACOCO_TEST_GUIDE.md`
