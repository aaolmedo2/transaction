package com.banquito.core.loan.transaction.controller;

import com.banquito.core.loan.transaction.DTO.PrestamosClienteDTO;
import com.banquito.core.loan.transaction.service.PrestamosClienteService;
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
import java.util.List;

@Slf4j
@CrossOrigin(origins = "http://ec2-18-116-67-249.us-east-2.compute.amazonaws.com:4174/")
@RestController
@RequestMapping("/api/v1/prestamos-clientes")
@Tag(name = "Préstamos Clientes", description = "API para la gestión de préstamos de clientes")
public class PrestamosClienteController {

        @Autowired
        private PrestamosClienteService prestamosClienteService;

        @Operation(summary = "Obtener todos los préstamos de clientes")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de préstamos obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PrestamosClienteDTO.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @GetMapping
        public ResponseEntity<List<PrestamosClienteDTO>> findAll() {
                log.info("Solicitando todos los préstamos de clientes");
                List<PrestamosClienteDTO> prestamos = prestamosClienteService.findAll();
                return ResponseEntity.ok(prestamos);
        }

        @Operation(summary = "Obtener un préstamo de cliente por ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Préstamo encontrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PrestamosClienteDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Préstamo no encontrado"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @GetMapping("/{id}")
        public ResponseEntity<PrestamosClienteDTO> findById(
                        @Parameter(description = "ID del préstamo cliente", required = true) @PathVariable Integer id) {
                log.info("Solicitando préstamo cliente con ID: {}", id);
                PrestamosClienteDTO prestamo = prestamosClienteService.findById(id);
                return ResponseEntity.ok(prestamo);
        }

        @Operation(summary = "Crear un nuevo préstamo de cliente")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Préstamo creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PrestamosClienteDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @PostMapping
        public ResponseEntity<PrestamosClienteDTO> create(
                        @Parameter(description = "Datos del préstamo cliente a crear", required = true) @Valid @RequestBody PrestamosClienteDTO prestamosClienteDTO) {
                log.info("Creando nuevo préstamo cliente para cliente: {}", prestamosClienteDTO.getIdCliente());
                PrestamosClienteDTO prestamoCreado = prestamosClienteService.create(prestamosClienteDTO);
                return ResponseEntity.status(HttpStatus.CREATED).body(prestamoCreado);
        }

        @Operation(summary = "Actualizar un préstamo de cliente existente")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Préstamo actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PrestamosClienteDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                        @ApiResponse(responseCode = "404", description = "Préstamo no encontrado"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @PutMapping("/{id}")
        public ResponseEntity<PrestamosClienteDTO> update(
                        @Parameter(description = "ID del préstamo cliente", required = true) @PathVariable Integer id,
                        @Parameter(description = "Datos del préstamo cliente a actualizar", required = true) @Valid @RequestBody PrestamosClienteDTO prestamosClienteDTO) {
                log.info("Actualizando préstamo cliente con ID: {}", id);
                PrestamosClienteDTO prestamoActualizado = prestamosClienteService.update(id, prestamosClienteDTO);
                return ResponseEntity.ok(prestamoActualizado);
        }

        @Operation(summary = "Actualizar el estado de un préstamo de cliente")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PrestamosClienteDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Estado inválido"),
                        @ApiResponse(responseCode = "404", description = "Préstamo no encontrado"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @PatchMapping("/{id}/estado")
        public ResponseEntity<PrestamosClienteDTO> updateEstado(
                        @Parameter(description = "ID del préstamo cliente", required = true) @PathVariable Integer id,
                        @Parameter(description = "Nuevo estado del préstamo", required = true) @RequestParam String estado) {
                log.info("Actualizando estado del préstamo cliente con ID: {} a estado: {}", id, estado);
                PrestamosClienteDTO prestamoActualizado = prestamosClienteService.updateEstado(id, estado);
                return ResponseEntity.ok(prestamoActualizado);
        }
}
