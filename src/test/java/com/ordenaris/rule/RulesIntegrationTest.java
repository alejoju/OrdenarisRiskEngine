package com.ordenaris.rule;

import com.ordenaris.model.*;
import com.ordenaris.provider.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RulesIntegrationTest {
    
    private CreditRequest baseRequest;
    private DatosContablesProvider.DatosContables datosContables;
    private HistorialPagosProvider.HistorialPagos historialPagos;
    private VerificacionLegalProvider.VerificacionLegal verificacionLegal;
    
    @BeforeEach
    void setUp() {
        baseRequest = new CreditRequest(
            "TEST",
            new BigDecimal("500000"),
            ProductoFinanciero.LINEA_OPERATIVA,
            LocalDate.now()
        );
        
        datosContables = new DatosContablesProvider.DatosContables(
            new BigDecimal("100000"),
            new BigDecimal("500000"),
            new BigDecimal("1000000"),
            36
        );
        
        historialPagos = new HistorialPagosProvider.HistorialPagos(false, 12, false);
        verificacionLegal = new VerificacionLegalProvider.VerificacionLegal(false, false, false);
    }
    
    @Test
    void testDeudaActivaRule() {
        DeudaActivaRule rule = new DeudaActivaRule();
        var historialMalo = new HistorialPagosProvider.HistorialPagos(true, 0, false);
        
        Optional<RiskLevel> result = rule.evaluate(baseRequest, datosContables, historialMalo, verificacionLegal);
        
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(RiskLevel.RECHAZADO);
    }
    
    @Test
    void testDemandaLegalRule() {
        DemandaLegalAbiertaRule rule = new DemandaLegalAbiertaRule();
        var legalConProblemas = new VerificacionLegalProvider.VerificacionLegal(true, false, false);
        
        Optional<RiskLevel> result = rule.evaluate(baseRequest, datosContables, historialPagos, legalConProblemas);
        
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(RiskLevel.ALTO);
    }
    
    @Test
    void testAltaSolicitudRule() {
        AltaSolicitudVsVentasRule rule = new AltaSolicitudVsVentasRule();
        var requestAlto = new CreditRequest(
            "TEST",
            new BigDecimal("900000"),
            ProductoFinanciero.LINEA_OPERATIVA,
            LocalDate.now()
        );
        
        Optional<RiskLevel> result = rule.evaluate(requestAlto, datosContables, historialPagos, verificacionLegal);
        
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(RiskLevel.ALTO);
    }
    
    @Test
    void testEmpresaNuevaRule() {
        EmpresaNuevaRule rule = new EmpresaNuevaRule();
        var datosEmpresaNueva = new DatosContablesProvider.DatosContables(
            new BigDecimal("100000"),
            new BigDecimal("500000"),
            new BigDecimal("1000000"),
            12
        );
        
        Optional<RiskLevel> result = rule.evaluate(baseRequest, datosEmpresaNueva, historialPagos, verificacionLegal);
        
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(RiskLevel.MEDIO);
    }
    
    @Test
    void testProductoEstrictoRule() {
        ProductoEstrictoRule rule = new ProductoEstrictoRule();
        var requestArrendamiento = new CreditRequest(
            "TEST",
            new BigDecimal("500000"),
            ProductoFinanciero.ARRENDAMIENTO_FINANCIERO,
            LocalDate.now()
        );
        
        Optional<RiskLevel> result = rule.evaluate(requestArrendamiento, datosContables, historialPagos, verificacionLegal);
        
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(RiskLevel.MEDIO);
    }
    
    @Test
    void testHistorialExcelenteRule() {
        HistorialExcelenteRule rule = new HistorialExcelenteRule();
        
        Optional<RiskLevel> result = rule.evaluate(baseRequest, datosContables, historialPagos, verificacionLegal);
        
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(RiskLevel.BAJO);
    }
    
    @Test
    void testRulePriorities() {
        DeudaActivaRule deudaRule = new DeudaActivaRule();
        DemandaLegalAbiertaRule legalRule = new DemandaLegalAbiertaRule();
        
        assertThat(deudaRule.getPriority()).isGreaterThan(legalRule.getPriority());
        assertThat(legalRule.getPriority()).isGreaterThan(0);
    }
}
