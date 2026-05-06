# 🧪 Guide Complet des Tests Unitaires avec JaCoCo

## 📚 Exemple Pratique: CarePlanService

### Avant d'ajouter JaCoCo

#### Service à tester:
```java
public class CarePlanService {
    
    private CarePlanRepository repository;
    private UserServiceClient userServiceClient;
    
    public CarePlan createCarePlan(CarePlan plan) {
        if (plan == null) {
            throw new IllegalArgumentException("Plan cannot be null");
        }
        
        UserDto user = userServiceClient.getUserById(plan.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        return repository.save(plan);
    }
    
    public void deleteCarePlan(Long planId) {
        CarePlan plan = repository.findById(planId)
            .orElseThrow(() -> new NotFoundException("Plan not found"));
        repository.delete(plan);
    }
}
```

### Test Initial (Faible Couverture)

```java
@ExtendWith(MockitoExtension.class)
class CarePlanServiceTest {
    
    @Mock
    private CarePlanRepository repository;
    
    @Mock
    private UserServiceClient userServiceClient;
    
    @InjectMocks
    private CarePlanService service;
    
    // ❌ Seul le cas "succès" est testé
    @Test
    void testCreateCarePlan() {
        // Arrange
        CarePlan plan = new CarePlan();
        plan.setId(1L);
        plan.setUserId(100L);
        
        UserDto user = new UserDto();
        user.setId(100L);
        
        when(userServiceClient.getUserById(100L)).thenReturn(user);
        when(repository.save(any())).thenReturn(plan);
        
        // Act
        CarePlan result = service.createCarePlan(plan);
        
        // Assert
        assertEquals(1L, result.getId());
        verify(repository).save(plan);
    }
    
    // ❌ Cas d'erreur pas testé
    // ❌ Méthode deleteCarePlan pas testée du tout
}
```

**Couverture attendue: 40%** (seul le chemin "heureux" est exécuté)

---

### Test Amélioré (Couverture Complète)

```java
@ExtendWith(MockitoExtension.class)
class CarePlanServiceTest {
    
    @Mock
    private CarePlanRepository repository;
    
    @Mock
    private UserServiceClient userServiceClient;
    
    @InjectMocks
    private CarePlanService service;
    
    // ✅ Cas de succès
    @Test
    void shouldCreateCarePlanSuccessfully() {
        // Arrange
        CarePlan plan = new CarePlan();
        plan.setId(1L);
        plan.setUserId(100L);
        
        UserDto user = new UserDto();
        user.setId(100L);
        
        when(userServiceClient.getUserById(100L)).thenReturn(user);
        when(repository.save(any())).thenReturn(plan);
        
        // Act
        CarePlan result = service.createCarePlan(plan);
        
        // Assert
        assertEquals(1L, result.getId());
        verify(repository).save(plan);
    }
    
    // ✅ Cas: Plan null
    @Test
    void shouldThrowExceptionWhenPlanIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> service.createCarePlan(null));
        
        // Vérifier que le repository n'a pas été appelé
        verify(repository, never()).save(any());
    }
    
    // ✅ Cas: User non trouvé
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        CarePlan plan = new CarePlan();
        plan.setUserId(999L);
        
        when(userServiceClient.getUserById(999L)).thenReturn(null);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> service.createCarePlan(plan));
        
        verify(repository, never()).save(any());
    }
    
    // ✅ Cas de succès pour deleteCarePlan
    @Test
    void shouldDeleteCarePlanSuccessfully() {
        // Arrange
        CarePlan plan = new CarePlan();
        plan.setId(1L);
        
        when(repository.findById(1L)).thenReturn(Optional.of(plan));
        
        // Act
        service.deleteCarePlan(1L);
        
        // Assert
        verify(repository).delete(plan);
    }
    
    // ✅ Cas: Plan non trouvé dans deleteCarePlan
    @Test
    void shouldThrowExceptionWhenDeletingNonExistentPlan() {
        // Arrange
        when(repository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(NotFoundException.class, 
            () -> service.deleteCarePlan(999L));
        
        verify(repository, never()).delete(any());
    }
}
```

**Couverture attendue: 95%** (tous les chemins testés)

---

## 🎯 Structure d'un Test Parfait (AAA Pattern)

