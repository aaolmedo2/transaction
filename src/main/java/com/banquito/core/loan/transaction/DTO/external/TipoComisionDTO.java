package com.banquito.core.loan.transaction.DTO.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TipoComisionDTO {
    private String id;
    private String tipo;
    private String nombre;
    private String descripcion;
    private String tipoCalculo;
    private BigDecimal monto;
    private String estado;
    private Integer version;
}
