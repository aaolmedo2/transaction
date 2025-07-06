package com.banquito.core.loan.transaction.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "prestamos_clientes", schema = "transaction")
public class PrestamosCliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('loans.prestamos_clientes_id_prestamo_cliente_seq')")
    @Column(name = "id_prestamo_cliente", nullable = false)
    private Integer id;

    @Column(name = "id_cliente", nullable = false) // este atributo referencia un ID entero del cliente
    private String idCliente;

    @Column(name = "id_prestamo", nullable = false) // este atributo espera un uuid de mongo db que esta en otro
                                                    // microservicio
    private String idPrestamo;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_aprobacion")
    private LocalDate fechaAprobacion;

    @Column(name = "fecha_desembolso")
    private LocalDate fechaDesembolso;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "monto_solicitado", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoSolicitado;

    @Column(name = "plazo_meses", nullable = false)
    private Integer plazoMeses;

    @Column(name = "tasa_interes_aplicada", nullable = false, precision = 5, scale = 2)
    private BigDecimal tasaInteresAplicada;

    @Column(name = "estado", nullable = false, length = 15)
    private String estado;

    @Version
    @Column(name = "version", nullable = false, precision = 9)
    private Long version;

    @OneToMany(mappedBy = "idPrestamoCliente")
    private Set<CronogramasPago> cronogramasPagos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idPrestamoCliente")
    private Set<GarantiasTiposPrestamosCliente> garantiasTiposPrestamosClientes = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idPrestamoCliente")
    private Set<SegurosPrestamoCliente> segurosPrestamoClientes = new LinkedHashSet<>();

    public PrestamosCliente() {
    }

    public PrestamosCliente(Integer id) {
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
        PrestamosCliente other = (PrestamosCliente) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}