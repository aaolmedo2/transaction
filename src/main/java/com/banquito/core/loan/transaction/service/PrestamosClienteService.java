package com.banquito.core.loan.transaction.service;

import com.banquito.core.loan.transaction.DTO.PrestamosClienteDTO;
import com.banquito.core.loan.transaction.DTO.ComisionesPrestamoClienteDTO;
import com.banquito.core.loan.transaction.DTO.SegurosPrestamoClienteDTO;
import com.banquito.core.loan.transaction.DTO.external.ClienteDTO;
import com.banquito.core.loan.transaction.DTO.external.PrestamosDTO;
import com.banquito.core.loan.transaction.client.ClientesClient;
import com.banquito.core.loan.transaction.client.PrestamosClient;
import com.banquito.core.loan.transaction.enums.EstadoPrestamoClienteEnum;
import com.banquito.core.loan.transaction.exception.CreateException;
import com.banquito.core.loan.transaction.exception.EntityNotFoundException;
import com.banquito.core.loan.transaction.exception.UpdateException;
import com.banquito.core.loan.transaction.mapper.PrestamosClienteMapper;
import com.banquito.core.loan.transaction.modelo.PrestamosCliente;
import com.banquito.core.loan.transaction.repositorio.PrestamosClienteRepositorio;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class PrestamosClienteService {

    @Autowired
    private PrestamosClienteRepositorio prestamosClienteRepositorio;

    @Autowired
    private PrestamosClienteMapper prestamosClienteMapper;

    @Autowired
    private PrestamosClient prestamosClient;

    @Autowired
    private ClientesClient clientesClient;

    @Autowired
    private ComisionesPrestamoClienteService comisionesService;

    @Autowired
    private SegurosPrestamoClienteService segurosService;

    @Transactional(readOnly = true)
    public List<PrestamosClienteDTO> findAll() {
        log.info("Obteniendo todos los préstamos de clientes");
        List<PrestamosCliente> prestamos = prestamosClienteRepositorio.findAll();
        return prestamos.stream()
                .map(prestamosClienteMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public PrestamosClienteDTO findById(Integer id) {
        log.info("Buscando préstamo cliente con ID: {}", id);
        Optional<PrestamosCliente> prestamo = prestamosClienteRepositorio.findById(id);

        if (prestamo.isEmpty()) {
            log.error("Préstamo cliente no encontrado con ID: {}", id);
            throw new EntityNotFoundException("PrestamosCliente", "Préstamo cliente no encontrado con ID: " + id);
        }

        return prestamosClienteMapper.toDTO(prestamo.get());
    }

    public PrestamosClienteDTO create(PrestamosClienteDTO prestamosClienteDTO) {
        log.info("Creando nuevo préstamo cliente para cliente: {}", prestamosClienteDTO.getIdCliente());

        try {
            // Validar que el cliente existe en el microservicio de clientes
            validarClienteExistente(prestamosClienteDTO.getIdCliente());

            // Validar que el préstamo existe en el microservicio externo
            PrestamosDTO prestamoExterno = validarPrestamoExterno(prestamosClienteDTO.getIdPrestamo());

            // Validar monto solicitado
            validarMontoSolicitado(prestamosClienteDTO.getMontoSolicitado(), prestamoExterno);

            // Validar plazo en meses
            validarPlazoMeses(prestamosClienteDTO.getPlazoMeses(), prestamoExterno);

            // Validar tasa de interés
            validarTasaInteres(prestamosClienteDTO.getTasaInteresAplicada(), prestamoExterno);

            // Establecer valores por defecto
            prestamosClienteDTO.setFechaInicio(LocalDate.now());
            prestamosClienteDTO.setEstado(EstadoPrestamoClienteEnum.SOLICITADO.name());

            // Calcular fechas automáticamente
            calcularFechas(prestamosClienteDTO);

            // Convertir DTO a entidad y guardar
            PrestamosCliente prestamo = prestamosClienteMapper.toEntity(prestamosClienteDTO);
            PrestamosCliente prestamoGuardado = prestamosClienteRepositorio.save(prestamo);

            log.info("Préstamo cliente creado exitosamente con ID: {}", prestamoGuardado.getId());

            // Crear automáticamente las comisiones y seguros asociados
            crearComisionesYSeguros(prestamoGuardado, prestamosClienteDTO.getIdPrestamo());

            return prestamosClienteMapper.toDTO(prestamoGuardado);

        } catch (Exception e) {
            log.error("Error al crear préstamo cliente: {}", e.getMessage());
            throw new CreateException("PrestamosCliente", "Error al crear préstamo cliente: " + e.getMessage());
        }
    }

    public PrestamosClienteDTO update(Integer id, PrestamosClienteDTO prestamosClienteDTO) {
        log.info("Actualizando préstamo cliente con ID: {}", id);

        try {
            PrestamosCliente prestamoExistente = prestamosClienteRepositorio.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("PrestamosCliente",
                            "Préstamo cliente no encontrado con ID: " + id));

            // Actualizar campos permitidos
            if (prestamosClienteDTO.getFechaAprobacion() != null) {
                prestamoExistente.setFechaAprobacion(prestamosClienteDTO.getFechaAprobacion());
            }
            if (prestamosClienteDTO.getFechaDesembolso() != null) {
                prestamoExistente.setFechaDesembolso(prestamosClienteDTO.getFechaDesembolso());
            }
            if (prestamosClienteDTO.getFechaVencimiento() != null) {
                prestamoExistente.setFechaVencimiento(prestamosClienteDTO.getFechaVencimiento());
            }
            if (prestamosClienteDTO.getEstado() != null) {
                prestamoExistente.setEstado(prestamosClienteDTO.getEstado());
            }

            PrestamosCliente prestamoActualizado = prestamosClienteRepositorio.save(prestamoExistente);

            log.info("Préstamo cliente actualizado exitosamente con ID: {}", prestamoActualizado.getId());
            return prestamosClienteMapper.toDTO(prestamoActualizado);

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al actualizar préstamo cliente: {}", e.getMessage());
            throw new UpdateException("PrestamosCliente", "Error al actualizar préstamo cliente: " + e.getMessage());
        }
    }

    public PrestamosClienteDTO updateEstado(Integer id, String nuevoEstado) {
        log.info("Actualizando estado del préstamo cliente con ID: {} a estado: {}", id, nuevoEstado);

        try {
            PrestamosCliente prestamoExistente = prestamosClienteRepositorio.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("PrestamosCliente",
                            "Préstamo cliente no encontrado con ID: " + id));

            // Validar que el estado es válido
            try {
                EstadoPrestamoClienteEnum.valueOf(nuevoEstado);
            } catch (IllegalArgumentException e) {
                throw new UpdateException("PrestamosCliente", "Estado no válido: " + nuevoEstado);
            }

            prestamoExistente.setEstado(nuevoEstado);
            PrestamosCliente prestamoActualizado = prestamosClienteRepositorio.save(prestamoExistente);

            log.info("Estado del préstamo cliente actualizado exitosamente");
            return prestamosClienteMapper.toDTO(prestamoActualizado);

        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al actualizar estado del préstamo cliente: {}", e.getMessage());
            throw new UpdateException("PrestamosCliente",
                    "Error al actualizar estado del préstamo cliente: " + e.getMessage());
        }
    }

    private PrestamosDTO validarPrestamoExterno(String idPrestamo) {
        try {
            log.info("Validando préstamo externo con ID: {}", idPrestamo);
            ResponseEntity<PrestamosDTO> response = prestamosClient.findById(idPrestamo);

            if (response.getBody() == null) {
                throw new CreateException("PrestamosCliente",
                        "Préstamo no encontrado en el sistema externo: " + idPrestamo);
            }

            log.info("Préstamo externo validado exitosamente");
            return response.getBody();

        } catch (Exception e) {
            log.error("Error al validar préstamo externo: {}", e.getMessage());
            throw new CreateException("PrestamosCliente", "Error al validar préstamo externo: " + e.getMessage());
        }
    }

    private void validarMontoSolicitado(BigDecimal montoSolicitado, PrestamosDTO prestamoExterno) {
        if (prestamoExterno.getMontoMinimo() != null
                && montoSolicitado.compareTo(prestamoExterno.getMontoMinimo()) < 0) {
            throw new CreateException("PrestamosCliente", "El monto solicitado (" + montoSolicitado +
                    ") es menor al mínimo permitido (" + prestamoExterno.getMontoMinimo() + ")");
        }

        if (prestamoExterno.getMontoMaximo() != null
                && montoSolicitado.compareTo(prestamoExterno.getMontoMaximo()) > 0) {
            throw new CreateException("PrestamosCliente", "El monto solicitado (" + montoSolicitado +
                    ") es mayor al máximo permitido (" + prestamoExterno.getMontoMaximo() + ")");
        }

        log.info("Monto solicitado validado exitosamente");
    }

    private void validarPlazoMeses(Integer plazoMeses, PrestamosDTO prestamoExterno) {
        if (prestamoExterno.getPlazoMinimoMeses() != null && plazoMeses < prestamoExterno.getPlazoMinimoMeses()) {
            throw new CreateException("PrestamosCliente", "El plazo solicitado (" + plazoMeses +
                    " meses) es menor al mínimo permitido (" + prestamoExterno.getPlazoMinimoMeses() + " meses)");
        }

        if (prestamoExterno.getPlazoMaximoMeses() != null && plazoMeses > prestamoExterno.getPlazoMaximoMeses()) {
            throw new CreateException("PrestamosCliente", "El plazo solicitado (" + plazoMeses +
                    " meses) es mayor al máximo permitido (" + prestamoExterno.getPlazoMaximoMeses() + " meses)");
        }

        log.info("Plazo en meses validado exitosamente");
    }

    private void validarTasaInteres(BigDecimal tasaInteres, PrestamosDTO prestamoExterno) {
        if (prestamoExterno.getTasaInteres() != null &&
                tasaInteres.compareTo(prestamoExterno.getTasaInteres()) < 0) {
            throw new CreateException("PrestamosCliente", "La tasa de interés (" + tasaInteres +
                    "%) no puede ser menor a la tasa mínima del préstamo (" + prestamoExterno.getTasaInteres() + "%)");
        }

        log.info("Tasa de interés validada exitosamente");
    }

    private void calcularFechas(PrestamosClienteDTO prestamosClienteDTO) {
        if (prestamosClienteDTO.getFechaDesembolso() == null) {
            prestamosClienteDTO.setFechaDesembolso(prestamosClienteDTO.getFechaInicio().plusDays(1));
            log.info("Fecha de desembolso calculada: {}", prestamosClienteDTO.getFechaDesembolso());
        }

        if (prestamosClienteDTO.getFechaVencimiento() == null) {
            prestamosClienteDTO.setFechaVencimiento(
                    prestamosClienteDTO.getFechaDesembolso().plusMonths(prestamosClienteDTO.getPlazoMeses()));
            log.info("Fecha de vencimiento calculada: {} (plazo: {} meses)",
                    prestamosClienteDTO.getFechaVencimiento(), prestamosClienteDTO.getPlazoMeses());
        }
    }

    private void validarClienteExistente(String idCliente) {
        log.info("Validando existencia del cliente con ID: {}", idCliente);

        try {
            ResponseEntity<ClienteDTO> response = clientesClient.findById(idCliente);

            if (response == null || response.getBody() == null) {
                throw new CreateException("PrestamosCliente", "Cliente no encontrado");
            }

        } catch (FeignException.NotFound e) {
            throw new CreateException("PrestamosCliente", "Cliente no encontrado");
        } catch (FeignException e) {
            log.error("Error al comunicarse con servicio de clientes: {}", e.getMessage());
            throw new CreateException("PrestamosCliente", "Error al validar cliente");
        }
    }

    private void crearComisionesYSeguros(PrestamosCliente prestamoGuardado, String idPrestamo) {
        try {
            log.info("Creando comisiones y seguros automáticamente para préstamo cliente ID: {}",
                    prestamoGuardado.getId());

            // Crear comisión automáticamente
            ComisionesPrestamoClienteDTO comisionDTO = ComisionesPrestamoClienteDTO.builder()
                    .idPrestamoCliente(prestamoGuardado.getId())
                    .idComisionPrestamo(idPrestamo)
                    .build();

            comisionesService.createInternal(comisionDTO);
            log.info("Comisión creada automáticamente para préstamo cliente ID: {}", prestamoGuardado.getId());

            // Crear seguro automáticamente
            SegurosPrestamoClienteDTO seguroDTO = SegurosPrestamoClienteDTO.builder()
                    .idPrestamoCliente(prestamoGuardado.getId())
                    .idSeguroPrestamo(idPrestamo)
                    .build();

            segurosService.createInternal(seguroDTO);
            log.info("Seguro creado automáticamente para préstamo cliente ID: {}", prestamoGuardado.getId());

        } catch (Exception e) {
            log.warn(
                    "Error al crear comisiones y/o seguros automáticamente: {}. El préstamo cliente se creó correctamente.",
                    e.getMessage());
            // No lanzamos excepción para que no falle la creación del préstamo principal
        }
    }
}
