package com.banquito.core.loan.transaction.DTO.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrestamosDTO {
    private String id;
    private String idTipoPrestamo;
    private String idMoneda;
    private String nombre;
    private String descripcion;
    private String fechaModificacion;
    private String baseCalculo;
    private BigDecimal tasaInteres;
    private BigDecimal montoMinimo;
    private BigDecimal montoMaximo;
    private Integer plazoMinimoMeses;
    private Integer plazoMaximoMeses;
    private String tipoAmortizacion;
    private String idSeguro;
    private String idTipoComision;
    private String estado;
    private Integer version;
    private TiposPrestamosDTO tipoPrestamo;
    private SeguroDTO seguro;
    private TipoComisionDTO tipoComision;
}
