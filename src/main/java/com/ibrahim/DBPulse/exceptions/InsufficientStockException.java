package com.ibrahim.DBPulse.exceptions;

/**
 * Exception thrown when there is insufficient stock to fulfill an order.
 * Results in HTTP 400 Bad Request response.
 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
