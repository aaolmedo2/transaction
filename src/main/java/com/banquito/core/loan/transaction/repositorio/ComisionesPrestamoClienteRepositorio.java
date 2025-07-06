package com.banquito.core.loan.transaction.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banquito.core.loan.transaction.modelo.ComisionesPrestamoCliente;
import com.banquito.core.loan.transaction.modelo.PrestamosCliente;

@Repository
public interface ComisionesPrestamoClienteRepositorio extends JpaRepository<ComisionesPrestamoCliente, Integer> {

    List<ComisionesPrestamoCliente> findByIdPrestamoCliente(PrestamosCliente prestamoCliente);
}