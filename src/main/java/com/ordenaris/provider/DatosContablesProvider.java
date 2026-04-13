package com.ordenaris.provider;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Proveedor de datos contables de la empresa
 */
public interface DatosContablesProvider {
    Optional<DatosContables> obtenerDatosContables(String empresaId);
    
    record DatosContables(
        BigDecimal ventasPromedioMensual,
        BigDecimal pasivosTotales,
        BigDecimal activosTotales,
        int mesesDeExistencia
    ) {
        public DatosContables {
            if (ventasPromedioMensual == null || ventasPromedioMensual.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("ventasPromedioMensual no puede ser negativo");
            }
            if (pasivosTotales == null || pasivosTotales.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("pasivosTotales no puede ser negativo");
            }
            if (activosTotales == null || activosTotales.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("activosTotales no puede ser negativo");
            }
            if (mesesDeExistencia < 0) {
                throw new IllegalArgumentException("mesesDeExistencia no puede ser negativo");
            }
        }
    }
}
