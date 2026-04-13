package com.ordenaris.rule;

import com.ordenaris.model.CreditRequest;
import com.ordenaris.model.RiskLevel;
import com.ordenaris.provider.*;

import java.util.Optional;

/**
 * Regla: Si existe un juicio mercantil en curso, riesgo ALTO automáticamente
 */
public class DemandaLegalAbiertaRule implements RiskRule {
    
    @Override
    public Optional<RiskLevel> evaluate(CreditRequest request,
                                        DatosContablesProvider.DatosContables datosContables,
                                        HistorialPagosProvider.HistorialPagos historialPagos,
                                        VerificacionLegalProvider.VerificacionLegal verificacionLegal) {
        if (verificacionLegal != null && verificacionLegal.tieneDemandaLegalAbierta()) {
            return Optional.of(RiskLevel.ALTO);
        }
        return Optional.empty();
    }
    
    @Override
    public String getRuleName() {
        return "Demanda Legal Abierta";
    }
    
    @Override
    public String getDescription() {
        return "Si existe un juicio mercantil en curso, riesgo ALTO automáticamente";
    }
    
    @Override
    public int getPriority() {
        return 90; // Alta prioridad
    }
}
