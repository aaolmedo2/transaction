package com.banquito.core.loan.transaction.DTO.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TiposPrestamosDTO {
    private String id;
    private String idMoneda;
    private String nombre;
    private String descripcion;
    private String requisitos;
    private String tipoCliente;
    private String fechaCreacion;
    private String fechaModificacion;
    private String estado;
    private Integer version;
    private String esquemaAmortizacion;
    private String idGarantia;
    private GarantiaDTO garantia;
}
