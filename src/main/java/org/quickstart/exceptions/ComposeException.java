package org.quickstart.exceptions;

public class ComposeException extends QuickStartException {
    private ServiceError serviceError;

    public ComposeException(String message) {
        super(message);
    }

    public ComposeException(ServiceError serviceError) {
        this.serviceError = serviceError;
        super(serviceError);
    }
}
