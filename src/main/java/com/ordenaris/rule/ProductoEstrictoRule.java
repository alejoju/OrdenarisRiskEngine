package com.ordenaris.rule;

import com.ordenaris.model.CreditRequest;
import com.ordenaris.model.ProductoFinanciero;
import com.ordenaris.model.RiskLevel;
import com.ordenaris.provider.*;

import java.util.Optional;

/**
 * Regla: Si el producto solicitado es ARRENDAMIENTO_FINANCIERO, se aumenta el nivel de riesgo en 1 punto
 */
public class ProductoEstrictoRule implements RiskRule {
    
    @Override
    public Optional<RiskLevel> evaluate(CreditRequest request,
                                        DatosContablesProvider.DatosContables datosContables,
                                        HistorialPagosProvider.HistorialPagos historialPagos,
                                        VerificacionLegalProvider.VerificacionLegal verificacionLegal) {
        if (request.productoFinanciero() == ProductoFinanciero.ARRENDAMIENTO_FINANCIERO) {
            return Optional.of(RiskLevel.MEDIO);
        }
        return Optional.empty();
    }
    
    @Override
    public String getRuleName() {
        return "Producto Estricto";
    }
    
    @Override
    public String getDescription() {
        return "Si el producto solicitado es ARRENDAMIENTO_FINANCIERO, se aumenta el nivel de riesgo en 1 punto";
    }
}
