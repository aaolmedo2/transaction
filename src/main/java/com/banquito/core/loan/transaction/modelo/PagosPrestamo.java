package com.banquito.core.loan.transaction.modelo;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pagos_prestamos", schema = "transaction")
public class PagosPrestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('loans.pagos_prestamos_id_pago_seq')")
    @Column(name = "id_pago", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "id_cuota", nullable = false)
    private CronogramasPago idCuota;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDate fechaPago;

    @Column(name = "monto_pagado", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoPagado;

    @Column(name = "interes_pagado", nullable = false, precision = 15, scale = 2)
    private BigDecimal interesPagado;

    @ColumnDefault("0")
    @Column(name = "mora_pagada", nullable = false, precision = 15, scale = 2)
    private BigDecimal moraPagada;

    @Column(name = "capital_pagado", nullable = false, precision = 15, scale = 2)
    private BigDecimal capitalPagado;

    @Column(name = "tipo_pago", nullable = false, length = 20)
    private String tipoPago;

    @Column(name = "referencia", nullable = false, length = 100)
    private String referencia;

    @ColumnDefault("'COMPLETADO'")
    @Column(name = "estado", nullable = false, length = 15)
    private String estado;

    @Version
    @Column(name = "version", nullable = false, precision = 9)
    private Long version;

    public PagosPrestamo() {
    }

    public PagosPrestamo(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CronogramasPago getIdCuota() {
        return idCuota;
    }

    public void setIdCuota(CronogramasPago idCuota) {
        this.idCuota = idCuota;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public BigDecimal getInteresPagado() {
        return interesPagado;
    }

    public void setInteresPagado(BigDecimal interesPagado) {
        this.interesPagado = interesPagado;
    }

    public BigDecimal getMoraPagada() {
        return moraPagada;
    }

    public void setMoraPagada(BigDecimal moraPagada) {
        this.moraPagada = moraPagada;
    }

    public BigDecimal getCapitalPagado() {
        return capitalPagado;
    }

    public void setCapitalPagado(BigDecimal capitalPagado) {
        this.capitalPagado = capitalPagado;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PagosPrestamo other = (PagosPrestamo) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PagosPrestamo [id=" + id + ", idCuota=" + idCuota + ", fechaPago=" + fechaPago + ", montoPagado="
                + montoPagado + ", interesPagado=" + interesPagado + ", moraPagada=" + moraPagada + ", capitalPagado="
                + capitalPagado + ", tipoPago=" + tipoPago + ", referencia=" + referencia + ", estado=" + estado
                + ", version=" + version + "]";
    }

}