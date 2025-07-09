package com.banquito.core.loan.transaction.controller;

import com.banquito.core.loan.transaction.DTO.ComisionesPrestamoClienteDTO;
import com.banquito.core.loan.transaction.service.ComisionesPrestamoClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/comisiones-prestamo-cliente")
@Tag(name = "Comisiones Préstamo Cliente", description = "API para la gestión de comisiones de préstamos de clientes")
public class ComisionesPrestamoClienteController {

        @Autowired
        private ComisionesPrestamoClienteService comisionesService;

        @Operation(summary = "Obtener una comisión por ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Comisión encontrada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComisionesPrestamoClienteDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Comisión no encontrada"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @GetMapping("/{id}")
        public ResponseEntity<ComisionesPrestamoClienteDTO> findById(
                        @Parameter(description = "ID de la comisión", required = true) @PathVariable Integer id) {
                log.info("Solicitando comisión con ID: {}", id);
                ComisionesPrestamoClienteDTO comision = comisionesService.findById(id);
                return ResponseEntity.ok(comision);
        }

        @Operation(summary = "Eliminar lógicamente una comisión")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Comisión eliminada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComisionesPrestamoClienteDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Comisión no encontrada"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<ComisionesPrestamoClienteDTO> deleteLogical(
                        @Parameter(description = "ID de la comisión", required = true) @PathVariable Integer id) {
                log.info("Eliminando lógicamente comisión con ID: {}", id);
                ComisionesPrestamoClienteDTO comisionEliminada = comisionesService.deleteLogical(id);
                return ResponseEntity.ok(comisionEliminada);
        }
}
