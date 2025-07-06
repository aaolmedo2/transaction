package com.banquito.core.loan.transaction.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO para la gestión de seguros de préstamos de clientes")
public class SegurosPrestamoClienteDTO {

    @Schema(description = "ID único del seguro", example = "1")
    private Integer id;

    @NotNull(message = "El ID del préstamo cliente es requerido")
    @Positive(message = "El ID del préstamo cliente debe ser positivo")
    @Schema(description = "ID del préstamo cliente", example = "1", required = true)
    private Integer idPrestamoCliente;

    @NotBlank(message = "El ID del seguro del préstamo es requerido")
    @Schema(description = "ID del seguro del préstamo", example = "507f1f77bcf86cd799439014", required = true)
    private String idSeguroPrestamo;

    @Schema(description = "Monto total del seguro (calculado automáticamente)", example = "1000.00")
    private BigDecimal montoTotal;

    @Schema(description = "Monto de la cuota del seguro (calculado automáticamente)", example = "50.00")
    private BigDecimal montoCuota;

    @Schema(description = "Estado del seguro", example = "ACTIVO")
    private String estado;

    @Schema(description = "Versión para control de concurrencia", example = "1")
    private Long version;
}
