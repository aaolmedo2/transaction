package com.banquito.core.loan.transaction.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banquito.core.loan.transaction.DTO.PagosPrestamoDTO;
import com.banquito.core.loan.transaction.enums.EstadoCronogramaEnum;
import com.banquito.core.loan.transaction.enums.EstadoPagoEnum;
import com.banquito.core.loan.transaction.exception.CreateException;
import com.banquito.core.loan.transaction.exception.EntityNotFoundException;
import com.banquito.core.loan.transaction.modelo.CronogramasPago;
import com.banquito.core.loan.transaction.modelo.PagosPrestamo;
import com.banquito.core.loan.transaction.repositorio.CronogramasPagoRepositorio;
import com.banquito.core.loan.transaction.repositorio.PagosPrestamoRepositorio;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PagosPrestamoService {

    private final PagosPrestamoRepositorio pagosPrestamoRepository;
    private final CronogramasPagoRepositorio cronogramasPagoRepository;

    public PagosPrestamoService(PagosPrestamoRepositorio pagosPrestamoRepository,
            CronogramasPagoRepositorio cronogramasPagoRepository) {
        this.pagosPrestamoRepository = pagosPrestamoRepository;
        this.cronogramasPagoRepository = cronogramasPagoRepository;
    }

    public PagosPrestamoDTO obtenerPorId(Integer id) {
        log.info("Obteniendo pago de préstamo por ID: {}", id);
        Optional<PagosPrestamo> pagoOpt = this.pagosPrestamoRepository.findById(id);
        if (pagoOpt.isPresent()) {
            return this.transformarADTO(pagoOpt.get());
        } else {
            throw new EntityNotFoundException("Pago de Préstamo", "No se encontró el pago con id: " + id);
        }
    }

    @Transactional
    public PagosPrestamoDTO registrarPago(Integer idCuota, String tipoPago, String referencia) {
        log.info("Registrando pago para la cuota con ID: {}", idCuota);
        try {
            Optional<CronogramasPago> cuotaOpt = this.cronogramasPagoRepository.findById(idCuota);
            if (!cuotaOpt.isPresent()) {
                throw new EntityNotFoundException("Cronograma de Pago", "No se encontró la cuota con id: " + idCuota);
            }

            CronogramasPago cuota = cuotaOpt.get();

            // Verificar que la cuota esté pendiente de pago
            if (!cuota.getEstado().equals(EstadoCronogramaEnum.PENDIENTE.getValor())) {
                throw new CreateException("Pago de Préstamo",
                        "La cuota ya ha sido pagada o no está en estado pendiente");
            }

            // Crear el pago con los valores de la cuota
            PagosPrestamo pago = new PagosPrestamo();
            pago.setIdCuota(cuota);
            pago.setFechaPago(LocalDate.now());
            pago.setMontoPagado(cuota.getTotal());
            pago.setInteresPagado(cuota.getInteres());
            pago.setMoraPagada(java.math.BigDecimal.ZERO); // No calculamos mora en este caso
            pago.setCapitalPagado(cuota.getMontoCuota());
            pago.setTipoPago(tipoPago);
            pago.setReferencia(referencia);
            pago.setEstado(EstadoPagoEnum.COMPLETADO.getValor());
            pago.setVersion(1L);

            // Guardar el pago
            PagosPrestamo pagoGuardado = this.pagosPrestamoRepository.save(pago);

            // Actualizar el estado de la cuota a PAGADO
            cuota.setEstado(EstadoCronogramaEnum.PAGADO.getValor());
            this.cronogramasPagoRepository.save(cuota);

            return this.transformarADTO(pagoGuardado);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al registrar el pago", e);
            throw new CreateException("Pago de Préstamo", "Error al registrar el pago: " + e.getMessage());
        }
    }

    private PagosPrestamoDTO transformarADTO(PagosPrestamo pago) {
        return PagosPrestamoDTO.builder()
                .id(pago.getId())
                .idCuota(pago.getIdCuota().getId())
                .fechaPago(pago.getFechaPago())
                .montoPagado(pago.getMontoPagado())
                .interesPagado(pago.getInteresPagado())
                .moraPagada(pago.getMoraPagada())
                .capitalPagado(pago.getCapitalPagado())
                .tipoPago(pago.getTipoPago())
                .referencia(pago.getReferencia())
                .estado(pago.getEstado())
                .version(pago.getVersion())
                .build();
    }
}
