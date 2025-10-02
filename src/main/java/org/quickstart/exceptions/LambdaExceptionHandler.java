package org.quickstart.exceptions;

import java.util.function.Consumer;

@FunctionalInterface
public interface LambdaExceptionHandler<T, E extends Exception> {

    void accept(T t) throws E;

     static <T>Consumer<T> handle(LambdaExceptionHandler<T, ?> le, ServiceError error) {
        return consumer -> {
            try {
                le.accept(consumer);
            }catch(Exception e) {
                throw new QuickStartException(error);
            }
        };
    }

}
