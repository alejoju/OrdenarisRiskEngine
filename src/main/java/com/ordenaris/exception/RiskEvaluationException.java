package com.ordenaris.exception;

/**
 * Excepción personalizada para errores en la evaluación de riesgo
 */
public class RiskEvaluationException extends RuntimeException {
    
    public RiskEvaluationException(String message) {
        super(message);
    }
    
    public RiskEvaluationException(String message, Throwable cause) {
        super(message, cause);
    }
}
