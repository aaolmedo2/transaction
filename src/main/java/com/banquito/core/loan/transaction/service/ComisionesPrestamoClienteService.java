package com.banquito.core.loan.transaction.service;

import com.banquito.core.loan.transaction.DTO.ComisionesPrestamoClienteDTO;
import com.banquito.core.loan.transaction.DTO.external.PrestamosDTO;
import com.banquito.core.loan.transaction.client.PrestamosClient;
import com.banquito.core.loan.transaction.enums.EstadoComisionClienteEnum;
import com.banquito.core.loan.transaction.exception.CreateException;
import com.banquito.core.loan.transaction.exception.EntityNotFoundException;
import com.banquito.core.loan.transaction.mapper.ComisionesPrestamoClienteMapper;
import com.banquito.core.loan.transaction.modelo.ComisionesPrestamoCliente;
import com.banquito.core.loan.transaction.modelo.PrestamosCliente;
import com.banquito.core.loan.transaction.repositorio.ComisionesPrestamoClienteRepositorio;
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
public class ComisionesPrestamoClienteService {

    @Autowired
    private ComisionesPrestamoClienteRepositorio comisionesRepositorio;

    @Autowired
    private PrestamosClienteRepositorio prestamosClienteRepositorio;

    @Autowired
    private ComisionesPrestamoClienteMapper comisionesMapper;

    @Autowired
    private PrestamosClient prestamosClient;

    @Transactional(readOnly = true)
    public ComisionesPrestamoClienteDTO findById(Integer id) {
        log.info("Buscando comisión con ID: {}", id);
        Optional<ComisionesPrestamoCliente> comision = comisionesRepositorio.findById(id);

        if (comision.isEmpty()) {
            log.error("Comisión no encontrada con ID: {}", id);
            throw new EntityNotFoundException("ComisionesPrestamoCliente", "Comisión no encontrada con ID: " + id);
        }

        return comisionesMapper.toDTO(comision.get());
    }

