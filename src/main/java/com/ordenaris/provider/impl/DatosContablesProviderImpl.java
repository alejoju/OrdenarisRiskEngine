package com.ordenaris.provider.impl;

import com.ordenaris.provider.DatosContablesProvider;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación en memoria del proveedor de datos contables
 */
public class DatosContablesProviderImpl implements DatosContablesProvider {
    
    private final Map<String, DatosContables> database = new ConcurrentHashMap<>();
    
    public DatosContablesProviderImpl() {
        initializeData();
    }
    
    private void initializeData() {
        // Empresa con buen desempeño
        database.put("EMPRESA_001", new DatosContables(
            new BigDecimal("100000"),  // ventas mensuales: 100k
            new BigDecimal("500000"),   // pasivos: 500k
            new BigDecimal("1000000"),  // activos: 1M
            36                          // 3 años
        ));
        
        // Empresa nueva
        database.put("EMPRESA_002", new DatosContables(
            new BigDecimal("50000"),    // ventas mensuales: 50k
            new BigDecimal("200000"),   // pasivos: 200k
            new BigDecimal("300000"),   // activos: 300k
            12                          // 1 año
        ));
        
        // Empresa con problemas
        database.put("EMPRESA_003", new DatosContables(
            new BigDecimal("200000"),   // ventas mensuales: 200k
            new BigDecimal("100000"),   // pasivos: 100k
            new BigDecimal("500000"),   // activos: 500k
            48                          // 4 años
        ));
        
        // Empresa con ventas bajas
        database.put("EMPRESA_004", new DatosContables(
            new BigDecimal("10000"),    // ventas mensuales: 10k
            new BigDecimal("50000"),    // pasivos: 50k
            new BigDecimal("100000"),   // activos: 100k
            60                          // 5 años
        ));
    }
    
    @Override
    public Optional<DatosContables> obtenerDatosContables(String empresaId) {
        return Optional.ofNullable(database.get(empresaId));
    }
    
    public void agregarOModificarEmpresa(String empresaId, DatosContables datos) {
        database.put(empresaId, datos);
    }
}
