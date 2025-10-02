package org.quickstart.exceptions;

public class RegistryException extends QuickStartException {

    private ServiceError serviceError;

    public RegistryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegistryException() {
        super();
    }

    public RegistryException(ServiceError serviceError){
        this.serviceError = serviceError;
        super(serviceError);
    }

    public RegistryException(String message) {
        super(message);
    }

    public ServiceError serviceError() {
        return serviceError;
    }
}
