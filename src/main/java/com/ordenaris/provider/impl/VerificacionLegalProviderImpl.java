package com.ordenaris.provider.impl;

import com.ordenaris.provider.VerificacionLegalProvider;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación en memoria del proveedor de verificación legal
 */
public class VerificacionLegalProviderImpl implements VerificacionLegalProvider {
    
    private final Map<String, VerificacionLegal> database = new ConcurrentHashMap<>();
    
    public VerificacionLegalProviderImpl() {
        initializeData();
    }
    
    private void initializeData() {
        // Sin problemas legales
        database.put("EMPRESA_001", new VerificacionLegal(false, false, false));
        database.put("EMPRESA_002", new VerificacionLegal(false, false, false));
        
        // Con juicio mercantil activo
        database.put("EMPRESA_003", new VerificacionLegal(true, true, false));
        
        // Con demandas pero sin juicio activo
        database.put("EMPRESA_004", new VerificacionLegal(false, true, false));
    }
    
    @Override
    public Optional<VerificacionLegal> obtenerVerificacionLegal(String empresaId) {
        return Optional.ofNullable(database.get(empresaId));
    }
    
    public void agregarOModificarVerificacion(String empresaId, VerificacionLegal verificacion) {
        database.put(empresaId, verificacion);
    }
}
