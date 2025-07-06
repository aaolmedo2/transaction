package com.banquito.core.loan.transaction.DTO.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GarantiaDTO {
    private String id;
    private String tipoGarantia;
    private String descripcion;
    private BigDecimal valor;
    private String estado;
    private Integer version;
}
