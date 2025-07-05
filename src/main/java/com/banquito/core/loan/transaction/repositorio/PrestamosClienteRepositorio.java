package com.banquito.core.loan.transaction.repositorio;

import com.banquito.core.loan.transaction.modelo.PrestamosCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrestamosClienteRepositorio extends JpaRepository<PrestamosCliente, Integer> {
}
