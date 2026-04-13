package com.ordenaris.rule;

import com.ordenaris.model.CreditRequest;
import com.ordenaris.model.RiskLevel;
import com.ordenaris.provider.*;

import java.util.Optional;

/**
 * Regla: Si la empresa tiene menos de 18 meses de existencia, el riesgo no puede ser menor a MEDIO
 */
public class EmpresaNuevaRule implements RiskRule {
    
    private static final int MESES_MINIMOS = 18;
    
    @Override
    public Optional<RiskLevel> evaluate(CreditRequest request,
                                        DatosContablesProvider.DatosContables datosContables,
                                        HistorialPagosProvider.HistorialPagos historialPagos,
                                        VerificacionLegalProvider.VerificacionLegal verificacionLegal) {
        if (datosContables != null && datosContables.mesesDeExistencia() < MESES_MINIMOS) {
            return Optional.of(RiskLevel.MEDIO);
        }
        return Optional.empty();
    }
    
    @Override
    public String getRuleName() {
        return "Empresa Nueva";
    }
    
    @Override
    public String getDescription() {
        return "Si la empresa tiene menos de 18 meses de existencia, el riesgo no puede ser menor a MEDIO";
    }
}
