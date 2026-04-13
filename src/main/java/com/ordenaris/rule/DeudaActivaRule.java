package com.ordenaris.rule;

import com.ordenaris.model.CreditRequest;
import com.ordenaris.model.RiskLevel;
import com.ordenaris.provider.*;

import java.util.Optional;

/**
 * Regla: Si la empresa posee una deuda vencida > 90 días, se clasifica como RECHAZADO
 */
public class DeudaActivaRule implements RiskRule {
    
    @Override
    public Optional<RiskLevel> evaluate(CreditRequest request, 
                                        DatosContablesProvider.DatosContables datosContables,
                                        HistorialPagosProvider.HistorialPagos historialPagos,
                                        VerificacionLegalProvider.VerificacionLegal verificacionLegal) {
        if (historialPagos != null && historialPagos.tieneDeudaVencidaMas90Dias()) {
            return Optional.of(RiskLevel.RECHAZADO);
        }
        return Optional.empty();
    }
    
    @Override
    public String getRuleName() {
        return "Deuda Activa";
    }
    
    @Override
    public String getDescription() {
        return "Si la empresa posee una deuda vencida > 90 días, se clasifica como RECHAZADO";
    }
    
    @Override
    public int getPriority() {
        return 100; // Prioridad máxima
    }
}
