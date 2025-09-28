package org.quickstart.exceptions;

public class ServiceError {

    private String message;
    private String hint;
    private Throwable stackTrace;

    public ServiceError(String message, String hint, Throwable stackTrace) {
        this.message = message;
        this.stackTrace = stackTrace;
        this.hint = hint;
    }

    public ServiceError(String message, String hint) {
        this.message = message;
        this.hint = hint;
    }

    public String message() {
        return message;
    }

    public String hint() {
        return hint;
    }

    public Throwable stackTrace() {
        return stackTrace;
    }

    public String toString(boolean showStack) {
        return showStack ? String.format("""
                error: (%s),
                help: (%s),
                Stack Trace: (%s)
                """, this.message, this.hint, this.stackTrace) :
                String.format("""
                error: (%s),
                help: (%s)
                """, this.message, this.hint);


    }
}
