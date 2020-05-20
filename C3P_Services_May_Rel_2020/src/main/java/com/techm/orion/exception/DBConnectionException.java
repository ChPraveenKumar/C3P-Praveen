package com.techm.orion.exception;


public class DBConnectionException extends Exception{

    public DBConnectionException(Throwable cause) {
        super(cause);
    }

    public DBConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBConnectionException(String message) {
        super(message);
    }
}
