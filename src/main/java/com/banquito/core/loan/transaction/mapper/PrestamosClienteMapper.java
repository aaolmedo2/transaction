package com.banquito.core.loan.transaction.mapper;

import com.banquito.core.loan.transaction.DTO.PrestamosClienteDTO;
import com.banquito.core.loan.transaction.modelo.PrestamosCliente;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrestamosClienteMapper {

    @Autowired
    private ModelMapper modelMapper;

    public PrestamosClienteDTO toDTO(PrestamosCliente entity) {
        return modelMapper.map(entity, PrestamosClienteDTO.class);
    }

    public PrestamosCliente toEntity(PrestamosClienteDTO dto) {
        return modelMapper.map(dto, PrestamosCliente.class);
    }
}
