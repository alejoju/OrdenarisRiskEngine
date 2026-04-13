package com.ordenaris.provider;

import java.util.Optional;

/**
 * Proveedor de historial de pagos de la empresa
 */
public interface HistorialPagosProvider {
    Optional<HistorialPagos> obtenerHistorialPagos(String empresaId);
    
    record HistorialPagos(
        boolean tieneDeudaVencidaMas90Dias,
        int ultimos12PagosTiempo,
        boolean tieneRefinanciamiento
    ) {
        public HistorialPagos {
            if (ultimos12PagosTiempo < 0 || ultimos12PagosTiempo > 12) {
                throw new IllegalArgumentException("ultimos12PagosTiempo debe estar entre 0 y 12");
            }
        }
        
        public boolean tieneHistorialExcelente() {
            return ultimos12PagosTiempo == 12 && !tieneRefinanciamiento;
        }
    }
}
