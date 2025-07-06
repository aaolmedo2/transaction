package com.banquito.core.loan.transaction.DTO.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SeguroDTO {
    private String id;
    private String tipoSeguro;
    private String compania;
    private BigDecimal montoAsegurado; // Representa el porcentaje
    private String fechaInicio;
    private String fechaFin;
    private String estado;
    private Integer version;
}
