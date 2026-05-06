# Guide de Test Unitaire avec JaCoCo - CarePlan Service

## 📋 Vue d'ensemble
Ce guide explique comment exécuter les tests unitaires du careplan-service avec JaCoCo pour générer des rapports de couverture de code.

## 🚀 Démarrage rapide

### 1. Exécuter les tests avec rapport JaCoCo
```bash
cd neuroguard-backend/careplan-service
mvn clean test
```

**Résultat:** 
- Les tests s'exécutent avec l'agent JaCoCo
- Un rapport HTML est généré dans: `target/jacoco-reports/index.html`

### 2. Générer un rapport complet
```bash
mvn clean test jacoco:report
```

## 📊 Structure des Rapports

### Emplacements des fichiers générés:
```
careplan-service/
├── target/
│   ├── jacoco.exec                 # Données brutes de couverture
│   ├── jacoco-reports/
│   │   ├── index.html              # Rapport principal (ouvrir dans navigateur)
│   │   ├── *.html                  # Rapports par package/classe
│   │   └── *.csv                   # Rapports en format CSV
│   └── surefire-reports/           # Rapports de tests JUnit
```

## 🔍 Interprétation des Rapports

### Métriques de couverture:
- **Line Coverage (Couverture de lignes):** % des lignes de code exécutées par les tests
- **Branch Coverage (Couverture de branches):** % des décisions exécutées (if/else, switch, etc.)
- **Cyclomatic Complexity:** Complexité du code

### Seuils configurés:
Le pom.xml est configuré avec un minimum de **50% de couverture** par package. Si ce seuil n'est pas atteint, la build échouera.

## 📝 Tests Existants

Les tests unitaires suivants sont actuellement configurés:

1. **CarePlanServiceTest** - Tests du service de plan de soin
2. **StatisticsServiceTest** - Tests du service de statistiques
3. **RiskAnalysisServiceTest** - Tests de l'analyse de risque
4. **PrescriptionServiceTest** - Tests du service de prescription
5. **CareplanServiceApplicationTests** - Tests d'intégration

## ✅ Commandes utiles

### Exécuter et afficher le rapport
```bash
# Nettoyer et exécuter tests avec JaCoCo
mvn clean test

# Ouvrir le rapport (Windows)
start target/jacoco-reports/index.html

# Ouvrir le rapport (Linux/Mac)
open target/jacoco-reports/index.html
```

### Ignorer les vérifications de couverture minimale
```bash
mvn clean test -Djacoco.skip=true
```

### Exécuter un test spécifique avec JaCoCo
```bash
mvn clean test -Dtest=CarePlanServiceTest
```

### Générer rapport XML (pour CI/CD)
```bash
mvn clean test jacoco:report
# Fichier généré: target/jacoco-reports/jacoco.xml
```

## 🔧 Configuration JaCoCo dans pom.xml

### Plugins configurés:

#### 1. **jacoco-maven-plugin**
- Phase initialize: Prépare l'agent JaCoCo
- Phase test: Génère le rapport HTML
- Phase test: Vérifie les seuils de couverture

#### 2. **maven-surefire-plugin**
- Configure l'intégration avec JaCoCo (`@{argLine}`)
- Exécute les tests JUnit

## 📈 Amélioration de la couverture

### Vérifier la couverture actuelle:
1. Ouvrir `target/jacoco-reports/index.html`
2. Identifier les packages/classes avec faible couverture
3. Ajouter des tests pour les sections non couvertes

### Ajouter un nouveau test:
```java
@ExtendWith(MockitoExtension.class)
class MyNewServiceTest {
    
    @Mock
    private MyRepository repository;
    
    @InjectMocks
    private MyService service;
    
    @Test
    void testShouldDoSomething() {
        // Arrange
        // Act
        // Assert
    }
}
```

## 🐛 Dépannage

### Le rapport JaCoCo ne se génère pas
```bash
# Vérifier que le plugin est bien configuré
mvn help:describe -Dplugin=org.jacoco:jacoco-maven-plugin

# Forcer la génération
mvn clean test jacoco:report -X
```

### La couverture minimale échoue
Le seuil est actuellement à 50%. Pour l'ajuster, modifier `pom.xml`:
```xml
<minimum>0.60</minimum>  <!-- Pour 60% -->
```

## 📦 Intégration CI/CD

Pour Jenkins/GitLab CI, ajoutez:
```bash
mvn clean test jacoco:report
# Publier: target/jacoco-reports/jacoco.xml
```

## 🎯 Objectifs de couverture recommandés

- **Code critique (Services, Logique métier):** 70-80%
- **Contrôleurs, DTOs:** 50-70%
- **Entités, Configurations:** 20-50%
- **Code généré (Lombok, etc.):** Exclure de la couverture
