package com.banquito.core.loan.transaction.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banquito.core.loan.transaction.DTO.CronogramasPagoDTO;
import com.banquito.core.loan.transaction.enums.EstadoCronogramaEnum;
import com.banquito.core.loan.transaction.enums.EstadoPrestamoClienteEnum;
import com.banquito.core.loan.transaction.exception.CreateException;
import com.banquito.core.loan.transaction.exception.EntityNotFoundException;
import com.banquito.core.loan.transaction.modelo.ComisionesPrestamoCliente;
import com.banquito.core.loan.transaction.modelo.CronogramasPago;
import com.banquito.core.loan.transaction.modelo.PrestamosCliente;
import com.banquito.core.loan.transaction.modelo.SegurosPrestamoCliente;
import com.banquito.core.loan.transaction.repositorio.ComisionesPrestamoClienteRepositorio;
import com.banquito.core.loan.transaction.repositorio.CronogramasPagoRepositorio;
import com.banquito.core.loan.transaction.repositorio.PrestamosClienteRepositorio;
import com.banquito.core.loan.transaction.repositorio.SegurosPrestamoClienteRepositorio;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CronogramasPagoService {

    private final CronogramasPagoRepositorio cronogramasPagoRepository;
    private final PrestamosClienteRepositorio prestamosClienteRepository;
    private final ComisionesPrestamoClienteRepositorio comisionesRepository;
    private final SegurosPrestamoClienteRepositorio segurosRepository;

    public CronogramasPagoService(CronogramasPagoRepositorio cronogramasPagoRepository,
            PrestamosClienteRepositorio prestamosClienteRepository,
            ComisionesPrestamoClienteRepositorio comisionesRepository,
            SegurosPrestamoClienteRepositorio segurosRepository) {
        this.cronogramasPagoRepository = cronogramasPagoRepository;
        this.prestamosClienteRepository = prestamosClienteRepository;
        this.comisionesRepository = comisionesRepository;
        this.segurosRepository = segurosRepository;
    }

    public List<CronogramasPagoDTO> obtenerPorPrestamoCliente(Integer idPrestamoCliente) {
        log.info("Obteniendo cronogramas de pagos por préstamo cliente con ID: {}", idPrestamoCliente);
        Optional<PrestamosCliente> prestamoClienteOpt = this.prestamosClienteRepository.findById(idPrestamoCliente);
        if (!prestamoClienteOpt.isPresent()) {
            throw new EntityNotFoundException("Préstamo Cliente",
                    "No se encontró el préstamo cliente con id: " + idPrestamoCliente);
        }

        List<CronogramasPago> cronogramas = this.cronogramasPagoRepository
                .findByIdPrestamoClienteOrderByNumeroCuotaAsc(prestamoClienteOpt.get());
        List<CronogramasPagoDTO> cronogramasDTO = new ArrayList<>();
        for (CronogramasPago cronograma : cronogramas) {
            cronogramasDTO.add(this.transformarADTO(cronograma));
        }
        return cronogramasDTO;
    }

    public CronogramasPagoDTO obtenerPorId(Integer id) {
        log.info("Obteniendo cronograma de pago por ID: {}", id);
        Optional<CronogramasPago> cronogramaOpt = this.cronogramasPagoRepository.findById(id);
        if (cronogramaOpt.isPresent()) {
            return this.transformarADTO(cronogramaOpt.get());
        } else {
            throw new EntityNotFoundException("Cronograma de Pago",
                    "No se encontró el cronograma de pago con id: " + id);
        }
    }

    @Transactional
    public List<CronogramasPagoDTO> generarCronogramaPagos(Integer idPrestamoCliente) {
        log.info("Generando cronograma de pagos para préstamo cliente con ID: {}", idPrestamoCliente);
        Optional<PrestamosCliente> prestamoClienteOpt = this.prestamosClienteRepository.findById(idPrestamoCliente);
        if (!prestamoClienteOpt.isPresent()) {
            throw new EntityNotFoundException("Préstamo Cliente",
                    "No se encontró el préstamo cliente con id: " + idPrestamoCliente);
        }

        PrestamosCliente prestamoCliente = prestamoClienteOpt.get();

        // Verificar que el estado sea APROBADO
        if (!prestamoCliente.getEstado().equals(EstadoPrestamoClienteEnum.APROBADO.getValor())) {
            throw new CreateException("Cronograma de Pago",
                    "No se puede generar el cronograma de pagos porque el préstamo cliente no está en estado APROBADO");
        }

        // Verificar que no existan cronogramas previos
        List<CronogramasPago> cronogramasExistentes = this.cronogramasPagoRepository
                .findByIdPrestamoCliente(prestamoCliente);
        if (!cronogramasExistentes.isEmpty()) {
            throw new CreateException("Cronograma de Pago",
                    "Ya existe un cronograma de pagos para este préstamo cliente");
        }

        try {
            List<CronogramasPagoDTO> cronogramasGenerados = new ArrayList<>();

            // Obtener datos necesarios para el cálculo
            BigDecimal montoSolicitado = prestamoCliente.getMontoSolicitado();
            Integer plazoMeses = prestamoCliente.getPlazoMeses();
            BigDecimal tasaInteresAnual = prestamoCliente.getTasaInteresAplicada();
            BigDecimal tasaInteresMensual = tasaInteresAnual.divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP)
                    .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP); // Convertir a decimal

            // Fecha de inicio para las cuotas (mes siguiente al desembolso)
            LocalDate fechaInicio = prestamoCliente.getFechaDesembolso().withDayOfMonth(1).plusMonths(1);

            // Cálculo del monto de la cuota utilizando la fórmula de amortización francesa
            BigDecimal montoCuota = calcularCuotaFrancesa(montoSolicitado, tasaInteresMensual, plazoMeses);
            log.info("Monto de la cuota calculada: {}", montoCuota);

            // Obtener comisiones y seguros asociados al préstamo
            BigDecimal comisionValue = obtenerComision(prestamoCliente);
            BigDecimal seguroCuota = obtenerSeguroCuota(prestamoCliente);

            log.info("Comisión para el préstamo: {}", comisionValue);
            log.info("Seguro por cuota para el préstamo: {}", seguroCuota);

            // Generación del cronograma
            BigDecimal saldoPendiente = montoSolicitado;

            for (int i = 1; i <= plazoMeses; i++) {
                // Calculamos el interés del periodo
                BigDecimal interesPeriodo = saldoPendiente.multiply(tasaInteresMensual).setScale(2,
                        RoundingMode.HALF_UP);

                // Calculamos el capital amortizado en este periodo
                BigDecimal capitalAmortizado = montoCuota.subtract(interesPeriodo).setScale(2, RoundingMode.HALF_UP);

                // Ajuste para la última cuota (para asegurar que el saldo llegue exactamente a
                // cero)
                if (i == plazoMeses) {
                    capitalAmortizado = saldoPendiente;
                    montoCuota = capitalAmortizado.add(interesPeriodo);
                }

                // Calculamos el saldo después de esta cuota
                saldoPendiente = saldoPendiente.subtract(capitalAmortizado).setScale(2, RoundingMode.HALF_UP);

                // Fecha programada para esta cuota
                LocalDate fechaProgramada = fechaInicio.plusMonths(i - 1);

                // Crear el cronograma para esta cuota
                CronogramasPago cronograma = new CronogramasPago();
                cronograma.setIdPrestamoCliente(prestamoCliente);
                cronograma.setNumeroCuota(i);
                cronograma.setFechaProgramada(fechaProgramada);
                cronograma.setMontoCuota(capitalAmortizado);
                cronograma.setInteres(interesPeriodo);

                // Asignar comisión (solo para la primera cuota, ya que la comisión se cobra una
                // sola vez)
                BigDecimal comisionCuota = (i == 1) ? comisionValue : BigDecimal.ZERO;
                cronograma.setComisiones(comisionCuota);

                // Asignar seguro para esta cuota
                cronograma.setSeguros(seguroCuota);

                // Calcular el total de la cuota sumando capital, interés, comisiones y seguros
                BigDecimal total = capitalAmortizado.add(interesPeriodo)
                        .add(cronograma.getComisiones())
                        .add(cronograma.getSeguros())
                        .setScale(2, RoundingMode.HALF_UP);
                cronograma.setTotal(total);

                // Establecemos el saldo pendiente después de esta cuota
                cronograma.setSaldoPendiente(saldoPendiente);
                cronograma.setEstado(EstadoCronogramaEnum.PENDIENTE.getValor());
                cronograma.setVersion(1L);

                CronogramasPago cronogramaGuardado = this.cronogramasPagoRepository.save(cronograma);
                cronogramasGenerados.add(this.transformarADTO(cronogramaGuardado));

                log.info("Cuota {} generada: capital={}, interés={}, comisión={}, seguro={}, total={}, saldo={}",
                        i, capitalAmortizado, interesPeriodo, comisionCuota, seguroCuota, total, saldoPendiente);
            }

            // Actualizar el estado del préstamo cliente a DESEMBOLSADO
            prestamoCliente.setEstado(EstadoPrestamoClienteEnum.DESEMBOLSADO.getValor());
            this.prestamosClienteRepository.save(prestamoCliente);

            return cronogramasGenerados;
        } catch (Exception e) {
            log.error("Error al generar el cronograma de pagos", e);
            throw new CreateException("Cronograma de Pago",
                    "Error al generar el cronograma de pagos: " + e.getMessage());
        }
    }

    private BigDecimal obtenerComision(PrestamosCliente prestamoCliente) {
        log.info("Obteniendo comisión para el préstamo cliente ID: {}", prestamoCliente.getId());
        List<ComisionesPrestamoCliente> comisiones = comisionesRepository.findByIdPrestamoCliente(prestamoCliente);

        if (comisiones.isEmpty()) {
            log.warn("No se encontraron comisiones asociadas al préstamo cliente ID: {}", prestamoCliente.getId());
            return BigDecimal.ZERO;
        }

        // Tomamos la primera comisión activa o pendiente
        for (ComisionesPrestamoCliente comision : comisiones) {
            if ("ACTIVO".equals(comision.getEstado()) || "PENDIENTE".equals(comision.getEstado())) {
                return comision.getMonto();
            }
        }

        log.warn("No se encontraron comisiones activas o pendientes para el préstamo cliente ID: {}",
                prestamoCliente.getId());
        return BigDecimal.ZERO;
    }

    private BigDecimal obtenerSeguroCuota(PrestamosCliente prestamoCliente) {
        log.info("Obteniendo seguro para el préstamo cliente ID: {}", prestamoCliente.getId());
        List<SegurosPrestamoCliente> seguros = segurosRepository.findByIdPrestamoCliente(prestamoCliente);

        if (seguros.isEmpty()) {
            log.warn("No se encontraron seguros asociados al préstamo cliente ID: {}", prestamoCliente.getId());
            return BigDecimal.ZERO;
        }

        // Tomamos el primer seguro activo
        for (SegurosPrestamoCliente seguro : seguros) {
            if ("ACTIVO".equals(seguro.getEstado())) {
                return seguro.getMontoCuota();
            }
        }

        log.warn("No se encontraron seguros activos para el préstamo cliente ID: {}", prestamoCliente.getId());
        return BigDecimal.ZERO;
    }

    private BigDecimal calcularCuotaFrancesa(BigDecimal montoSolicitado, BigDecimal tasaInteresMensual,
            Integer plazoMeses) {
        // Fórmula de amortización francesa: C = P * (r * (1 + r)^n) / ((1 + r)^n - 1)
        // Donde C = cuota, P = monto del préstamo, r = tasa de interés mensual, n =
        // plazo en meses

        double r = tasaInteresMensual.doubleValue();
        double p = montoSolicitado.doubleValue();
        int n = plazoMeses;

        double factor = Math.pow(1 + r, n);
        double numerador = r * factor;
        double denominador = factor - 1;
        double cuota = p * (numerador / denominador);

        return new BigDecimal(cuota).setScale(2, RoundingMode.HALF_UP);
    }

    private CronogramasPagoDTO transformarADTO(CronogramasPago cronograma) {
        return CronogramasPagoDTO.builder()
                .id(cronograma.getId())
                .idPrestamoCliente(cronograma.getIdPrestamoCliente().getId())
                .numeroCuota(cronograma.getNumeroCuota())
                .fechaProgramada(cronograma.getFechaProgramada())
                .montoCuota(cronograma.getMontoCuota())
                .interes(cronograma.getInteres())
                .comisiones(cronograma.getComisiones())
                .seguros(cronograma.getSeguros())
                .total(cronograma.getTotal())
                .saldoPendiente(cronograma.getSaldoPendiente())
                .estado(cronograma.getEstado())
                .version(cronograma.getVersion())
                .build();
    }
}
