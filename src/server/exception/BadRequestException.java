package server.exception;

import java.util.logging.Logger;

public class BadRequestException extends RuntimeException {

    private static final Logger logger = Logger.getLogger(BadRequestException.class.getName());

    public BadRequestException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return super.fillInStackTrace();
    }
}
