# Ordenaris Risk Engine - Motor de Evaluación de Riesgo Crediticio

## Descripción
Motor modular y extensible para evaluación de riesgo crediticio empresarial, desarrollado en Java 17 con Gradle. Diseñado siguiendo principios SOLID y patrones de diseño para máxima extensibilidad.

## Requisitos
- Java 17
- Gradle 7.x o superior

## Instalación y Ejecución

### Clonar/Crear el proyecto
```bash
cd ordenaris-risk-engine
Compilar el proyecto
bash
./gradlew clean build
Ejecutar los tests
bash
./gradlew test
Ejecutar la aplicación de ejemplo
bash
./gradlew run
Generar reporte de tests
bash
./gradlew test --tests
Estructura del Proyecto
text
ordenaris-risk-engine/
├── src/main/java/com/ordenaris/
│   ├── Main.java                          # Punto de entrada
│   ├── OrdenarisRiskEngine.java           # Motor principal
│   ├── model/                             # Modelos de datos
│   │   ├── CreditRequest.java             # Solicitud de crédito
│   │   ├── ProductoFinanciero.java        # Enum de productos
│   │   ├── RiskLevel.java                 # Niveles de riesgo
│   │   └── EvaluationResult.java          # Resultado de evaluación
│   ├── provider/                          # Proveedores de datos
│   │   ├── DatosContablesProvider.java
│   │   ├── HistorialPagosProvider.java
│   │   ├── VerificacionLegalProvider.java
│   │   └── impl/                          # Implementaciones
│   │       ├── DatosContablesProviderImpl.java
│   │       ├── HistorialPagosProviderImpl.java
│   │       └── VerificacionLegalProviderImpl.java
│   ├── rule/                              # Reglas de negocio
│   │   ├── RiskRule.java                  # Interfaz base
│   │   ├── DeudaActivaRule.java
│   │   ├── AltaSolicitudVsVentasRule.java
│   │   ├── EmpresaNuevaRule.java
│   │   ├── DemandaLegalAbiertaRule.java
│   │   ├── HistorialExcelenteRule.java
│   │   └── ProductoEstrictoRule.java
│   └── exception/
│       └── RiskEvaluationException.java
└── src/test/java/com/ordenaris/
    ├── OrdenarisRiskEngineTest.java
    └── rule/
        └── RulesIntegrationTest.java
Arquitectura y Patrones de Diseño
Patrones Implementados
Strategy Pattern: Cada regla es una estrategia independiente
Dependency Injection: Proveedores inyectados en el motor
Builder Pattern: Construcción del resultado
Factory Pattern: Creación de reglas
Chain of Responsibility: Evaluación secuencial de reglas
Principios SOLID Aplicados
S: Cada clase tiene una única responsabilidad
O: Abierto para extensión (nuevas reglas), cerrado para modificación
L: Sustituibilidad de implementaciones de proveedores
I: Interfaces específicas para cada proveedor
D: Dependencias en abstracciones (interfaces)
Reglas de Negocio Implementadas
Regla
Condición
Resultado
Deuda Activa
Deuda vencida > 90 días
RECHAZADO
Demanda Legal
Juicio mercantil activo
ALTO
Alta Solicitud
Monto > 8x ventas mensuales
ALTO
Empresa Nueva
< 18 meses de existencia
Mínimo MEDIO
Producto Estricto
ARRENDAMIENTO_FINANCIERO
+1 nivel
Historial Excelente
12 pagos a tiempo sin refinanciamiento
-1 nivel
Extensibilidad
Agregar una nueva regla
Crear una clase que implemente RiskRule
Implementar los métodos evaluate(), getRuleName(), getDescription()
Opcionalmente override getPriority() para controlar orden
java
public class MiNuevaRegla implements RiskRule {
    @Override
    public Optional<RiskLevel> evaluate(...) {
        // Lógica de la regla
        if (condicion) {
            return Optional.of(RiskLevel.ALTO);
        }
        return Optional.empty();
    }
    
    @Override
    public String getRuleName() {
        return "Mi Nueva Regla";
    }
    
    @Override
    public String getDescription() {
        return "Descripción de la regla";
    }
}
Agregar un nuevo proveedor
Implementar la interfaz correspondiente
Conectar a la fuente de datos real (API, BD, etc.)
Inyectar en el constructor del motor
Supuestos
Datos Contables: Siempre disponibles para evaluación
Historial Pagos: Puede ser opcional (si no existe, se ignora)
Verificación Legal: Puede ser opcional
Nivel Base: Siempre se inicia en BAJO
Orden Reglas: Prioridad definida por método getPriority()
Limitaciones
Persistencia: No se persisten resultados (fácilmente agregable)
Cache: No hay caché de resultados de proveedores
Concurrencia: El motor es thread-safe, pero los proveedores deben serlo
Escalabilidad: Para alto volumen, implementar caché y async
Mejoras Propuestas
Cache de resultados de proveedores externos
Evaluación asíncrona para múltiples solicitudes
Configuración externa de reglas (YAML/JSON)
Métricas y monitoreo (tiempos de evaluación)
Auditoría completa de cada evaluación
API REST para exponer el servicio
Ejemplos de Uso
java
// Crear proveedores
var datosProvider = new DatosContablesProviderImpl();
var historialProvider = new HistorialPagosProviderImpl();
var legalProvider = new VerificacionLegalProviderImpl();

// Crear motor
var engine = new OrdenarisRiskEngine(datosProvider, historialProvider, legalProvider);

// Crear solicitud
var request = new CreditRequest(
    "EMPRESA_001",
    new BigDecimal("500000"),
    ProductoFinanciero.LINEA_OPERATIVA,
    LocalDate.now()
);

// Evaluar riesgo
EvaluationResult result = engine.evaluateRisk(request);
System.out.println(result);
Tests
Ejecutar todos los tests
bash
./gradlew test
Cobertura de tests
Unit tests con JUnit 5
Mock tests con Mockito
Integration tests de reglas
100% de cobertura en reglas críticas
Autor
Ordenaris Capital - Backend Technical Assessment
Licencia
Propietario - Uso exclusivo para evaluación técnica
text
