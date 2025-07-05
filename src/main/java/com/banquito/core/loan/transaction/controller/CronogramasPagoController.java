package com.banquito.core.loan.transaction.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banquito.core.loan.transaction.DTO.CronogramasPagoDTO;
import com.banquito.core.loan.transaction.service.CronogramasPagoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/cronogramas-pagos")
@Tag(name = "Cronogramas de Pagos", description = "API para gestionar cronogramas de pagos de préstamos")
@Slf4j
public class CronogramasPagoController {

        private final CronogramasPagoService cronogramasPagoService;

        public CronogramasPagoController(CronogramasPagoService cronogramasPagoService) {
                this.cronogramasPagoService = cronogramasPagoService;
        }

        @Operation(summary = "Obtener cronograma de pago por ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cronograma de pago encontrado", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = CronogramasPagoDTO.class)) }),
                        @ApiResponse(responseCode = "404", description = "Cronograma de pago no encontrado", content = @Content)
        })
        @GetMapping("/{id}")
        public ResponseEntity<CronogramasPagoDTO> obtenerPorId(
                        @Parameter(description = "ID del cronograma de pago") @PathVariable Integer id) {
                log.info("Obteniendo cronograma de pago por ID: {}", id);
                return ResponseEntity.ok(this.cronogramasPagoService.obtenerPorId(id));
        }

        @Operation(summary = "Obtener cronogramas de pagos por préstamo cliente")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cronogramas de pagos encontrados", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = CronogramasPagoDTO.class)) }),
                        @ApiResponse(responseCode = "204", description = "No hay cronogramas de pagos para ese préstamo cliente", content = @Content)
        })
        @GetMapping("/prestamo-cliente/{idPrestamoCliente}")
        public ResponseEntity<List<CronogramasPagoDTO>> obtenerPorPrestamoCliente(
                        @Parameter(description = "ID del préstamo cliente") @PathVariable Integer idPrestamoCliente) {
                log.info("Obteniendo cronogramas de pagos por préstamo cliente con ID: {}", idPrestamoCliente);
                List<CronogramasPagoDTO> cronogramas = this.cronogramasPagoService
                                .obtenerPorPrestamoCliente(idPrestamoCliente);
                if (cronogramas.isEmpty()) {
                        return ResponseEntity.noContent().build();
                }
                return ResponseEntity.ok(cronogramas);
        }

        @Operation(summary = "Generar cronograma de pagos para un préstamo cliente")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cronograma de pagos generado", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = CronogramasPagoDTO.class)) }),
                        @ApiResponse(responseCode = "400", description = "Error al generar el cronograma de pagos", content = @Content)
        })
        @PostMapping("/generar/{idPrestamoCliente}")
        public ResponseEntity<List<CronogramasPagoDTO>> generarCronogramas(
                        @Parameter(description = "ID del préstamo cliente") @PathVariable Integer idPrestamoCliente) {
                log.info("Generando cronograma de pagos para préstamo cliente con ID: {}", idPrestamoCliente);
                List<CronogramasPagoDTO> cronogramas = this.cronogramasPagoService
                                .generarCronogramaPagos(idPrestamoCliente);
                return ResponseEntity.ok(cronogramas);
        }
}
