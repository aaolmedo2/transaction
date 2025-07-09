package com.banquito.core.loan.transaction.controller;

import com.banquito.core.loan.transaction.DTO.GarantiasTiposPrestamosClienteDTO;
import com.banquito.core.loan.transaction.service.GarantiasTiposPrestamosClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/garantias-tipos-prestamos-cliente")
@Tag(name = "Garantías Tipos Préstamos Cliente", description = "API para la gestión de garantías de tipos de préstamos de clientes")
public class GarantiasTiposPrestamosClienteController {

        @Autowired
        private GarantiasTiposPrestamosClienteService garantiasService;

        @Operation(summary = "Obtener una garantía por ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Garantía encontrada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GarantiasTiposPrestamosClienteDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Garantía no encontrada"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @GetMapping("/{id}")
        public ResponseEntity<GarantiasTiposPrestamosClienteDTO> findById(
                        @Parameter(description = "ID de la garantía", required = true) @PathVariable Integer id) {
                log.info("Solicitando garantía con ID: {}", id);
                GarantiasTiposPrestamosClienteDTO garantia = garantiasService.findById(id);
                return ResponseEntity.ok(garantia);
        }

        @Operation(summary = "Crear una nueva garantía de tipo préstamo cliente")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Garantía creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GarantiasTiposPrestamosClienteDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @PostMapping
        public ResponseEntity<GarantiasTiposPrestamosClienteDTO> create(
                        @Parameter(description = "Datos de la garantía a crear", required = true) @Valid @RequestBody GarantiasTiposPrestamosClienteDTO garantiaDTO) {
                log.info("Creando nueva garantía para préstamo cliente ID: {}", garantiaDTO.getIdPrestamoCliente());
                GarantiasTiposPrestamosClienteDTO garantiaCreada = garantiasService.create(garantiaDTO);
                return ResponseEntity.status(HttpStatus.CREATED).body(garantiaCreada);
        }

        @Operation(summary = "Eliminar lógicamente una garantía")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Garantía eliminada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GarantiasTiposPrestamosClienteDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Garantía no encontrada"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<GarantiasTiposPrestamosClienteDTO> deleteLogical(
                        @Parameter(description = "ID de la garantía", required = true) @PathVariable Integer id) {
                log.info("Eliminando lógicamente garantía con ID: {}", id);
                GarantiasTiposPrestamosClienteDTO garantiaEliminada = garantiasService.deleteLogical(id);
                return ResponseEntity.ok(garantiaEliminada);
        }
}
