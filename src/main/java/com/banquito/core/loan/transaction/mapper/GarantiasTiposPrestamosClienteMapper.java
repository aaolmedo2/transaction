package com.banquito.core.loan.transaction.mapper;

import com.banquito.core.loan.transaction.DTO.GarantiasTiposPrestamosClienteDTO;
import com.banquito.core.loan.transaction.modelo.GarantiasTiposPrestamosCliente;
import com.banquito.core.loan.transaction.modelo.PrestamosCliente;
import org.springframework.stereotype.Component;

@Component
public class GarantiasTiposPrestamosClienteMapper {

    public GarantiasTiposPrestamosClienteDTO toDTO(GarantiasTiposPrestamosCliente entity) {
        if (entity == null)
            return null;

        return GarantiasTiposPrestamosClienteDTO.builder()
                .id(entity.getId())
                .idPrestamoCliente(entity.getIdPrestamoCliente() != null ? entity.getIdPrestamoCliente().getId() : null)
                .idGarantiaTipoPrestamo(entity.getIdGarantiaTipoPrestamo())
                .montoTasado(entity.getMontoTasado())
                .fechaRegistro(entity.getFechaRegistro())
                .descripcion(entity.getDescripcion())
                .documentoReferencia(entity.getDocumentoReferencia())
                .estado(entity.getEstado())
                .version(entity.getVersion())
                .build();
    }

    public GarantiasTiposPrestamosCliente toEntity(GarantiasTiposPrestamosClienteDTO dto) {
        if (dto == null)
            return null;

        GarantiasTiposPrestamosCliente entity = new GarantiasTiposPrestamosCliente();
        entity.setId(dto.getId());

        // Crear la referencia al préstamo cliente
        if (dto.getIdPrestamoCliente() != null) {
            PrestamosCliente prestamoCliente = new PrestamosCliente();
            prestamoCliente.setId(dto.getIdPrestamoCliente());
            prestamoCliente.setVersion(1L); // Inicializar versión para entidades referenciadas
            entity.setIdPrestamoCliente(prestamoCliente);
        }

        entity.setIdGarantiaTipoPrestamo(dto.getIdGarantiaTipoPrestamo());
        entity.setMontoTasado(dto.getMontoTasado());
        entity.setFechaRegistro(dto.getFechaRegistro());
        entity.setDescripcion(dto.getDescripcion());
        entity.setDocumentoReferencia(dto.getDocumentoReferencia());
        entity.setEstado(dto.getEstado());
        entity.setVersion(dto.getVersion());

        return entity;
    }
}
