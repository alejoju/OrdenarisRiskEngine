package com.ordenaris.rule;

import com.ordenaris.model.CreditRequest;
import com.ordenaris.model.RiskLevel;
import com.ordenaris.provider.*;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Regla: Si el monto solicitado es > 8 veces el promedio mensual de ventas, el riesgo se clasifica como ALTO
 */
public class AltaSolicitudVsVentasRule implements RiskRule {
    
    private static final BigDecimal LIMITE_VECES_VENTAS = new BigDecimal("8");
    
    @Override
    public Optional<RiskLevel> evaluate(CreditRequest request,
                                        DatosContablesProvider.DatosContables datosContables,
                                        HistorialPagosProvider.HistorialPagos historialPagos,
                                        VerificacionLegalProvider.VerificacionLegal verificacionLegal) {
        if (datosContables != null && datosContables.ventasPromedioMensual().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal vecesVentas = request.montoSolicitado()
                .divide(datosContables.ventasPromedioMensual(), 2, BigDecimal.ROUND_HALF_UP);
            
            if (vecesVentas.compareTo(LIMITE_VECES_VENTAS) > 0) {
                return Optional.of(RiskLevel.ALTO);
            }
        }
        return Optional.empty();
    }
    
    @Override
    public String getRuleName() {
        return "Alta Solicitud vs Ventas";
    }
    
    @Override
    public String getDescription() {
        return "Si el monto solicitado es > 8 veces el promedio mensual de ventas, el riesgo se clasifica como ALTO";
    }
}
