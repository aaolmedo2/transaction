package com.banquito.core.loan.transaction.DTO.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TiposComisionDTO {
    private String id;
    private String nombre;
    private String descripcion;
    private BigDecimal valor;
    private String tipoValor;
    private String estado;
}
