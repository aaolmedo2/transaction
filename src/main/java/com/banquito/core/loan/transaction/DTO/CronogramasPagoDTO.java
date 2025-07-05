package com.banquito.core.loan.transaction.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CronogramasPagoDTO {
    private Integer id;
    private Integer idPrestamoCliente;
    private Integer numeroCuota;
    private LocalDate fechaProgramada;
    private BigDecimal montoCuota;
    private BigDecimal interes;
    private BigDecimal comisiones;
    private BigDecimal seguros;
    private BigDecimal total;
    private BigDecimal saldoPendiente;
    private String estado;
    private Long version;
}
