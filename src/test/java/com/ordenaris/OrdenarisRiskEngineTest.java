package com.ordenaris;

import com.ordenaris.model.*;
import com.ordenaris.provider.*;
import com.ordenaris.exception.RiskEvaluationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdenarisRiskEngineTest {
    
    @Mock
    private DatosContablesProvider datosProvider;
    
    @Mock
    private HistorialPagosProvider historialProvider;
    
    @Mock
    private VerificacionLegalProvider legalProvider;
    
    private OrdenarisRiskEngine engine;
    
    @BeforeEach
    void setUp() {
        engine = new OrdenarisRiskEngine(datosProvider, historialProvider, legalProvider);
    }
    
    @Test
    void testDeudaActiva_ShouldReject() {
        // Given
        CreditRequest request = createBaseRequest();
        when(datosProvider.obtenerDatosContables(anyString()))
            .thenReturn(Optional.of(createGoodDatosContables()));
        when(historialProvider.obtenerHistorialPagos(anyString()))
            .thenReturn(Optional.of(new HistorialPagosProvider.HistorialPagos(true, 5, false)));
        when(legalProvider.obtenerVerificacionLegal(anyString()))
            .thenReturn(Optional.of(createCleanLegalStatus()));
        
        // When
        EvaluationResult result = engine.evaluateRisk(request);
        
        // Then
        assertThat(result.getRiesgoFinal()).isEqualTo(RiskLevel.RECHAZADO);
        assertThat(result.getMotivoFinal()).contains("Deuda Activa");
    }
    
    @Test
    void testDemandaLegal_ShouldBeHigh() {
        // Given
        CreditRequest request = createBaseRequest();
        when(datosProvider.obtenerDatosContables(anyString()))
            .thenReturn(Optional.of(createGoodDatosContables()));
        when(historialProvider.obtenerHistorialPagos(anyString()))
            .thenReturn(Optional.of(createGoodHistorial()));
        when(legalProvider.obtenerVerificacionLegal(anyString()))
            .thenReturn(Optional.of(new VerificacionLegalProvider.VerificacionLegal(true, true, false)));
        
        // When
        EvaluationResult result = engine.evaluateRisk(request);
        
        // Then
        assertThat(result.getRiesgoFinal()).isEqualTo(RiskLevel.ALTO);
    }
    
    @Test
    void testEmpresaNueva_RiskShouldBeAtLeastMedium() {
        // Given
        CreditRequest request = createBaseRequest();
        when(datosProvider.obtenerDatosContables(anyString()))
            .thenReturn(Optional.of(new DatosContablesProvider.DatosContables(
                new BigDecimal("100000"), new BigDecimal("500000"), 
                new BigDecimal("1000000"), 12
            )));
        when(historialProvider.obtenerHistorialPagos(anyString()))
            .thenReturn(Optional.of(createGoodHistorial()));
        when(legalProvider.obtenerVerificacionLegal(anyString()))
            .thenReturn(Optional.of(createCleanLegalStatus()));
        
        // When
        EvaluationResult result = engine.evaluateRisk(request);
        
        // Then
        assertThat(result.getRiesgoFinal()).isIn(RiskLevel.MEDIO, RiskLevel.ALTO);
    }
    
    @Test
    void testHistorialExcelente_ShouldLowerRisk() {
        // Given
        CreditRequest request = createBaseRequest();
        when(datosProvider.obtenerDatosContables(anyString()))
            .thenReturn(Optional.of(createGoodDatosContables()));
        when(historialProvider.obtenerHistorialPagos(anyString()))
            .thenReturn(Optional.of(new HistorialPagosProvider.HistorialPagos(false, 12, false)));
        when(legalProvider.obtenerVerificacionLegal(anyString()))
            .thenReturn(Optional.of(createCleanLegalStatus()));
        
        // When
        EvaluationResult result = engine.evaluateRisk(request);
        
        // Then
        assertThat(result.getRiesgoFinal()).isEqualTo(RiskLevel.BAJO);
    }
    
    @Test
    void testAltaSolicitudVsVentas_ShouldBeHigh() {
        // Given
        CreditRequest request = new CreditRequest(
            "EMPRESA_001",
            new BigDecimal("900000"),
            ProductoFinanciero.LINEA_OPERATIVA,
            LocalDate.now()
        );
        when(datosProvider.obtenerDatosContables(anyString()))
            .thenReturn(Optional.of(createGoodDatosContables()));
        when(historialProvider.obtenerHistorialPagos(anyString()))
            .thenReturn(Optional.of(createGoodHistorial()));
        when(legalProvider.obtenerVerificacionLegal(anyString()))
            .thenReturn(Optional.of(createCleanLegalStatus()));
        
        // When
        EvaluationResult result = engine.evaluateRisk(request);
        
        // Then
        assertThat(result.getRiesgoFinal()).isEqualTo(RiskLevel.ALTO);
    }
    
    @Test
    void testProductoEstricto_ShouldIncreaseRisk() {
        // Given
        CreditRequest request = new CreditRequest(
            "EMPRESA_001",
            new BigDecimal("500000"),
            ProductoFinanciero.ARRENDAMIENTO_FINANCIERO,
            LocalDate.now()
        );
        when(datosProvider.obtenerDatosContables(anyString()))
            .thenReturn(Optional.of(createGoodDatosContables()));
        when(historialProvider.obtenerHistorialPagos(anyString()))
            .thenReturn(Optional.of(createGoodHistorial()));
        when(legalProvider.obtenerVerificacionLegal(anyString()))
            .thenReturn(Optional.of(createCleanLegalStatus()));
        
        // When
        EvaluationResult result = engine.evaluateRisk(request);
        
        // Then
        // Riesgo base BAJO + producto estricto = MEDIO, luego historial excelente baja a BAJO
        assertThat(result.getRiesgoFinal()).isEqualTo(RiskLevel.BAJO);
    }
    
    @Test
    void testMultipleRules_CombinedEffect() {
        // Given: Empresa nueva con demanda legal
        CreditRequest request = createBaseRequest();
        when(datosProvider.obtenerDatosContables(anyString()))
            .thenReturn(Optional.of(new DatosContablesProvider.DatosContables(
                new BigDecimal("100000"), new BigDecimal("500000"), 
                new BigDecimal("1000000"), 12
            )));
        when(historialProvider.obtenerHistorialPagos(anyString()))
            .thenReturn(Optional.of(createGoodHistorial()));
        when(legalProvider.obtenerVerificacionLegal(anyString()))
            .thenReturn(Optional.of(new VerificacionLegalProvider.VerificacionLegal(true, false, false)));
        
        // When
        EvaluationResult result = engine.evaluateRisk(request);
        
        // Then
        assertThat(result.getRiesgoFinal()).isEqualTo(RiskLevel.ALTO);
        assertThat(result.getMotivoFinal()).contains("Demanda Legal Abierta");
    }
    
    @Test
    void testMissingDatosContables_ShouldThrowException() {
        // Given
        CreditRequest request = createBaseRequest();
        when(datosProvider.obtenerDatosContables(anyString()))
            .thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> engine.evaluateRisk(request))
            .isInstanceOf(RiskEvaluationException.class)
            .hasMessageContaining("No se pudieron obtener datos contables");
    }
    
    private CreditRequest createBaseRequest() {
        return new CreditRequest(
            "EMPRESA_001",
            new BigDecimal("500000"),
            ProductoFinanciero.LINEA_OPERATIVA,
            LocalDate.now()
        );
    }
    
    private DatosContablesProvider.DatosContables createGoodDatosContables() {
        return new DatosContablesProvider.DatosContables(
            new BigDecimal("100000"), new BigDecimal("500000"), 
            new BigDecimal("1000000"), 36
        );
    }
    
    private HistorialPagosProvider.HistorialPagos createGoodHistorial() {
        return new HistorialPagosProvider.HistorialPagos(false, 12, false);
    }
    
    private VerificacionLegalProvider.VerificacionLegal createCleanLegalStatus() {
        return new VerificacionLegalProvider.VerificacionLegal(false, false, false);
    }
}
