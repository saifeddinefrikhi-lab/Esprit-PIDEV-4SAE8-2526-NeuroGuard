# 🚀 JaCoCo - Commandes Essentielles Cheat Sheet

## 📌 Quick Reference - Tous les Services

### ⚡ Commandes de Base

#### 1️⃣ Exécuter les tests (Simple)
```bash
mvn clean test
```
✅ Exécute les tests, génère le rapport, vérifie la couverture

---

#### 2️⃣ Exécuter et afficher le rapport
```bash
# Windows
mvn clean test && start target\jacoco-reports\index.html

# Linux/Mac
mvn clean test && open target/jacoco-reports/index.html
```

---

#### 3️⃣ Exécuter un test spécifique
```bash
mvn clean test -Dtest=MyServiceTest
```
Exemples:
```bash
mvn clean test -Dtest=CarePlanServiceTest
mvn clean test -Dtest=PrescriptionServiceTest
mvn clean test -Dtest=PharmacyServiceTest
```

---

#### 4️⃣ Ignorer les vérifications de couverture (Développement rapide)
```bash
mvn clean test -Djacoco.skip=true
```
⚠️ À utiliser temporairement seulement

---

#### 5️⃣ Mode Verbose (Déboguer)
```bash
mvn clean test -X
```
Affiche tous les détails d'exécution

---

### 🏢 Commandes par Service

#### CarePlan Service
```bash
cd careplan-service
mvn clean test
start target\jacoco-reports\index.html
```

#### Prescription Service
```bash
cd prescription-service
mvn clean test
start target\jacoco-reports\index.html
```

#### Pharmacy Service
```bash
cd pharmacy-service
mvn clean test
start target\jacoco-reports\index.html
```

---

### 🔄 Tester Tous les Services

#### Windows (PowerShell)
```powershell
mvn -f careplan-service/pom.xml clean test; `
mvn -f prescription-service/pom.xml clean test; `
mvn -f pharmacy-service/pom.xml clean test
```

#### Linux/Mac (Bash)
```bash
mvn -f careplan-service/pom.xml clean test && \
mvn -f prescription-service/pom.xml clean test && \
mvn -f pharmacy-service/pom.xml clean test
```

---

### 📊 Générer Rapports XML (pour CI/CD)

```bash
mvn clean test jacoco:report
```
Génère: `target/jacoco-reports/jacoco.xml`

---

### 🔍 Ouvrir les Rapports

#### CarePlan
```bash
start careplan-service/target/jacoco-reports/index.html
```

#### Prescription
```bash
start prescription-service/target/jacoco-reports/index.html
```

#### Pharmacy
```bash
start pharmacy-service/target/jacoco-reports/index.html
```

---

## 📚 Utiliser les Scripts Automatisés

### Windows (.bat)
```bash
# CarePlan
careplan-service\run-tests-jacoco.bat

# Prescription
prescription-service\run-tests-jacoco.bat

# Pharmacy
pharmacy-service\run-tests-jacoco.bat
```
Menu interactif avec 5 options

---

### Linux/Mac (.sh)
```bash
# CarePlan
chmod +x careplan-service/run-tests-jacoco.sh
./careplan-service/run-tests-jacoco.sh

# Prescription
chmod +x prescription-service/run-tests-jacoco.sh
./prescription-service/run-tests-jacoco.sh

# Pharmacy
chmod +x pharmacy-service/run-tests-jacoco.sh
./pharmacy-service/run-tests-jacoco.sh
```
Menu interactif avec 5 options

---

## 🎯 Scénarios Courants

### Scénario 1: Je viens de finir mon code
```bash
mvn clean test
```
Si ✅ passe → Prêt à committer
Si ❌ échoue → Consulter le rapport et corriger

---

### Scénario 2: Je veux améliorer la couverture
```bash
mvn clean test
start target\jacoco-reports\index.html
```
1. Regarder les lignes en rouge (non couvertes)
2. Ajouter des tests pour ces lignes
3. Réexécuter jusqu'à 70%+ de couverture

---

### Scénario 3: Je développe rapidement et tester ralentit
```bash
mvn clean test -Djacoco.skip=true
```
Mais n'oublie pas de faire `mvn clean test` avant de committer!

