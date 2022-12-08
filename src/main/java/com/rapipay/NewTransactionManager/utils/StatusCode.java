package com.rapipay.NewTransactionManager.utils;

public enum StatusCode {
    SOMETHING_WENT_WRONG("401","something went wrong"),
    SUCCESS("200","Sucesss"),
    deviceSerialNumberError("60001","Device serial number is blank or invalid."),
    msgTypeError("60002","msg type is blank or invalid. "),
    primaryAccountNumberError("60003","primary account number is blank or invalid."),
    processingCodeError("60004","processing code is blank or invalid."),
    transactionAmountError("60005","transaction amount is blank or invalid."),
    transactionTimeError("60006","transaction time is blank or invalid."),
    transactionDateError("60007","transaction date is blank or invalid."),
    expirationDateError("60008","expiration date is blank or invalid."),
    acquiringInstitutionError("60009","acquiring institution is blank or invalid."),
    posEntryModeError("60010","pos entry mode is blank or invalid."),
    posConditionCodeError("60011","pos condition code is blank or invalid."),
    track2Error("60012","track2 is blank or invalid."),
    terminalIDError("60013","terminal ID is blank or invalid."),
    cardAcceptorIDCodeError("60014","card Acceptor ID Code is blank or invalid."),
    encryptionCodeError("60015","encryption code is blank or invalid."),
    currencyCodeError("60016","currency code is blank or invalid."),
    pinBlockError("60017","pin block is blank or invalid."),
    pinKsnError("60018","pin ksn is blank or invalid."),
    additionalAmountsError("60019","additional amounts is blank or invalid."),
    emvDataError("60020","emv data is blank or invalid."),
    batchNumberError("60021","batch number is blank or invalid."),
    printDataError("60022","print data is blank or invalid."),
    reservedError("60023","reserved is blank or invalid."),
    isPdfReceiptError("60024","isPdfReceipt is blank or invalid."),
    cardReadTypeError("60025","card read type is blank or invalid."),
    cardBrandError("60026","card band is blank or invalid."),
    userIdError("60027","user ID is blank or invalid."),
    serviceTypeError("60028","service type is blank or invalid."),
    deviceTypeError("60029","device type is blank or invalid."),
    requestLatitudeError("60030","request latitude is blank or invalid."),
    requestLongitudeError("60031","request longitude is blank or invalid."),
    voucherNumberError("60032","voucher number is blank or invalid.");

    private final String errorCode;
    private final String errorMessage;

    StatusCode(String errorCode, String errorMessage) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public String getErrorCode() {
        return this.errorCode;
    }
}
