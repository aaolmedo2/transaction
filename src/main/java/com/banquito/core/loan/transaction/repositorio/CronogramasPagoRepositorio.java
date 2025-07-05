package com.banquito.core.loan.transaction.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banquito.core.loan.transaction.modelo.CronogramasPago;
import com.banquito.core.loan.transaction.modelo.PrestamosCliente;

@Repository
public interface CronogramasPagoRepositorio extends JpaRepository<CronogramasPago, Integer> {
    List<CronogramasPago> findByIdPrestamoCliente(PrestamosCliente prestamoCliente);

    List<CronogramasPago> findByIdPrestamoClienteOrderByNumeroCuotaAsc(PrestamosCliente prestamoCliente);

    List<CronogramasPago> findByIdPrestamoClienteAndEstado(PrestamosCliente prestamoCliente, String estado);
}