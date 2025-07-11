package com.banquito.core.loan.transaction.enums;

public enum EstadoPagoEnum {
    ACTIVO("ACTIVO"),
    INACTIVO("INACTIVO"),
    COMPLETADO("COMPLETADO"),
    REVERTIDO("REVERTIDO");

    private final String valor;

    EstadoPagoEnum(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}