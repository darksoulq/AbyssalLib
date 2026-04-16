package com.github.darksoulq.abyssallib.server.economy;

public enum TransactionStatus {
    SUCCESS,
    INSUFFICIENT_FUNDS,
    CAPACITY_EXCEEDED,
    ACCOUNT_NOT_FOUND,
    ACCOUNT_LOCKED,
    ACCOUNT_FROZEN,
    UNSUPPORTED_CURRENCY,
    UNSUPPORTED_CONTEXT,
    INVALID_AMOUNT,
    BATCH_FAILED,
    CANCELLED_BY_EVENT,
    ERROR
}