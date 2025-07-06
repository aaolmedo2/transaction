package com.banquito.core.loan.transaction.controller;

import com.banquito.core.loan.transaction.DTO.SegurosPrestamoClienteDTO;
import com.banquito.core.loan.transaction.service.SegurosPrestamoClienteService;
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
@RestController
@RequestMapping("/api/v1/seguros-prestamo-cliente")
@Tag(name = "Seguros Préstamo Cliente", description = "API para la gestión de seguros de préstamos de clientes")
public class SegurosPrestamoClienteController {

        @Autowired
        private SegurosPrestamoClienteService segurosService;

        @Operation(summary = "Obtener todos los seguros de préstamos de clientes")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de seguros obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SegurosPrestamoClienteDTO.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @GetMapping
        public ResponseEntity<List<SegurosPrestamoClienteDTO>> findAll() {
                log.info("Solicitando todos los seguros de préstamos de clientes");
                List<SegurosPrestamoClienteDTO> seguros = segurosService.findAll();
                return ResponseEntity.ok(seguros);
        }

        @Operation(summary = "Obtener un seguro por ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Seguro encontrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SegurosPrestamoClienteDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Seguro no encontrado"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @GetMapping("/{id}")
        public ResponseEntity<SegurosPrestamoClienteDTO> findById(
                        @Parameter(description = "ID del seguro", required = true) @PathVariable Integer id) {
                log.info("Solicitando seguro con ID: {}", id);
                SegurosPrestamoClienteDTO seguro = segurosService.findById(id);
                return ResponseEntity.ok(seguro);
        }

        @Operation(summary = "Crear un nuevo seguro de préstamo cliente")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Seguro creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SegurosPrestamoClienteDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @PostMapping
        public ResponseEntity<SegurosPrestamoClienteDTO> create(
                        @Parameter(description = "Datos del seguro a crear", required = true) @Valid @RequestBody SegurosPrestamoClienteDTO seguroDTO) {
                log.info("Creando nuevo seguro para préstamo cliente ID: {}", seguroDTO.getIdPrestamoCliente());
                SegurosPrestamoClienteDTO seguroCreado = segurosService.create(seguroDTO);
                return ResponseEntity.status(HttpStatus.CREATED).body(seguroCreado);
        }

        @Operation(summary = "Eliminar lógicamente un seguro")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Seguro eliminado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SegurosPrestamoClienteDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Seguro no encontrado"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<SegurosPrestamoClienteDTO> deleteLogical(
                        @Parameter(description = "ID del seguro", required = true) @PathVariable Integer id) {
                log.info("Eliminando lógicamente seguro con ID: {}", id);
                SegurosPrestamoClienteDTO seguroEliminado = segurosService.deleteLogical(id);
                return ResponseEntity.ok(seguroEliminado);
        }
}
