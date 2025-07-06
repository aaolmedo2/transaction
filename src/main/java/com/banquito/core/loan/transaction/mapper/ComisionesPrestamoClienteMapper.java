package com.banquito.core.loan.transaction.mapper;

import com.banquito.core.loan.transaction.DTO.ComisionesPrestamoClienteDTO;
import com.banquito.core.loan.transaction.modelo.ComisionesPrestamoCliente;
import com.banquito.core.loan.transaction.modelo.PrestamosCliente;
import org.springframework.stereotype.Component;

@Component
public class ComisionesPrestamoClienteMapper {

    public ComisionesPrestamoClienteDTO toDTO(ComisionesPrestamoCliente entity) {
        if (entity == null)
            return null;

        return ComisionesPrestamoClienteDTO.builder()
                .id(entity.getId())
                .idPrestamoCliente(entity.getIdPrestamoCliente() != null ? entity.getIdPrestamoCliente().getId() : null)
                .idComisionPrestamo(entity.getIdComisionPrestamo())
                .fechaAplicacion(entity.getFechaAplicacion())
                .montoComision(entity.getMonto())
                .estado(entity.getEstado())
                .version(entity.getVersion())
                .build();
    }

    public ComisionesPrestamoCliente toEntity(ComisionesPrestamoClienteDTO dto) {
        if (dto == null)
            return null;

        ComisionesPrestamoCliente entity = new ComisionesPrestamoCliente();
        entity.setId(dto.getId());

        // Crear la referencia al préstamo cliente
        if (dto.getIdPrestamoCliente() != null) {
            PrestamosCliente prestamoCliente = new PrestamosCliente();
            prestamoCliente.setId(dto.getIdPrestamoCliente());
            prestamoCliente.setVersion(1L); // Inicializar versión para entidades referenciadas
            entity.setIdPrestamoCliente(prestamoCliente);
        }

        entity.setIdComisionPrestamo(dto.getIdComisionPrestamo());
        entity.setFechaAplicacion(dto.getFechaAplicacion());
        entity.setMonto(dto.getMontoComision());
        entity.setEstado(dto.getEstado());
        entity.setVersion(dto.getVersion());

        return entity;
    }
}
