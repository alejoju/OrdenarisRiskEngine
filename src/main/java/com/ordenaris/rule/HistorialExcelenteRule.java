package com.ordenaris.rule;

import com.ordenaris.model.CreditRequest;
import com.ordenaris.model.RiskLevel;
import com.ordenaris.provider.*;

import java.util.Optional;

/**
 * Regla: Si los últimos 12 pagos fueron en tiempo y sin refinanciamiento, se baja un nivel de riesgo
 */
public class HistorialExcelenteRule implements RiskRule {
    
    @Override
    public Optional<RiskLevel> evaluate(CreditRequest request,
                                        DatosContablesProvider.DatosContables datosContables,
                                        HistorialPagosProvider.HistorialPagos historialPagos,
                                        VerificacionLegalProvider.VerificacionLegal verificacionLegal) {
        if (historialPagos != null && historialPagos.tieneHistorialExcelente()) {
            return Optional.of(RiskLevel.BAJO);
        }
        return Optional.empty();
    }
    
    @Override
    public String getRuleName() {
        return "Historial Excelente";
    }
    
    @Override
    public String getDescription() {
        return "Si los últimos 12 pagos fueron en tiempo y sin refinanciamiento, se baja un nivel de riesgo";
    }
}
