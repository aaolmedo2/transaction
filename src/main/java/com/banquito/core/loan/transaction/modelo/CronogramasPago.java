package com.banquito.core.loan.transaction.modelo;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "cronogramas_pagos", schema = "loans")
public class CronogramasPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('loans.cronogramas_pagos_id_cuota_seq')")
    @Column(name = "id_cuota", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "id_prestamo_cliente", nullable = false)
    private PrestamosCliente idPrestamoCliente;

    @Column(name = "numero_cuota", nullable = false)
    private Integer numeroCuota;

    @Column(name = "fecha_programada", nullable = false)
    private LocalDate fechaProgramada;

    @Column(name = "monto_cuota", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoCuota;

    @Column(name = "interes", nullable = false, precision = 15, scale = 2)
    private BigDecimal interes;

    @ColumnDefault("0")
    @Column(name = "comisiones", nullable = false, precision = 15, scale = 2)
    private BigDecimal comisiones;

    @ColumnDefault("0")
    @Column(name = "seguros", nullable = false, precision = 15, scale = 2)
    private BigDecimal seguros;

    @Column(name = "total", nullable = false, precision = 15, scale = 2)
    private BigDecimal total;

    @Column(name = "saldo_pendiente", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoPendiente;

    @ColumnDefault("'PENDIENTE'")
    @Column(name = "estado", nullable = false, length = 15)
    private String estado;

    @Version
    @Column(name = "version", nullable = false, precision = 9)
    private Long version;

    @OneToMany(mappedBy = "idCuota")
    private Set<PagosPrestamo> pagosPrestamos = new LinkedHashSet<>();

    public CronogramasPago() {
    }

    public CronogramasPago(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PrestamosCliente getIdPrestamoCliente() {
        return idPrestamoCliente;
    }

    public void setIdPrestamoCliente(PrestamosCliente idPrestamoCliente) {
        this.idPrestamoCliente = idPrestamoCliente;
    }

    public Integer getNumeroCuota() {
        return numeroCuota;
    }

    public void setNumeroCuota(Integer numeroCuota) {
        this.numeroCuota = numeroCuota;
    }

    public LocalDate getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDate fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public BigDecimal getMontoCuota() {
        return montoCuota;
    }

    public void setMontoCuota(BigDecimal montoCuota) {
        this.montoCuota = montoCuota;
    }

    public BigDecimal getInteres() {
        return interes;
    }

    public void setInteres(BigDecimal interes) {
        this.interes = interes;
    }

    public BigDecimal getComisiones() {
        return comisiones;
    }

    public void setComisiones(BigDecimal comisiones) {
        this.comisiones = comisiones;
    }

    public BigDecimal getSeguros() {
        return seguros;
    }

    public void setSeguros(BigDecimal seguros) {
        this.seguros = seguros;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(BigDecimal saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
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

    public Set<PagosPrestamo> getPagosPrestamos() {
        return pagosPrestamos;
    }

    public void setPagosPrestamos(Set<PagosPrestamo> pagosPrestamos) {
        this.pagosPrestamos = pagosPrestamos;
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
        CronogramasPago other = (CronogramasPago) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CronogramasPago [id=" + id + ", idPrestamoCliente=" + idPrestamoCliente + ", numeroCuota=" + numeroCuota
                + ", fechaProgramada=" + fechaProgramada + ", montoCuota=" + montoCuota + ", interes=" + interes
                + ", comisiones=" + comisiones + ", seguros=" + seguros + ", total=" + total + ", saldoPendiente="
                + saldoPendiente + ", estado=" + estado + ", version=" + version + ", pagosPrestamos=" + pagosPrestamos
                + "]";
    }

}