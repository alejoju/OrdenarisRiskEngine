package com.ordenaris.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Resultado completo de la evaluación de riesgo
 */
public class EvaluationResult {
    private final RiskLevel riesgoFinal;
    private final List<RuleEvaluation> reglasEvaluadas;
    private final String motivoFinal;
    
    private EvaluationResult(Builder builder) {
        this.riesgoFinal = builder.riesgoFinal;
        this.reglasEvaluadas = List.copyOf(builder.reglasEvaluadas);
        this.motivoFinal = builder.motivoFinal;
    }
    
    public RiskLevel getRiesgoFinal() {
        return riesgoFinal;
    }
    
    public List<RuleEvaluation> getReglasEvaluadas() {
        return reglasEvaluadas;
    }
    
    public String getMotivoFinal() {
        return motivoFinal;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========== RESULTADO EVALUACIÓN ==========\n");
        sb.append("Riesgo Final: ").append(riesgoFinal).append("\n");
        sb.append("Motivo: ").append(motivoFinal).append("\n");
        sb.append("\nReglas Evaluadas:\n");
        for (RuleEvaluation rule : reglasEvaluadas) {
            sb.append("  • ").append(rule.nombreRegla()).append(": ");
            sb.append(rule.aplico() ? "✓ APLICÓ" : "✗ NO APLICÓ").append("\n");
            sb.append("    ").append(rule.descripcion()).append("\n");
        }
        sb.append("=========================================\n");
        return sb.toString();
    }
    
    public static class Builder {
        private RiskLevel riesgoFinal;
        private final List<RuleEvaluation> reglasEvaluadas = new ArrayList<>();
        private String motivoFinal;
        
        public Builder riesgoFinal(RiskLevel riesgoFinal) {
            this.riesgoFinal = riesgoFinal;
            return this;
        }
        
        public Builder addReglaEvaluada(String nombre, boolean aplico, String descripcion, RiskLevel riesgoResultante) {
            reglasEvaluadas.add(new RuleEvaluation(nombre, aplico, descripcion, riesgoResultante));
            return this;
        }
        
        public Builder motivoFinal(String motivoFinal) {
            this.motivoFinal = motivoFinal;
            return this;
        }
        
        public EvaluationResult build() {
            return new EvaluationResult(this);
        }
    }
    
    public record RuleEvaluation(
        String nombreRegla,
        boolean aplico,
        String descripcion,
        RiskLevel riesgoResultante
    ) {}
}
