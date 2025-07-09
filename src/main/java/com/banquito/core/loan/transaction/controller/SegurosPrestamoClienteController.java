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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/seguros-prestamo-cliente")
@Tag(name = "Seguros Préstamo Cliente", description = "API para la gestión de seguros de préstamos de clientes")
public class SegurosPrestamoClienteController {

        @Autowired
        private SegurosPrestamoClienteService segurosService;

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
