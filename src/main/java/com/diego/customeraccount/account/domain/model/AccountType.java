package com.diego.customeraccount.account.domain.model;

public enum AccountType {
    SAVINGS("10"),
    CHECKING("20");

    private final String code;

    AccountType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
