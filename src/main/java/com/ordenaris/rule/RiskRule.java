package com.ordenaris.rule;

import com.ordenaris.model.CreditRequest;
import com.ordenaris.model.RiskLevel;
import com.ordenaris.provider.*;

import java.util.Optional;

/**
 * Interfaz base para todas las reglas de evaluación de riesgo
 */
public interface RiskRule {
    /**
     * Evalúa la regla y retorna el resultado
     * @param request Solicitud de crédito
     * @param datosContables Datos contables de la empresa
     * @param historialPagos Historial de pagos
     * @param verificacionLegal Verificación legal
     * @return Optional con el nuevo nivel de riesgo si la regla aplica
     */
    Optional<RiskLevel> evaluate(
        CreditRequest request,
        DatosContablesProvider.DatosContables datosContables,
        HistorialPagosProvider.HistorialPagos historialPagos,
        VerificacionLegalProvider.VerificacionLegal verificacionLegal
    );
    
    /**
     * @return Nombre descriptivo de la regla
     */
    String getRuleName();
    
    /**
     * @return Descripción detallada de la regla
     */
    String getDescription();
    
    /**
     * Prioridad de ejecución (mayor número = mayor prioridad)
     * @return Prioridad de la regla
     */
    default int getPriority() {
        return 0;
    }
}
