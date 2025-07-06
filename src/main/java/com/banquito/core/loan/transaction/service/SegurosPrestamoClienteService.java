package com.banquito.core.loan.transaction.service;

import com.banquito.core.loan.transaction.DTO.SegurosPrestamoClienteDTO;
import com.banquito.core.loan.transaction.DTO.external.PrestamosDTO;
import com.banquito.core.loan.transaction.client.PrestamosClient;
import com.banquito.core.loan.transaction.enums.EstadoGeneralEnum;
import com.banquito.core.loan.transaction.exception.CreateException;
import com.banquito.core.loan.transaction.exception.EntityNotFoundException;
import com.banquito.core.loan.transaction.mapper.SegurosPrestamoClienteMapper;
import com.banquito.core.loan.transaction.modelo.SegurosPrestamoCliente;
import com.banquito.core.loan.transaction.modelo.PrestamosCliente;
import com.banquito.core.loan.transaction.repositorio.SegurosPrestamoClienteRepositorio;
import com.banquito.core.loan.transaction.repositorio.PrestamosClienteRepositorio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class SegurosPrestamoClienteService {

    @Autowired
    private SegurosPrestamoClienteRepositorio segurosRepositorio;

    @Autowired
    private PrestamosClienteRepositorio prestamosClienteRepositorio;

    @Autowired
    private SegurosPrestamoClienteMapper segurosMapper;

    @Autowired
    private PrestamosClient prestamosClient;

    @Transactional(readOnly = true)
    public List<SegurosPrestamoClienteDTO> findAll() {
        log.info("Obteniendo todos los seguros de préstamos de clientes");
        List<SegurosPrestamoCliente> seguros = segurosRepositorio.findAll();
        return seguros.stream()
                .map(segurosMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public SegurosPrestamoClienteDTO findById(Integer id) {
        log.info("Buscando seguro con ID: {}", id);
        Optional<SegurosPrestamoCliente> seguro = segurosRepositorio.findById(id);

        if (seguro.isEmpty()) {
            log.error("Seguro no encontrado con ID: {}", id);
            throw new EntityNotFoundException("SegurosPrestamoCliente", "Seguro no encontrado con ID: " + id);
        }

        return segurosMapper.toDTO(seguro.get());
    }

    public SegurosPrestamoClienteDTO create(SegurosPrestamoClienteDTO seguroDTO) {
        log.info("Creando nuevo seguro para préstamo cliente ID: {}", seguroDTO.getIdPrestamoCliente());

        try {
            // Validar que el préstamo cliente existe
            PrestamosCliente prestamoCliente = validarPrestamoClienteExiste(seguroDTO.getIdPrestamoCliente());

            // Validar que el seguro existe en el microservicio externo y corresponde al
            // mismo préstamo, además calcular los montos
            calcularYValidarSeguro(seguroDTO, prestamoCliente);

            // Establecer estado por defecto
            seguroDTO.setEstado(EstadoGeneralEnum.ACTIVO.name());

            // Convertir DTO a entidad y guardar
            SegurosPrestamoCliente seguro = segurosMapper.toEntity(seguroDTO);
            SegurosPrestamoCliente seguroGuardado = segurosRepositorio.save(seguro);

            log.info("Seguro creado exitosamente con ID: {}", seguroGuardado.getId());
            return segurosMapper.toDTO(seguroGuardado);

        } catch (Exception e) {
            log.error("Error al crear seguro: {}", e.getMessage());
            throw new CreateException("SegurosPrestamoCliente", "Error al crear seguro: " + e.getMessage());
        }
    }

    public SegurosPrestamoClienteDTO deleteLogical(Integer id) {
        log.info("Eliminando lógicamente seguro con ID: {}", id);

        try {
            SegurosPrestamoCliente seguroExistente = segurosRepositorio.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("SegurosPrestamoCliente",
                            "Seguro no encontrado con ID: " + id));

            seguroExistente.setEstado(EstadoGeneralEnum.INACTIVO.name());
            SegurosPrestamoCliente seguroActualizado = segurosRepositorio.save(seguroExistente);

            log.info("Seguro eliminado lógicamente exitosamente");
            return segurosMapper.toDTO(seguroActualizado);

        } catch (Exception e) {
            log.error("Error al eliminar seguro: {}", e.getMessage());
            throw new CreateException("SegurosPrestamoCliente", "Error al eliminar seguro: " + e.getMessage());
        }
    }

    private PrestamosCliente validarPrestamoClienteExiste(Integer idPrestamoCliente) {
        Optional<PrestamosCliente> prestamoCliente = prestamosClienteRepositorio.findById(idPrestamoCliente);
        if (prestamoCliente.isEmpty()) {
            throw new CreateException("SegurosPrestamoCliente",
                    "Préstamo cliente no encontrado con ID: " + idPrestamoCliente);
        }
        return prestamoCliente.get();
    }

    private void calcularYValidarSeguro(SegurosPrestamoClienteDTO seguroDTO, PrestamosCliente prestamoCliente) {
        try {
            log.info("Validando y calculando seguro externo con ID: {}", seguroDTO.getIdSeguroPrestamo());
            ResponseEntity<PrestamosDTO> response = prestamosClient.findById(prestamoCliente.getIdPrestamo());

            PrestamosDTO prestamo = response.getBody();
            if (prestamo == null || prestamo.getIdSeguro() == null) {
                throw new CreateException("SegurosPrestamoCliente",
                        "No se pudo obtener el seguro del préstamo: " + prestamoCliente.getIdPrestamo());
            }

            // Validar que el ID del préstamo proporcionado coincida con el ID del préstamo
            // consultado
            if (!prestamo.getId().equals(seguroDTO.getIdSeguroPrestamo())) {
                throw new CreateException("SegurosPrestamoCliente",
                        String.format(
                                "El ID del préstamo proporcionado (%s) no coincide con el préstamo del cliente (%s)",
                                seguroDTO.getIdSeguroPrestamo(), prestamo.getId()));
            }

            // Validar que el préstamo tenga información del seguro
            if (prestamo.getSeguro() == null) {
                throw new CreateException("SegurosPrestamoCliente",
                        "El préstamo no tiene información del seguro asociado");
            }

            // Validar que el seguro esté activo
            if (!"ACTIVO".equals(prestamo.getSeguro().getEstado())) {
                throw new CreateException("SegurosPrestamoCliente",
                        "El seguro no está activo: " + prestamo.getSeguro().getEstado());
            }

            // Calcular el monto total del seguro basado en el porcentaje y el monto del
            // préstamo
            java.math.BigDecimal montoTotal = prestamoCliente.getMontoSolicitado()
                    .multiply(prestamo.getSeguro().getMontoAsegurado())
                    .divide(java.math.BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);

            // Calcular la cuota del seguro dividiendo el monto total entre el número de
            // cuotas
            java.math.BigDecimal montoCuota = montoTotal
                    .divide(java.math.BigDecimal.valueOf(prestamoCliente.getPlazoMeses()), 2,
                            java.math.RoundingMode.HALF_UP);

            // Asignar los valores calculados al DTO
            seguroDTO.setMontoTotal(montoTotal);
            seguroDTO.setMontoCuota(montoCuota);

            log.info("Seguro externo validado y calculado exitosamente. Monto total: {}, Cuota: {}",
                    montoTotal, montoCuota);

        } catch (Exception e) {
            log.error("Error al validar y calcular seguro externo: {}", e.getMessage());
            throw new CreateException("SegurosPrestamoCliente",
                    "Error al validar y calcular seguro externo: " + e.getMessage());
        }
    }
}
