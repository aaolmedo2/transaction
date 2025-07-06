package com.banquito.core.loan.transaction.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO para la gestión de garantías de tipos de préstamos de clientes")
public class GarantiasTiposPrestamosClienteDTO {

    @Schema(description = "ID único de la garantía", example = "1")
    private Integer id;

    @NotNull(message = "El ID del préstamo cliente es requerido")
    @Positive(message = "El ID del préstamo cliente debe ser positivo")
    @Schema(description = "ID del préstamo cliente", example = "1", required = true)
    private Integer idPrestamoCliente;

    @NotBlank(message = "El ID de la garantía tipo préstamo es requerido")
    @Schema(description = "ID de la garantía tipo préstamo", example = "507f1f77bcf86cd799439015", required = true)
    private String idGarantiaTipoPrestamo;

    @NotNull(message = "El monto tasado es requerido")
    @DecimalMin(value = "0.01", message = "El monto tasado debe ser mayor a cero")
    @Schema(description = "Monto tasado de la garantía", example = "15000.00", required = true)
    private BigDecimal montoTasado;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Fecha de registro de la garantía", example = "2024-01-15")
    private LocalDate fechaRegistro;

    @Schema(description = "Descripción de la garantía", example = "Vehículo marca Toyota modelo 2020")
    private String descripcion;

    @NotBlank(message = "El documento de referencia es requerido")
    @Schema(description = "Documento de referencia", example = "DOC-RF5689", required = true)
    private String documentoReferencia;

    @Schema(description = "Estado de la garantía", example = "ACTIVO")
    private String estado;

    @Schema(description = "Versión para control de concurrencia", example = "1")
    private Long version;
}
