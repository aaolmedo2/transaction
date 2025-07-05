package com.banquito.core.loan.transaction.exception;

public class DeleteException extends RuntimeException {

    private final Integer errorCode;
    private final String entityName;

    public DeleteException(String entityName, String message) {

        super(message);
        this.errorCode = 4;
        this.entityName = entityName;
    }

    @Override
    public String getMessage() {
        return "Error Code: " + errorCode + "; Entity Name: " + entityName + "; Message: " + super.getMessage();
    }
}
