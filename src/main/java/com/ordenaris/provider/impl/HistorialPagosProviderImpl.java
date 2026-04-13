package com.ordenaris.provider.impl;

import com.ordenaris.provider.HistorialPagosProvider;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación en memoria del proveedor de historial de pagos
 */
public class HistorialPagosProviderImpl implements HistorialPagosProvider {
    
    private final Map<String, HistorialPagos> database = new ConcurrentHashMap<>();
    
    public HistorialPagosProviderImpl() {
        initializeData();
    }
    
    private void initializeData() {
        // Historial excelente
        database.put("EMPRESA_001", new HistorialPagos(false, 12, false));
        
        // Historial regular (con refinanciamiento)
        database.put("EMPRESA_002", new HistorialPagos(false, 8, true));
        
        // Historial malo (deuda vencida)
        database.put("EMPRESA_003", new HistorialPagos(true, 3, true));
        
        // Sin historial (se asume regular)
        // EMPRESA_004 no tiene entrada
    }
    
    @Override
    public Optional<HistorialPagos> obtenerHistorialPagos(String empresaId) {
        return Optional.ofNullable(database.get(empresaId));
    }
    
    public void agregarOModificarHistorial(String empresaId, HistorialPagos historial) {
        database.put(empresaId, historial);
    }
}
