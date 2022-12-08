package com.rapipay.NewTransactionManager.utils;

public enum ProcedureName {
    PAYNEXT_INSERT_DETAILS("[POS].[INSERT_PAYNEXT_RESPONSE_DATA]"),
    VERIFY_LDAP_AUTH_DATA("[SEC].[VEREFY_LDAP_AUTHDATA]");

    public final String procedureName;

    private ProcedureName(String procedureName){
        this.procedureName = procedureName;
    }
}