```java
@Test
void shouldDoSomethingWhenConditionIsMet() {
    // ✅ Arrange (Préparation)
    // - Créer les objets
    // - Configurer les mocks
    // - Configurer les données
    
    CarePlan plan = new CarePlan();
    plan.setId(1L);
    when(repository.save(any())).thenReturn(plan);
    
    // ✅ Act (Action)
    // - Appeler la méthode à tester
    
    CarePlan result = service.createCarePlan(plan);
    
    // ✅ Assert (Vérification)
    // - Vérifier le résultat
    // - Vérifier les appels aux mocks
    
    assertNotNull(result);
    assertEquals(1L, result.getId());
    verify(repository).save(plan);
}
```

---

## 🔍 Checklist pour Chaque Méthode

Pour chaque méthode, tester:

### 1. Cas de Succès ("Happy Path")
```java
@Test
void shouldReturnSuccessfulResult() {
    // Vérifier le comportement normal
}
```

### 2. Cas d'Erreur
```java
@Test
void shouldThrowExceptionWhenInputIsNull() {
    assertThrows(IllegalArgumentException.class, () -> service.method(null));
}

@Test
void shouldThrowExceptionWhenResourceNotFound() {
    when(repository.findById(999L)).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> service.method(999L));
}
```

### 3. Cas Limites ("Edge Cases")
```java
@Test
void shouldHandleEmptyList() {
    when(repository.findAll()).thenReturn(Collections.emptyList());
    List<CarePlan> result = service.getAllPlans();
    assertEquals(0, result.size());
}

@Test
void shouldHandleSpecialCharacters() {
    CarePlan plan = new CarePlan();
    plan.setName("Test@#$%^&*()");
    service.createCarePlan(plan);
    verify(repository).save(any());
}
```

### 4. Interactions avec Dépendances
```java
@Test
void shouldCallUserServiceClient() {
    CarePlan plan = new CarePlan();
    plan.setUserId(1L);
    
    service.createCarePlan(plan);
    
    verify(userServiceClient).getUserById(1L);
}
```

---

## 🚀 Commandes pour Améliorer la Couverture

### 1. Exécuter les tests et voir quels chemins ne sont pas testés
```bash
mvn clean test
open target/jacoco-reports/index.html
```

### 2. Exécuter un test spécifique
```bash
mvn test -Dtest=CarePlanServiceTest#shouldCreateCarePlan
```

### 3. Mode verbose pour voir chaque assertion
```bash
mvn test -Dtest=CarePlanServiceTest -X
```

---

## 📊 Matrice de Test

Créer une matrice pour chaque service:

| Méthode | Cas Succès | Null Input | Not Found | Edge Cases | Mocks Vérifiés |
|---------|-----------|-----------|----------|-----------|----------------|
| `createCarePlan()` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `deleteCarePlan()` | ✅ | ✅ | ✅ | ❌ | ✅ |
| `getCarePlan()` | ✅ | ❌ | ✅ | ❌ | ❌ |

**Légende:** ✅ = Testé, ❌ = À tester

---

## 🎓 Bonnes Pratiques

### ✅ À faire
```java
// Noms clairs
@Test
void shouldThrowExceptionWhenCarePlanIsNull() { }

// Une assertion principale par test
@Test
void shouldReturnCarePlan() {
    CarePlan result = service.getCarePlan(1L);
    assertEquals(1L, result.getId());  // Assertion principale
}

// Utiliser les assertions spécifiques
assertEquals(expected, actual);
assertNotNull(result);
assertThrows(Exception.class, () -> { });
```

### ❌ À éviter
```java
// Noms vagues
@Test
void testCarePlan() { }

// Plusieurs assertions sans relation
@Test
void shouldWork() {
    assertTrue(a > 0);
    assertFalse(b < 0);
    assertEquals(c, d);
    assertNull(e);
}

// Tester plusieurs choses à la fois
@Test
void shouldCreateAndReturnAndSaveCarePlan() { }
```

---

## 📈 Progression Recommandée

### Semaine 1: Coverage 50%
- Tester tous les cas de succès
- Commencer les tests d'erreur basiques

### Semaine 2: Coverage 65%
- Ajouter tous les cas d'erreur
- Tester les interactions avec mocks

### Semaine 3+: Coverage 75%+
- Ajouter les edge cases
- Améliorer la qualité des assertions

---

## 🔗 Ressources

- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)

---

**Objectif Final:** Atteindre 70%+ de couverture dans les services critiques! 🎯
