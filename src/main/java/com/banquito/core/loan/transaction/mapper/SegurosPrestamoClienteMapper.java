package com.banquito.core.loan.transaction.mapper;

import com.banquito.core.loan.transaction.DTO.SegurosPrestamoClienteDTO;
import com.banquito.core.loan.transaction.modelo.SegurosPrestamoCliente;
import com.banquito.core.loan.transaction.modelo.PrestamosCliente;
import org.springframework.stereotype.Component;

@Component
public class SegurosPrestamoClienteMapper {

    public SegurosPrestamoClienteDTO toDTO(SegurosPrestamoCliente entity) {
        if (entity == null)
            return null;

        return SegurosPrestamoClienteDTO.builder()
                .id(entity.getId())
                .idPrestamoCliente(entity.getIdPrestamoCliente() != null ? entity.getIdPrestamoCliente().getId() : null)
                .idSeguroPrestamo(entity.getIdSeguroPrestamo())
                .montoTotal(entity.getMontoTotal())
                .montoCuota(entity.getMontoCuota())
                .estado(entity.getEstado())
                .version(entity.getVersion())
                .build();
    }

    public SegurosPrestamoCliente toEntity(SegurosPrestamoClienteDTO dto) {
        if (dto == null)
            return null;

        SegurosPrestamoCliente entity = new SegurosPrestamoCliente();
        entity.setId(dto.getId());

        // Crear la referencia al préstamo cliente
        if (dto.getIdPrestamoCliente() != null) {
            PrestamosCliente prestamoCliente = new PrestamosCliente();
            prestamoCliente.setId(dto.getIdPrestamoCliente());
            prestamoCliente.setVersion(1L); // Inicializar versión para entidades referenciadas
            entity.setIdPrestamoCliente(prestamoCliente);
        }

        entity.setIdSeguroPrestamo(dto.getIdSeguroPrestamo());
        entity.setMontoTotal(dto.getMontoTotal());
        entity.setMontoCuota(dto.getMontoCuota());
        entity.setEstado(dto.getEstado());
        entity.setVersion(dto.getVersion());

        return entity;
    }
}