    public ComisionesPrestamoClienteDTO deleteLogical(Integer id) {
        log.info("Eliminando lógicamente comisión con ID: {}", id);

        try {
            ComisionesPrestamoCliente comisionExistente = comisionesRepositorio.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("ComisionesPrestamoCliente",
                            "Comisión no encontrada con ID: " + id));

            comisionExistente.setEstado(EstadoComisionClienteEnum.INACTIVO.name());
            ComisionesPrestamoCliente comisionActualizada = comisionesRepositorio.save(comisionExistente);

            log.info("Comisión eliminada lógicamente exitosamente");
            return comisionesMapper.toDTO(comisionActualizada);

        } catch (Exception e) {
            log.error("Error al eliminar comisión: {}", e.getMessage());
            throw new CreateException("ComisionesPrestamoCliente", "Error al eliminar comisión: " + e.getMessage());
        }
    }

    private PrestamosCliente validarPrestamoClienteExiste(Integer idPrestamoCliente) {
        Optional<PrestamosCliente> prestamoCliente = prestamosClienteRepositorio.findById(idPrestamoCliente);
        if (prestamoCliente.isEmpty()) {
            throw new CreateException("ComisionesPrestamoCliente",
                    "Préstamo cliente no encontrado con ID: " + idPrestamoCliente);
        }
        return prestamoCliente.get();
    }

    private void calcularYValidarComision(ComisionesPrestamoClienteDTO comisionDTO, PrestamosCliente prestamoCliente) {
        try {
            log.info("Validando y calculando comisión externa con ID: {}", comisionDTO.getIdComisionPrestamo());
            ResponseEntity<PrestamosDTO> response = prestamosClient.findById(prestamoCliente.getIdPrestamo());

            PrestamosDTO prestamo = response.getBody();
            if (prestamo == null || prestamo.getIdTipoComision() == null) {
                throw new CreateException("ComisionesPrestamoCliente",
                        "No se pudo obtener el tipo de comisión del préstamo: " + prestamoCliente.getIdPrestamo());
            }

            // Validar que el ID del préstamo proporcionado coincida con el ID del préstamo
            // consultado
            if (!prestamo.getId().equals(comisionDTO.getIdComisionPrestamo())) {
                throw new CreateException("ComisionesPrestamoCliente",
                        String.format(
                                "El ID del préstamo proporcionado (%s) no coincide con el préstamo del cliente (%s)",
                                comisionDTO.getIdComisionPrestamo(), prestamo.getId()));
            }

            // Validar que el préstamo tenga información del tipo de comisión
            if (prestamo.getTipoComision() == null) {
                throw new CreateException("ComisionesPrestamoCliente",
                        "El préstamo no tiene información del tipo de comisión asociado");
            }

            // Validar que la comisión esté activa
            if (!"ACTIVO".equals(prestamo.getTipoComision().getEstado())) {
                throw new CreateException("ComisionesPrestamoCliente",
                        "La comisión no está activa: " + prestamo.getTipoComision().getEstado());
            }

            // Calcular el monto de la comisión basado en el tipo de cálculo
            java.math.BigDecimal montoComision;
            if ("PORCENTAJE".equals(prestamo.getTipoComision().getTipoCalculo())) {
                // Si es porcentaje, calcularlo sobre el monto solicitado del préstamo
                montoComision = prestamoCliente.getMontoSolicitado()
                        .multiply(prestamo.getTipoComision().getMonto())
                        .divide(java.math.BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
                log.info("Comisión calculada por PORCENTAJE: {}% de {} = {}",
                        prestamo.getTipoComision().getMonto(), prestamoCliente.getMontoSolicitado(), montoComision);
            } else if ("FIJO".equals(prestamo.getTipoComision().getTipoCalculo())) {
                // Si es monto fijo, usar el monto directamente
                montoComision = prestamo.getTipoComision().getMonto();
                log.info("Comisión establecida como monto FIJO: {}", montoComision);
            } else {
                throw new CreateException("ComisionesPrestamoCliente",
                        "Tipo de cálculo no válido: " + prestamo.getTipoComision().getTipoCalculo()
                                + ". Debe ser PORCENTAJE o FIJO");
            }

            // Siempre asignar el valor calculado (sobrescribir cualquier valor enviado por
            // el usuario)
            comisionDTO.setMontoComision(montoComision);

            log.info("Comisión externa validada y calculada exitosamente. Tipo: {}, Monto final: {}",
                    prestamo.getTipoComision().getTipoCalculo(), montoComision);

        } catch (Exception e) {
            log.error("Error al validar y calcular comisión externa: {}", e.getMessage());
            throw new CreateException("ComisionesPrestamoCliente",
                    "Error al validar y calcular comisión externa: " + e.getMessage());
        }
    }

    public ComisionesPrestamoClienteDTO createInternal(ComisionesPrestamoClienteDTO comisionDTO) {
        log.info("Creando comisión interna para préstamo cliente ID: {}", comisionDTO.getIdPrestamoCliente());

        try {
            // Validar que el préstamo cliente existe
            PrestamosCliente prestamoCliente = validarPrestamoClienteExiste(comisionDTO.getIdPrestamoCliente());

            // Validar que la comisión existe en el microservicio externo y corresponde al
            // mismo préstamo, además calcular el monto
            calcularYValidarComision(comisionDTO, prestamoCliente);

            // Establecer valores por defecto
            if (comisionDTO.getFechaAplicacion() == null) {
                comisionDTO.setFechaAplicacion(LocalDate.now());
            }

            comisionDTO.setEstado(EstadoComisionClienteEnum.PENDIENTE.name());

            // Convertir DTO a entidad y guardar
            ComisionesPrestamoCliente comision = comisionesMapper.toEntity(comisionDTO);
            ComisionesPrestamoCliente comisionGuardada = comisionesRepositorio.save(comision);

            log.info("Comisión interna creada exitosamente con ID: {}", comisionGuardada.getId());
            return comisionesMapper.toDTO(comisionGuardada);

        } catch (Exception e) {
            log.error("Error al crear comisión interna: {}", e.getMessage());
            throw new CreateException("ComisionesPrestamoCliente",
                    "Error al crear comisión interna: " + e.getMessage());
        }
    }
}
