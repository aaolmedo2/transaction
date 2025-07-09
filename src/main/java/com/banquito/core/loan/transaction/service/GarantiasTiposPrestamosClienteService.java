package com.banquito.core.loan.transaction.service;

import com.banquito.core.loan.transaction.DTO.GarantiasTiposPrestamosClienteDTO;
import com.banquito.core.loan.transaction.DTO.external.TiposPrestamosDTO;
import com.banquito.core.loan.transaction.client.TiposPrestamosClient;
import com.banquito.core.loan.transaction.enums.EstadoGeneralEnum;
import com.banquito.core.loan.transaction.exception.CreateException;
import com.banquito.core.loan.transaction.exception.EntityNotFoundException;
import com.banquito.core.loan.transaction.mapper.GarantiasTiposPrestamosClienteMapper;
import com.banquito.core.loan.transaction.modelo.GarantiasTiposPrestamosCliente;
import com.banquito.core.loan.transaction.modelo.PrestamosCliente;
import com.banquito.core.loan.transaction.repositorio.GarantiasTiposPrestamosClienteRepositorio;
import com.banquito.core.loan.transaction.repositorio.PrestamosClienteRepositorio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class GarantiasTiposPrestamosClienteService {

    @Autowired
    private GarantiasTiposPrestamosClienteRepositorio garantiasRepositorio;

    @Autowired
    private PrestamosClienteRepositorio prestamosClienteRepositorio;

    @Autowired
    private GarantiasTiposPrestamosClienteMapper garantiasMapper;

    @Autowired
    private TiposPrestamosClient tiposPrestamosClient;

    @Transactional(readOnly = true)
    public GarantiasTiposPrestamosClienteDTO findById(Integer id) {
        log.info("Buscando garantía con ID: {}", id);
        Optional<GarantiasTiposPrestamosCliente> garantia = garantiasRepositorio.findById(id);

        if (garantia.isEmpty()) {
            log.error("Garantía no encontrada con ID: {}", id);
            throw new EntityNotFoundException("GarantiasTiposPrestamosCliente", "Garantía no encontrada con ID: " + id);
        }

        return garantiasMapper.toDTO(garantia.get());
    }

    public GarantiasTiposPrestamosClienteDTO create(GarantiasTiposPrestamosClienteDTO garantiaDTO) {
        log.info("Creando nueva garantía para préstamo cliente ID: {}", garantiaDTO.getIdPrestamoCliente());

        try {
            // Validar que el préstamo cliente existe
            validarPrestamoClienteExiste(garantiaDTO.getIdPrestamoCliente());

            // Validar que la garantía tipo préstamo existe en el microservicio externo
            // y validar el monto tasado contra el valor de la garantía
            validarGarantiaTipoPrestamoExterno(garantiaDTO.getIdGarantiaTipoPrestamo(), garantiaDTO.getMontoTasado());

            // Establecer valores por defecto
            if (garantiaDTO.getFechaRegistro() == null) {
                garantiaDTO.setFechaRegistro(LocalDate.now());
            }
            garantiaDTO.setEstado(EstadoGeneralEnum.ACTIVO.name());

            // Convertir DTO a entidad y guardar
            GarantiasTiposPrestamosCliente garantia = garantiasMapper.toEntity(garantiaDTO);
            GarantiasTiposPrestamosCliente garantiaGuardada = garantiasRepositorio.save(garantia);

            log.info("Garantía creada exitosamente con ID: {}", garantiaGuardada.getId());
            return garantiasMapper.toDTO(garantiaGuardada);

        } catch (Exception e) {
            log.error("Error al crear garantía: {}", e.getMessage());
            throw new CreateException("GarantiasTiposPrestamosCliente", "Error al crear garantía: " + e.getMessage());
        }
    }

    public GarantiasTiposPrestamosClienteDTO deleteLogical(Integer id) {
        log.info("Eliminando lógicamente garantía con ID: {}", id);

        try {
            GarantiasTiposPrestamosCliente garantiaExistente = garantiasRepositorio.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("GarantiasTiposPrestamosCliente",
                            "Garantía no encontrada con ID: " + id));

            garantiaExistente.setEstado(EstadoGeneralEnum.INACTIVO.name());
            GarantiasTiposPrestamosCliente garantiaActualizada = garantiasRepositorio.save(garantiaExistente);

            log.info("Garantía eliminada lógicamente exitosamente");
            return garantiasMapper.toDTO(garantiaActualizada);

        } catch (Exception e) {
            log.error("Error al eliminar garantía: {}", e.getMessage());
            throw new CreateException("GarantiasTiposPrestamosCliente",
                    "Error al eliminar garantía: " + e.getMessage());
        }
    }

    private void validarPrestamoClienteExiste(Integer idPrestamoCliente) {
        Optional<PrestamosCliente> prestamoCliente = prestamosClienteRepositorio.findById(idPrestamoCliente);
        if (prestamoCliente.isEmpty()) {
            throw new CreateException("GarantiasTiposPrestamosCliente",
                    "Préstamo cliente no encontrado con ID: " + idPrestamoCliente);
        }
    }

    private void validarGarantiaTipoPrestamoExterno(String idGarantiaTipoPrestamo, java.math.BigDecimal montoTasado) {
        try {
            log.info("Validando garantía tipo préstamo externa con ID: {}", idGarantiaTipoPrestamo);
            ResponseEntity<TiposPrestamosDTO> response = tiposPrestamosClient.findById(idGarantiaTipoPrestamo);

            TiposPrestamosDTO tipoPrestamo = response.getBody();
            if (tipoPrestamo == null) {
                throw new CreateException("GarantiasTiposPrestamosCliente",
                        "Garantía tipo préstamo no encontrada en el sistema externo: " + idGarantiaTipoPrestamo);
            }

            // Validar que el tipo de préstamo tiene una garantía asociada
            if (tipoPrestamo.getGarantia() == null) {
                throw new CreateException("GarantiasTiposPrestamosCliente",
                        "El tipo de préstamo no tiene una garantía asociada: " + idGarantiaTipoPrestamo);
            }

            // Validar que el monto tasado no sea mayor al valor de la garantía
            if (montoTasado != null && tipoPrestamo.getGarantia().getValor() != null) {
                if (montoTasado.compareTo(tipoPrestamo.getGarantia().getValor()) > 0) {
                    throw new CreateException("GarantiasTiposPrestamosCliente",
                            String.format("El monto tasado (%.2f) no puede ser mayor al valor de la garantía (%.2f)",
                                    montoTasado, tipoPrestamo.getGarantia().getValor()));
                }
            }

            log.info("Garantía tipo préstamo externa validada exitosamente. Valor garantía: {}, Monto tasado: {}",
                    tipoPrestamo.getGarantia().getValor(), montoTasado);

        } catch (Exception e) {
            log.error("Error al validar garantía tipo préstamo externa: {}", e.getMessage());
            throw new CreateException("GarantiasTiposPrestamosCliente",
                    "Error al validar garantía tipo préstamo externa: " + e.getMessage());
        }
    }
}
