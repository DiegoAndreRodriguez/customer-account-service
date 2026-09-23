package com.diego.customeraccount.account.domain.model;

public enum Currency {
    PEN(0),
    USD(1);

    private final int offset;

    Currency(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }
}