# Evaluación T1 - Pruebas de Software (Cibertec)

**Curso:** 2424 – Pruebas de Software  
**Ciclo:** Sexto | **Sección:** T6DO  
**Estudiante:** Angela Barrenechea  

---

## 📌 Descripción del Proyecto

Este repositorio contiene la solución completa de la **Primera Evaluación (T1)** del curso de Pruebas de Software, desarrollada en **Java 21** con **Maven**, implementando pruebas unitarias con **JUnit 5** y **Mockito**.

---

## 📂 Estructura del Proyecto

```text
src/
├── main/java/com/cibertec/
│   ├── loyalty/
│   │   ├── LoyaltyRepository.java          # Interfaz de persistencia de fidelización
│   │   ├── LoyaltyResponse.java            # Record de respuesta DTO
│   │   ├── LoyaltyServiceImpl.java         # Servicio de cálculo y asignación de puntos
│   │   └── service/
│   │       └── LoyaltyPointsCalculator.java# Calculadora de puntos (Pregunta 1)
│   ├── rental/
│   │   └── service/
│   │       └── PenaltyCalculator.java      # Calculadora de penalizaciones (Pregunta 2)
│   └── subscription/
│       ├── ExpiredCardException.java       # Excepción de tarjeta expirada
│       ├── PaymentGatewayClient.java       # Interfaz pasarela de pagos
│       ├── Subscription.java               # Record de suscripción
│       ├── SubscriptionRepository.java     # Interfaz repositorio de suscripciones
│       └── SubscriptionServiceImpl.java    # Servicio de renovación de suscripciones
└── test/java/com/cibertec/
    ├── loyalty/
    │   ├── LoyaltyServiceImplTest.java     # Pruebas con Mockito (Pregunta 3)
    │   └── service/
    │       └── LoyaltyPointsCalculatorTest.java # Pruebas con JUnit 5 (Pregunta 1)
    ├── rental/
    │   └── service/
    │       └── PenaltyCalculatorTest.java  # Pruebas con JUnit 5 (Pregunta 2)
    └── subscription/
        └── SubscriptionServiceImplTest.java# Pruebas con Mockito (Pregunta 4)
```

---

## 🧪 Resumen de Preguntas y Cobertura de Pruebas

### Pregunta 1: Sistema de Recompensas y Fidelización (E-commerce)
- **Tecnología:** JUnit 5 (`org.junit.jupiter.api.*`).
- **Aserciones utilizadas:** `assertThrows` para validar errores de parámetros nulos/negativos/cero y `assertEquals` para cliente regular (1 punto por unidad) y cliente VIP (doble de puntos).
- **Cobertura:** 6 métodos de prueba con nombres descriptivos.

### Pregunta 2: Motor de Penalizaciones por Devoluciones Tardías (SaaS)
- **Tecnología:** JUnit 5 (`org.junit.jupiter.api.*`).
- **Aserciones utilizadas:** `assertThrows` y `assertEquals`.
- **Escenarios evaluados:**
  1. Días negativos lanza `IllegalStateException`.
  2. Devolución a tiempo (0 días) = $0.
  3. Retraso leve ($\le 7$ días) = $15/día (ej. 5 días = $75).
  4. Límite de 7 días exactos = $105.
  5. Retraso prolongado con activación del tope comercial de $300.

### Pregunta 3: Sistema de Puntos y Fidelización (E-commerce)
- **Tecnología:** JUnit 5 + Mockito (`@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`).
- **Verificaciones clave:**
  - `verify(loyaltyRepository, never()).addPoints(...)` ante montos inválidos o nulos.
  - `verify(loyaltyRepository).addPoints(userId, 300)` para cliente VIP validando la persistencia con puntaje duplicado.
  - Validación del retorno `LoyaltyResponse`.

### Pregunta 4: Renovación Automática de Suscripciones (SaaS)
- **Tecnología:** JUnit 5 + Mockito.
- **Verificaciones clave:**
  - **Tarjeta vencida:** Lanza `ExpiredCardException`, comprueba que se actualice a `"SUSPENDED"` mediante `verify(subscriptionRepository).updateStatus(...)` y garantiza cero cobros con `verify(paymentGatewayClient, never()).chargeMonthlyFee(...)`.
  - **Renovación exitosa:** Verifica cobro con `chargeMonthlyFee` y extensión exacta de fecha por un mes con `verify(subscriptionRepository).updateBillingDate(...)`.

---

## 🚀 Ejecución de Pruebas

Para compilar el proyecto y ejecutar toda la suite de pruebas:

```bash
mvn clean test
```

### Resultados de Ejecución
```text
[INFO] Running com.cibertec.loyalty.LoyaltyServiceImplTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.cibertec.loyalty.service.LoyaltyPointsCalculatorTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.cibertec.rental.service.PenaltyCalculatorTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.cibertec.subscription.SubscriptionServiceImplTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] Tests run: 17, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
```
