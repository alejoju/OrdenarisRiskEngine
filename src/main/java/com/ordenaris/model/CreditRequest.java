package com.ordenaris.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa una solicitud de crédito empresarial
 * @param empresaId Identificador único de la empresa
 * @param montoSolicitado Monto solicitado en la línea de crédito
 * @param productoFinanciero Tipo de producto financiero solicitado
 * @param fechaSolicitud Fecha en que se realiza la solicitud
 */
public record CreditRequest(
    String empresaId,
    BigDecimal montoSolicitado,
    ProductoFinanciero productoFinanciero,
    LocalDate fechaSolicitud
) {
    public CreditRequest {
        if (empresaId == null || empresaId.isBlank()) {
            throw new IllegalArgumentException("empresaId no puede ser nulo o vacío");
        }
        if (montoSolicitado == null || montoSolicitado.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("montoSolicitado debe ser mayor a cero");
        }
        if (productoFinanciero == null) {
            throw new IllegalArgumentException("productoFinanciero no puede ser nulo");
        }
        if (fechaSolicitud == null) {
            throw new IllegalArgumentException("fechaSolicitud no puede ser nula");
        }
    }
}
