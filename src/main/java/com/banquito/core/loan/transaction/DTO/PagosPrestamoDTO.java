package com.banquito.core.loan.transaction.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PagosPrestamoDTO {
    private Integer id;
    private Integer idCuota;
    private LocalDate fechaPago;
    private BigDecimal montoPagado;
    private BigDecimal interesPagado;
    private BigDecimal moraPagada;
    private BigDecimal capitalPagado;
    private String tipoPago;
    private String referencia;
    private String estado;
    private Long version;
}
