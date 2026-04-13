package com.ordenaris;

import com.ordenaris.model.*;
import com.ordenaris.provider.impl.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Clase principal para demostrar el funcionamiento del motor
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("=== ORDENARIS RISK ENGINE ===\n");
        
        // Inicializar proveedores
        var datosProvider = new DatosContablesProviderImpl();
        var historialProvider = new HistorialPagosProviderImpl();
        var legalProvider = new VerificacionLegalProviderImpl();
        
        // Crear motor
        var engine = new OrdenarisRiskEngine(datosProvider, historialProvider, legalProvider);
        
        // Caso 1: Empresa con excelente historial
        System.out.println("CASO 1: Empresa con excelente historial");
        var request1 = new CreditRequest(
            "EMPRESA_001",
            new BigDecimal("500000"),
            ProductoFinanciero.LINEA_OPERATIVA,
            LocalDate.now()
        );
        var result1 = engine.evaluateRisk(request1);
        System.out.println(result1);
        
        // Caso 2: Empresa nueva (riesgo debe ser al menos MEDIO)
        System.out.println("\nCASO 2: Empresa nueva (riesgo debe ser al menos MEDIO)");
        var request2 = new CreditRequest(
            "EMPRESA_002",
            new BigDecimal("300000"),
            ProductoFinanciero.CREDITO_REVOLVENTE,
            LocalDate.now()
        );
        var result2 = engine.evaluateRisk(request2);
        System.out.println(result2);
        
        // Caso 3: Empresa con problemas legales
        System.out.println("\nCASO 3: Empresa con demanda legal activa");
        var request3 = new CreditRequest(
            "EMPRESA_003",
            new BigDecimal("200000"),
            ProductoFinanciero.ARRENDAMIENTO_FINANCIERO,
            LocalDate.now()
        );
        var result3 = engine.evaluateRisk(request3);
        System.out.println(result3);
        
        // Caso 4: Solicitud muy alta vs ventas
        System.out.println("\nCASO 4: Monto solicitado muy alto vs ventas");
        var request4 = new CreditRequest(
            "EMPRESA_001",
            new BigDecimal("1000000"),
            ProductoFinanciero.LINEA_OPERATIVA,
            LocalDate.now()
        );
        var result4 = engine.evaluateRisk(request4);
        System.out.println(result4);
        
        // Caso 5: Empresa con deuda vencida
        System.out.println("\nCASO 5: Empresa con deuda vencida > 90 días");
        var request5 = new CreditRequest(
            "EMPRESA_003",
            new BigDecimal("50000"),
            ProductoFinanciero.LINEA_OPERATIVA,
            LocalDate.now()
        );
        var result5 = engine.evaluateRisk(request5);
        System.out.println(result5);
        
        // Caso 6: Empresa con ventas bajas solicitando arrendamiento
        System.out.println("\nCASO 6: Empresa con ventas bajas solicitando arrendamiento");
        var request6 = new CreditRequest(
            "EMPRESA_004",
            new BigDecimal("80000"),
            ProductoFinanciero.ARRENDAMIENTO_FINANCIERO,
            LocalDate.now()
        );
        var result6 = engine.evaluateRisk(request6);
        System.out.println(result6);
    }
}
