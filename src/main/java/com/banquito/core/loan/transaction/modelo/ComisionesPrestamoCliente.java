package com.banquito.core.loan.transaction.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "comisiones_prestamo_cliente", schema = "transaction")
public class ComisionesPrestamoCliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('loans.comisiones_prestamo_cliente_id_comision_cliente_seq')")
    @Column(name = "id_comision_cliente", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "id_prestamo_cliente", nullable = false)
    private PrestamosCliente idPrestamoCliente;

    @Column(name = "id_comision_prestamo", nullable = false)
    private String idComisionPrestamo; // este atributo espera un uuid de mongo db que esta en otro microservicio

    @Column(name = "fecha_aplicacion", nullable = false)
    private LocalDate fechaAplicacion;

    @Column(name = "monto", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @ColumnDefault("'PENDIENTE'")
    @Column(name = "estado", nullable = false, length = 15)
    private String estado;

    @Version
    @ColumnDefault("1")
    @Column(name = "version", nullable = false, precision = 9)
    private Long version;

    public ComisionesPrestamoCliente() {
    }

    public ComisionesPrestamoCliente(Integer id) {
        this.id = id;
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
        ComisionesPrestamoCliente other = (ComisionesPrestamoCliente) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ComisionesPrestamoCliente [id=" + id + ", idPrestamoCliente=" + idPrestamoCliente
                + ", idComisionPrestamo=" + idComisionPrestamo + ", fechaAplicacion=" + fechaAplicacion + ", monto="
                + monto + ", estado=" + estado + ", version=" + version + "]";
    }
}