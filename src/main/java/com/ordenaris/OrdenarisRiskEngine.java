package com.ordenaris;

import com.ordenaris.exception.RiskEvaluationException;
import com.ordenaris.model.*;
import com.ordenaris.provider.*;
import com.ordenaris.rule.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Motor principal de evaluación de riesgo crediticio
 * Diseñado para ser extensible y permitir agregar nuevas reglas fácilmente
 */
public class OrdenarisRiskEngine {
    
    private static final Logger logger = LoggerFactory.getLogger(OrdenarisRiskEngine.class);
    
    private final DatosContablesProvider datosContablesProvider;
    private final HistorialPagosProvider historialPagosProvider;
    private final VerificacionLegalProvider verificacionLegalProvider;
    private final List<RiskRule> rules;
    
    public OrdenarisRiskEngine(DatosContablesProvider datosContablesProvider,
                               HistorialPagosProvider historialPagosProvider,
                               VerificacionLegalProvider verificacionLegalProvider) {
        this.datosContablesProvider = Objects.requireNonNull(datosContablesProvider, "datosContablesProvider no puede ser nulo");
        this.historialPagosProvider = Objects.requireNonNull(historialPagosProvider, "historialPagosProvider no puede ser nulo");
        this.verificacionLegalProvider = Objects.requireNonNull(verificacionLegalProvider, "verificacionLegalProvider no puede ser nulo");
        this.rules = initializeRules();
    }
    
    private List<RiskRule> initializeRules() {
        List<RiskRule> allRules = List.of(
            new DeudaActivaRule(),
            new DemandaLegalAbiertaRule(),
            new AltaSolicitudVsVentasRule(),
            new EmpresaNuevaRule(),
            new ProductoEstrictoRule(),
            new HistorialExcelenteRule()
        );
        
        // Ordenar por prioridad (mayor prioridad primero)
        return allRules.stream()
            .sorted((r1, r2) -> Integer.compare(r2.getPriority(), r1.getPriority()))
            .toList();
    }
    
    /**
     * Evalúa el riesgo crediticio de una solicitud
     * @param request Solicitud de crédito
     * @return Resultado completo de la evaluación
     * @throws RiskEvaluationException Si hay error en la evaluación
     */
    public EvaluationResult evaluateRisk(CreditRequest request) {
        logger.info("Evaluando riesgo para empresa: {}", request.empresaId());
        
        try {
            // Obtener datos de proveedores
            var datosContables = datosContablesProvider.obtenerDatosContables(request.empresaId())
                .orElseThrow(() -> new RiskEvaluationException("No se pudieron obtener datos contables para: " + request.empresaId()));
            
            var historialPagos = historialPagosProvider.obtenerHistorialPagos(request.empresaId())
                .orElse(null);
            
            var verificacionLegal = verificacionLegalProvider.obtenerVerificacionLegal(request.empresaId())
                .orElse(null);
            
            // Nivel de riesgo inicial
            RiskLevel currentRisk = RiskLevel.BAJO;
            RiskLevel minRiskFloor = RiskLevel.BAJO;
            List<String> motivos = new ArrayList<>();
            EvaluationResult.Builder resultBuilder = new EvaluationResult.Builder();
            
            // Evaluar cada regla
            for (RiskRule rule : rules) {
                Optional<RiskLevel> ruleResult = rule.evaluate(request, datosContables, historialPagos, verificacionLegal);
                
                if (ruleResult.isPresent()) {
                    RiskLevel newRisk = ruleResult.get();
                    
                    // Reglas especiales
                    if (rule instanceof HistorialExcelenteRule) {
                        RiskLevel before = currentRisk;
                        RiskLevel lowered = currentRisk.disminuirNivel();
                        if (lowered.getLevel() >= minRiskFloor.getLevel()) {
                            currentRisk = lowered;
                            motivos.add(String.format("%s - Se baja un nivel de riesgo de %s a %s", 
                                rule.getRuleName(), before, currentRisk));
                            resultBuilder.addReglaEvaluada(rule.getRuleName(), true, 
                                rule.getDescription() + " - Riesgo bajó de " + before + " a " + currentRisk, currentRisk);
                        } else {
                            motivos.add(String.format("%s - Historial excelente no puede bajar el riesgo por debajo del mínimo (%s)", 
                                rule.getRuleName(), minRiskFloor));
                            resultBuilder.addReglaEvaluada(rule.getRuleName(), true, 
                                rule.getDescription() + " - No se aplicó reducción por límite mínimo", currentRisk);
                        }
                    } 
                    else if (rule instanceof ProductoEstrictoRule) {
                        RiskLevel before = currentRisk;
                        currentRisk = currentRisk.aumentarNivel();
                        motivos.add(String.format("%s - Se aumenta un nivel de riesgo de %s a %s", 
                            rule.getRuleName(), before, currentRisk));
                        resultBuilder.addReglaEvaluada(rule.getRuleName(), true, 
                            rule.getDescription() + " - Riesgo subió de " + before + " a " + currentRisk, currentRisk);
                    }
                    else {
                        // Reglas que fijan un nivel específico
                        if (newRisk.getLevel() > currentRisk.getLevel()) {
                            currentRisk = newRisk;
                            motivos.add(rule.getRuleName() + " - Riesgo establecido en " + currentRisk);
                        }
                        if (newRisk.getLevel() > minRiskFloor.getLevel()) {
                            minRiskFloor = newRisk;
                        }
                        resultBuilder.addReglaEvaluada(rule.getRuleName(), true, 
                            rule.getDescription() + " - Riesgo: " + newRisk, newRisk);
                    }
                } else {
                    resultBuilder.addReglaEvaluada(rule.getRuleName(), false, 
                        rule.getDescription() + " - No aplica", currentRisk);
                }
                
                // Short-circuit para RECHAZADO
                if (currentRisk == RiskLevel.RECHAZADO) {
                    logger.info("Empresa rechazada por regla: {}", motivos.get(motivos.size() - 1));
                    break;
                }
            }
            
            // Construir resultado final
            String motivoFinal = motivos.isEmpty() ? "No aplicaron reglas, riesgo BAJO por defecto" 
                                  : String.join("; ", motivos);
            
            EvaluationResult result = resultBuilder
                .riesgoFinal(currentRisk)
                .motivoFinal(motivoFinal)
                .build();
            
            logger.info("Evaluación completada - Riesgo final: {}", currentRisk);
            return result;
            
        } catch (RiskEvaluationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error evaluando riesgo para empresa: {}", request.empresaId(), e);
            throw new RiskEvaluationException("Error durante la evaluación de riesgo", e);
        }
    }
    
    /**
     * Obtiene la lista de reglas configuradas
     * @return Lista inmutable de reglas
     */
    public List<RiskRule> getRules() {
        return Collections.unmodifiableList(rules);
    }
}
