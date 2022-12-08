package com.rapipay.NewTransactionManager.utils;

public enum ErrorCodes {
    Success("Success"),
    Success_Code("200"),
    Failure("Failure"),
    Failure_Code("401"),
    DB_Exception("Exception in Inserting Data to Db"),
    HSM_PIN_Excpetion("Failure in HSM Manager for Pin Block"),
    HSM_TRACK_Excpetion("Failure in HSM Manager for Track Block"),
    PAYNEXT_EXCEPTION("Failure in Paynext Api"),
    Something_Went_Wrong("Something went wrong"),

    Merchant_Limit_Exceeded("Merchant Limit Exceeded"),
    Session_Key_Null("Header is empty or null"),
    Request_Body_Failure("Something went wrong"),
    Request_Header_Failure("Request Header Failure"),
    Session_Key_Failure("Session Key Failure"),
    Receipt_Data_Does_Not_Exist("Receipt data does not exist");

    public final String errorCodes;

    private ErrorCodes(String errorCodes) {
        this.errorCodes = errorCodes;
    }
}
