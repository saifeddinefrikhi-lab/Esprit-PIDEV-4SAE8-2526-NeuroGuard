# 📊 JaCoCo Coverage Report Interpretation Guide

## Qu'est-ce que JaCoCo?

**JaCoCo** (Java Code Coverage) est un outil qui mesure la **couverture de code** des tests unitaires. Il répond à la question: *"Quel pourcentage de mon code est exécuté par les tests?"*

---

## 🎯 Métriques Principales

### 1. **Line Coverage (Couverture de lignes)**
- **Définition:** Le pourcentage de lignes de code exécutées par les tests
- **Exemple:** Si vous avez 100 lignes et que les tests exécutent 70 lignes → 70% de line coverage
- **Bon objectif:** 70-80%

```
Couverture = (Lignes exécutées / Lignes totales) × 100
```

### 2. **Branch Coverage (Couverture de branches)**
- **Définition:** Le pourcentage des décisions (if/else, switch, boucles) qui sont testées
- **Exemple:** Un if statement a 2 branches (true/false) → Si seulement true est testée → 50% de branch coverage
- **Bon objectif:** 60-75%

```java
if (user.isActive()) {      // Branch 1: true
    doSomething();
} else {                     // Branch 2: false
    doSomethingElse();
}
```

### 3. **Cyclomatic Complexity**
- **Définition:** Mesure la complexité du chemin logique dans le code
- **Plus simple = meilleur:** Moins de branches = plus facile à tester et maintenir
- **Formule:** CC = E - N + 2P (E=arêtes, N=nœuds, P=composantes)

---

## 🔍 Comment Lire le Rapport HTML

### Étape 1: Ouvrir le rapport
```
target/jacoco-reports/index.html
```

### Étape 2: Vue d'ensemble (Premier écran)
```
Instruction Coverage:  65%    (ligne par ligne)
Branch Coverage:       55%    (if/else, switch)
Line Coverage:         68%    (lignes totales)
Complexity:            45/125 (nombre de branches)
Method Coverage:       72%    (méthodes testées)
Class Coverage:        80%    (classes testées)
```

### Étape 3: Explorer les packages
- Cliquer sur un package pour voir les classes
- Chercher les classes en rouge (faible couverture)
- Cliquer sur une classe pour voir les lignes non couvertes

### Étape 4: Voir les lignes non couvertes
```java
public class CarePlanService {
    
    public void createCarePlan(CarePlan plan) {    // ✅ COUVERT
        validatePlan(plan);                         // ✅ COUVERT
        if (plan.isValid()) {                       // ✅ Branch testée
            savePlan(plan);                         // ✅ COUVERT
        } else {
            logError("Invalid plan");                // ❌ NON COUVERT
        }
    }
}
```

**Les lignes en rouge ne sont pas exécutées par les tests**

---

## 📈 Résultat d'Exemple - CarePlan Service

### Rapport attendu après première exécution:
```
╔════════════════════════════════════════════════════════════╗
║              CarePlan Service Coverage Report              ║
╠════════════════════════════════════════════════════════════╣
║ Package                    │ Coverage │ Status            ║
╟────────────────────────────┼──────────┼──────────────────╢
║ com.esprit...services      │   65%    │ ⚠️  Moyen        ║
║ com.esprit...controllers   │   55%    │ ❌ Faible        ║
║ com.esprit...repositories  │   45%    │ ❌ Faible        ║
║ com.esprit...entities      │   30%    │ ❌ Très faible   ║
║ com.esprit...dto           │   20%    │ ❌ N/A            ║
╟────────────────────────────┼──────────┼──────────────────╢
║ TOTAL                      │   48%    │ ⚠️  Minimum OK   ║
╚════════════════════════════════════════════════════════════╝
```

---

## 🎨 Code Coverage Legend

| Couleur | Signification | Action |
|---------|--------------|--------|
| 🟢 **Vert** | > 70% couverture | ✅ Excellent - Maintenir |
| 🟡 **Jaune** | 50-70% couverture | ⚠️ Moyen - Améliorer |
| 🔴 **Rouge** | < 50% couverture | ❌ Faible - Ajouter des tests |

---

## 💡 Comment Améliorer la Couverture

### 1. Identifier les lignes non couvertes
1. Ouvrir le rapport
2. Chercher les packages en rouge
3. Cliquer sur la classe
4. Les lignes en **rose** = non couvertes

### 2. Ajouter des tests pour les cas manquants

#### Exemple: Tester le cas d'erreur
```java
@Test
void shouldThrowExceptionWhenCarePlanIsInvalid() {
    // Arrange
    CarePlan invalidPlan = new CarePlan();
    invalidPlan.setName(null);  // Invalid
    
    // Act & Assert
    assertThrows(IllegalArgumentException.class, 
        () -> carePlanService.createCarePlan(invalidPlan));
}
```

### 3. Tester tous les branches

```java
// Avant: seul le cas "success" était testé
@Test
void shouldCreateCarePlan() {
    carePlanService.createCarePlan(validPlan);
    verify(repository).save(validPlan);
}

// Après: ajouter le cas "error"
@Test
void shouldLogErrorWhenCarePlanIsInvalid() {
    carePlanService.createCarePlan(invalidPlan);
    verify(logger).error(anyString());
}
```

---

## 📋 Checklist avant de Committer

- [ ] `mvn clean test` passe tous les tests
- [ ] Couverture >= 50% (seuil minimum)
- [ ] Aucun warning JaCoCo
- [ ] Rapport généré sans erreur
- [ ] Au moins 70% dans les services critiques

```bash
# Commande finale avant commit
mvn clean test
```

---

## 🔧 Fichiers Générés

Après `mvn clean test`:

```
target/
├── jacoco.exec                          # Données brutes de couverture
├── jacoco-reports/
│   ├── index.html                       # 📊 Rapport principal
│   ├── status.xml                       # Statut résumé
│   ├── jacoco.xml                       # Format XML (pour SonarQube)
│   ├── csv/                             # Rapports CSV
│   └── com/esprit/.../                  # Détails par package
└── surefire-reports/
    ├── TEST-*.xml                       # Résultats JUnit
    └── *.txt                            # Logs de test
```

---

## 🌐 Intégration avec SonarQube (Optionnel)

Si vous utilisez **SonarQube** pour analyser la qualité:

```xml
<!-- Dans pom.xml -->
<sonar.coverage.jacoco.xmlReportPaths>
    target/jacoco-reports/jacoco.xml
</sonar.coverage.jacoco.xmlReportPaths>
```

---

## 📞 Dépannage

### Problème: Rapport ne s'affiche pas
```bash
# Vérifier les détails
mvn clean test -X | grep -i jacoco
```

### Problème: Coverage très bas (< 10%)
- Les tests ne s'exécutent pas
- Vérifier: `mvn test` affiche les résultats JUnit

### Problème: Build échoue sur couverture
```bash
# Voir le seuil actuel dans pom.xml
<minimum>0.50</minimum>  <!-- Actuellement 50% -->
```

---

## 🚀 Commandes Rapides

```bash
# Exécution simple
mvn clean test

# Voir rapport HTML
start target/jacoco-reports/index.html  # Windows
open target/jacoco-reports/index.html   # Mac
xdg-open target/jacoco-reports/index.html # Linux

# Exporter pour SonarQube
mvn clean test jacoco:report

# Ignorer les vérifications (dev rapide)
mvn clean test -Djacoco.skip=true
```

---

**Dernière mise à jour:** 2026-05-06  
**Version JaCoCo:** 0.8.10  
**Version Maven Surefire:** 3.0.0
