package com.stocksense.inventoryservice.exception;

public class DuplicateTransactionException
        extends RuntimeException {

    public DuplicateTransactionException(String message) {
        super(message);
    }
}