package com.ordenaris.model;

/**
 * Niveles de riesgo crediticio
 */
public enum RiskLevel {
    BAJO(1),
    MEDIO(2),
    ALTO(3),
    RECHAZADO(4);
    
    private final int level;
    
    RiskLevel(int level) {
        this.level = level;
    }
    
    public int getLevel() {
        return level;
    }
    
    /**
     * Aumenta el nivel de riesgo en 1 punto
     * @return Nuevo nivel de riesgo aumentado
     */
    public RiskLevel aumentarNivel() {
        return switch (this) {
            case BAJO -> MEDIO;
            case MEDIO -> ALTO;
            case ALTO, RECHAZADO -> RECHAZADO;
        };
    }
    
    /**
     * Disminuye el nivel de riesgo en 1 punto
     * @return Nuevo nivel de riesgo disminuido
     */
    public RiskLevel disminuirNivel() {
        return switch (this) {
            case ALTO -> MEDIO;
            case MEDIO -> BAJO;
            case BAJO, RECHAZADO -> BAJO;
        };
    }
}
