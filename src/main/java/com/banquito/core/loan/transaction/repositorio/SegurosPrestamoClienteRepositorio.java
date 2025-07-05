package com.banquito.core.loan.transaction.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banquito.core.loan.transaction.modelo.SegurosPrestamoCliente;

@Repository
public interface SegurosPrestamoClienteRepositorio extends JpaRepository<SegurosPrestamoCliente, Integer> {

}