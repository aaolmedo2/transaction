package com.banquito.core.loan.transaction.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banquito.core.loan.transaction.modelo.SegurosPrestamoCliente;
import com.banquito.core.loan.transaction.modelo.PrestamosCliente;

@Repository
public interface SegurosPrestamoClienteRepositorio extends JpaRepository<SegurosPrestamoCliente, Integer> {

    List<SegurosPrestamoCliente> findByIdPrestamoCliente(PrestamosCliente prestamoCliente);
}