---

### Scénario 4: Un test échoue
```bash
mvn clean test -Dtest=MyServiceTest -X
```
Le `-X` montre tous les détails

---

### Scénario 5: Je veux vérifier juste mon service
```bash
cd my-service
mvn clean test
```

---

## 📊 Interprétation des Résultats

### ✅ BUILD SUCCESS
```
[INFO] All coverage checks have been met.
[INFO] BUILD SUCCESS
```
→ Tests passés et couverture suffisante ✅

---

### ❌ BUILD FAILURE
```
[WARNING] Rule violated for package xxx: lines covered ratio is X.XX, but expected minimum is Y.YY
[INFO] BUILD FAILURE
```
→ Couverture insuffisante - Ajouter des tests

---

## 📈 Rapports JaCoCo - Ce qu'on y voit

Après ouvrir `target/jacoco-reports/index.html`:

1. **Vue d'ensemble** (haut)
   - Line Coverage: % global
   - Branch Coverage: % décisions
   - Classes: # couvertes

2. **Par package** (milieu)
   - Cliquer sur un package
   - Voir les classes dedans

3. **Code source** (bas)
   - Lignes vertes: ✅ couvertes
   - Lignes rouges: ❌ non couvertes
   - Cliquer sur une classe pour voir le détail

---

## 🛠️ Configuration pour Ambitieux

### Modifier la couverture minimale

Dans `pom.xml` de chaque service:
```xml
<minimum>0.25</minimum>   <!-- Actuellement 25% -->
```

Changer à:
```xml
<minimum>0.50</minimum>   <!-- Augmenter à 50% -->
```

---

## 📞 Troubleshooting

### "Maven not found"
```bash
# Vérifier si Maven est installé
mvn --version

# Si non: https://maven.apache.org/install.html
```

---

### "Rapport ne se génère pas"
```bash
# Vérifier le pom.xml a bien JaCoCo
mvn help:describe -Dplugin=org.jacoco:jacoco-maven-plugin

# Forcer avec -X
mvn clean test -X | grep -i jacoco
```

---

### "Tests trop lents"
```bash
# Ajouter -T pour paralléliser
mvn clean test -T 1C

# C = cores, ex: -T 4C = 4 cores
```

---

### "Je veux nettoyer les anciennes données"
```bash
# Supprimer target/ et recréer
rm -r target/
mvn clean test
```

---

## 📚 Pour Plus d'Info

### Guides Disponibles
- **JACOCO_QUICKSTART.md** - Commandes rapides
- **TESTING_BEST_PRACTICES.md** - Comment écrire des tests
- **REPORT_INTERPRETATION.md** - Comment lire les rapports
- **JACOCO_ADVANCED_CONFIG.md** - Configurations avancées

### Sites Officiels
- [JaCoCo Documentation](https://www.jacoco.org)
- [JUnit 5 User Guide](https://junit.org/junit5)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core)

---

## ⏱️ Temps Typical d'Exécution

| Service | Tests | Temps |
|---------|-------|-------|
| CarePlan | 12 | ~60 sec |
| Prescription | 12 | ~40 sec |
| Pharmacy | 10 | ~37 sec |
| **Tous** | **34** | **~140 sec** |

---

## 🎓 Best Practices

✅ **À faire:**
```bash
# Avant de committer
mvn clean test

# Vérifier les rapports
start target\jacoco-reports\index.html

# Ajouter des tests progressivement
# Viser 70%+ pour services critiques
```

❌ **À éviter:**
```bash
# Ne pas committer si test échoue
# Ne pas ignorer JaCoCo check en production
# Ne pas mélanger plusieurs tâches dans un commit
```

---

## 🚀 Workflow Recommandé

```
1. Faire des modifications au code
   ↓
2. Exécuter: mvn clean test
   ↓
3. Si ✅ passe:
   • Consulter rapport JaCoCo
   • Ajouter tests si couverture < 70%
   • Committer
   ↓
4. Si ❌ échoue:
   • Lire les erreurs
   • Corriger le code ou ajouter tests
   • Réessayer à l'étape 2
```

---

**Dernière mise à jour:** 2026-05-06  
**Status:** ✅ All Services Ready
