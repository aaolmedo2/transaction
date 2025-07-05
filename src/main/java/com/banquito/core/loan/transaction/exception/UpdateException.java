package com.banquito.core.loan.transaction.exception;

public class UpdateException extends RuntimeException {

    private final Integer errorCode;
    private final String entityName;

    public UpdateException(String entityName, String message) {
        super(message);
        this.errorCode = 3;
        this.entityName = entityName;
    }

    @Override
    public String getMessage() {
        return "Error Code: " + errorCode + "; Entity Name: " + entityName + "; Message: " + super.getMessage();
    }
}
