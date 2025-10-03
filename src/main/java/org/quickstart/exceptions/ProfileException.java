package org.quickstart.exceptions;

public class ProfileException extends QuickStartException {
    private ServiceError serviceError;

    public ProfileException(String message) {
        super(message);
    }

    public ProfileException(ServiceError serviceError) {
        this.serviceError = serviceError;
        super(serviceError);
    }
}
