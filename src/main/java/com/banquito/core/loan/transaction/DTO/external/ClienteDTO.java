package com.banquito.core.loan.transaction.DTO.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    private String id; // ID del cliente como string (ej: "1753898188")
    private String numeroIdentificacion;
    private String tipoIdentificacion;
    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;
    private LocalDate fechaNacimiento;
    private String estado; // ACTIVO, INACTIVO, etc.

    // Agregar más campos según la estructura real del microservicio de clientes
}
