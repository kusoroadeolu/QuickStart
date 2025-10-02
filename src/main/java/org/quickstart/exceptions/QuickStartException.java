package org.quickstart.exceptions;

public class QuickStartException extends RuntimeException {

    private ServiceError serviceError;


    public QuickStartException(String message, Throwable cause) {
        super(message, cause);
    }

    public QuickStartException(String message) {
        super(message);
    }

    public QuickStartException() {
    }

    public QuickStartException(ServiceError  serviceError) {
        this.serviceError = serviceError;
    }

    public ServiceError serviceError() {
        return serviceError;
    }
}
