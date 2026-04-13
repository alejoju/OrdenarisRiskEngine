package com.ordenaris.provider;

import java.util.Optional;

/**
 * Proveedor de verificación legal de la empresa
 */
public interface VerificacionLegalProvider {
    Optional<VerificacionLegal> obtenerVerificacionLegal(String empresaId);
    
    record VerificacionLegal(
        boolean tieneJuicioMercantilActivo,
        boolean tieneDemandas,
        boolean tieneEmbargos
    ) {
        public boolean tieneDemandaLegalAbierta() {
            return tieneJuicioMercantilActivo || tieneDemandas;
        }
        
        public boolean tieneProblemasLegales() {
            return tieneJuicioMercantilActivo || tieneDemandas || tieneEmbargos;
        }
    }
}
