package com.banquito.core.loan.transaction.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banquito.core.loan.transaction.modelo.GarantiasTiposPrestamosCliente;

@Repository
public interface GarantiasTiposPrestamosClienteRepositorio
                extends JpaRepository<GarantiasTiposPrestamosCliente, Integer> {

}