package com.banquito.core.loan.transaction.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO para la gestión de comisiones de préstamos de clientes")
public class ComisionesPrestamoClienteDTO {

    @Schema(description = "ID único de la comisión", example = "1")
    private Integer id;

    @NotNull(message = "El ID del préstamo cliente es requerido")
    @Positive(message = "El ID del préstamo cliente debe ser positivo")
    @Schema(description = "ID del préstamo cliente", example = "1", required = true)
    private Integer idPrestamoCliente;

    @NotBlank(message = "El ID de la comisión del préstamo es requerido")
    @Schema(description = "ID de la comisión del préstamo", example = "507f1f77bcf86cd799439013", required = true)
    private String idComisionPrestamo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Fecha de aplicación de la comisión", example = "2024-01-15")
    private LocalDate fechaAplicacion;

    @Schema(description = "Monto de la comisión (calculado automáticamente)", example = "150.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal montoComision;

    @Schema(description = "Estado de la comisión", example = "ACT")
    private String estado;

    @Schema(description = "Versión para control de concurrencia", example = "1")
    private Long version;
}
