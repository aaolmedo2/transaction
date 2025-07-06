package com.banquito.core.loan.transaction.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor; // <--- AÑADE ESTA LÍNEA
import lombok.AllArgsConstructor; // <--- Y AÑADE ESTA OTRA LÍNEA

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor // <--- AÑADE ESTA ANOTACIÓN
@AllArgsConstructor // <--- AÑADE ESTA ANOTACIÓN
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO para la gestión de préstamos de clientes")
public class PrestamosClienteDTO {

    @Schema(description = "ID único del préstamo cliente", example = "1")
    private Integer id;

    @NotNull(message = "El ID del cliente es requerido")
    @Positive(message = "El ID del cliente debe ser mayor a cero")
    @Schema(description = "ID único del cliente", example = "12345", required = true)
    private Integer idCliente;

    @NotBlank(message = "El ID del préstamo es requerido")
    @Schema(description = "ID único del préstamo", example = "507f1f77bcf86cd799439012", required = true)
    private String idPrestamo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Fecha de inicio del préstamo", example = "2024-01-15")
    private LocalDate fechaInicio;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Fecha de aprobación del préstamo", example = "2024-01-16")
    private LocalDate fechaAprobacion;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Fecha de desembolso del préstamo", example = "2024-01-17")
    private LocalDate fechaDesembolso;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Fecha de vencimiento del préstamo", example = "2026-01-17")
    private LocalDate fechaVencimiento;

    @NotNull(message = "El monto solicitado es requerido")
    @Positive(message = "El monto solicitado debe ser mayor a cero")
    @DecimalMin(value = "0.01", message = "El monto solicitado debe ser mayor a cero")
    @Schema(description = "Monto solicitado para el préstamo", example = "15000.00", required = true)
    private BigDecimal montoSolicitado;

    @NotNull(message = "El plazo en meses es requerido")
    @Min(value = 1, message = "El plazo debe ser mínimo 1 mes")
    @Max(value = 360, message = "El plazo no puede ser mayor a 360 meses")
    @Schema(description = "Plazo del préstamo en meses", example = "24", required = true)
    private Integer plazoMeses;

    @NotNull(message = "La tasa de interés es requerida")
    @DecimalMin(value = "0.01", message = "La tasa de interés debe ser mayor a cero")
    @Schema(description = "Tasa de interés aplicada al préstamo", example = "12.50", required = true)
    private BigDecimal tasaInteresAplicada;

    @Schema(description = "Estado del préstamo", example = "PEN")
    private String estado;

    @Schema(description = "Versión para control de concurrencia", example = "1")
    private Long version;
}