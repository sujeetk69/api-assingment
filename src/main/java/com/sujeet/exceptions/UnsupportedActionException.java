package com.sujeet.exceptions;

/**
 * A customer exception
 */
public class UnsupportedActionException extends Exception {
    public UnsupportedActionException() {
    }

    public UnsupportedActionException(String message) {
        super(message);
    }
}
