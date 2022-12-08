package com.rapipay.NewTransactionManager.utils;

public enum CollectionName {
    TRANSACTIONREQUESTRESPONSE("transaction_request_response"),
    VASDETAILS("vas_detail");

    public final String collectionName;

    private CollectionName(String collectionName) {
        this.collectionName = collectionName;
    }
}


