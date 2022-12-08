package com.rapipay.NewTransactionManager.utils;

public class Constants {

    public static final String MERCHANTLIMITDAILY="MERCHANT_LIMIT_DAILY_";
    public static final String MERCHANTLIMITWEEKLY="MERCHANT_LIMIT_WEEKLY_";
    public static final String REVERSALPOSTRANACTIONMANAGER = "REVERSALPOSTRANSACTIONMANAGER";
    public static final String MISCREDENTIALS="credential_details";
    public static final String HARDWARE_FAILURE = "22";
    public static final String ISREVERSEDTRUE="True";
    public static final String ISREVERSEDFALSE="False";
    public static final String ARPC_FAILURE = "E1";
    public static final String NO_TC_FAILURE = "E2";
    public static final String TIMEOUT = "91";
    public static final String DAILYKEY="{\"remainingLimit\":\"25000\",\"usedLimit\":\"0\",\"updatedOn\":\"\"}";
    public static final String WEEKLYKEY="{\"remainingLimit\":\"75000\",\"usedLimit\":\"0\",\"updatedOn\":\"\"}";
    public static final String MONTHLYKEY="{\"remainingLimit\":\"75000\",\"usedLimit\":\"0\",\"updatedOn\":\"\"}";
    public static final String YEARLYKEY="{\"remainingLimit\":\"200000\",\"usedLimit\":\"0\",\"updatedOn\":\"\"}";
    public static final String RESPONSEDATEFORMAT="yyyy-MM-dd hh:mm:ss";
    public static final String MERCHANTLIMITMONTHLY="MERCHANT_LIMIT_MONTHLY_";
    public static final String MERCHANTLIMITDAILYYEARLY="MERCHANT_LIMIT_YEARLY_";
    public static final String RAPIPOSSALEDETAILS="RAPIPOS_SALE_DETAILS";
    public static final String MERCHANTSALEDETAILS="MERCHANT_SALE_DETAILS_";
    public static final String RAPIPOSDETAILS="{\"upiSale\":{\"auBankTransaction\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"},\"ICICITransaction\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"},\"yesBankTransaction\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"}},\"cardDetails\":{\"debitCard\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"},\"creditCard\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"},\"otherCard\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"}},\"typeOfSaleDetails\":{\"rupayCashatPos\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"},\"visaCashatPos\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"},\"masterCashatPos\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"},\"visaSale\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"},\"masterSale\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"},\"rupaySale\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"},\"visaVoid\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"},\"masterVoid\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"},\"rupayVoid\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"},\"visaReversal\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"},\"masterReversal\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"},\"rupayReversal\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"}},\"brandCardDetails\":{\"debitRupayCard\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"},\"creditMasterCard\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"},\"debitMasterCard\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"},\"debitVisaCard\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"},\"creditVisaCard\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"},\"otherBrandCards\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"},\"creditRupayCard\":{\"txnCount\":\"0\",\"txnAmount\":\"0\"}}}";
    public static final String VOIDMSGTYPE = "0420";
    public static final String SALEPROCESSINGCODE = "000000";
    public static final String CASHATPOSPROCESSINGCODE = "090000";
    public static final String POSCONDITIONCODE = "00";

    public static final String chargeSlip="{\n" +
            "    \"line1\": {\n" +
            "        \"Display\": \"C\",\n" +
            "        \"Font\": \"L\",\n" +
            "        \"Data\": \"{merchantoutletname}\"\n" +
            "    },\n" +
            "    \"line2\": {\n" +
            "        \"Display\": \"C\",\n" +
            "        \"Font\": \"L\",\n" +
            "        \"Data\": \"{city}\"\n" +
            "    },\n" +
            "    \"line3\": {\n" +
            "        \"Display\": \"C\",\n" +
            "        \"Font\": \"L\",\n" +
            "        \"Data\": \"--------------------\"\n" +
            "    },\n" +
            "    \"line4\": {\n" +
            "        \"Display\": \"L\",\n" +
            "        \"Font\": \"M\",\n" +
            "        \"Data\": \"DATE : {date}\"\n" +
            "    },\n" +
            "    \"line4\": {\n" +
            "        \"Display\": \"R\",\n" +
            "        \"Font\": \"M\",\n" +
            "        \"Data\": \"Time : {time}\"\n" +
            "    },\n" +
            "    \"line5\": {\n" +
            "        \"Display\": \"L\",\n" +
            "        \"Font\": \"M\",\n" +
            "        \"Data\": \"mid : {mid}\"\n" +
            "    },\n" +
            "    \"line5\": {\n" +
            "        \"Display\": \"R\",\n" +
            "        \"Font\": \"L\",\n" +
            "        \"Data\": \"TID : {tid}\"\n" +
            "    },\n" +
            "    \"line6\": {\n" +
            "        \"Display\": \"L\",\n" +
            "        \"Font\": \"M\",\n" +
            "        \"Data\": \"Batch No : {batchno}\"\n" +
            "    },\n" +
            "    \"line6\": {\n" +
            "        \"Display\": \"R\",\n" +
            "        \"Font\": \"M\",\n" +
            "        \"Data\": \"Invoice No : {invoiceno}\"\n" +
            "    },\n" +
            "    \"line7\": {\n" +
            "        \"Display\": \"C\",\n" +
            "        \"Font\": \"L\",\n" +
            "        \"Data\": \"--------------------\"\n" +
            "    },\n" +
            "    \"line7\": {\n" +
            "        \"Display\": \"C\",\n" +
            "        \"Font\": \"L\",\n" +
            "        \"Data\": \"Transaction Type : {transactionType}\"\n" +
            "    },\n" +
            "    \"line8\": {\n" +
            "        \"Display\": \"L\",\n" +
            "        \"Font\": \"M\",\n" +
            "        \"Data\": \"Card No : {cardno}\"\n" +
            "    },\n" +
            "    \"line9\": {\n" +
            "        \"Display\": \"L\",\n" +
            "        \"Font\": \"M\",\n" +
            "        \"Data\": \"Exp. Date : {expiredate}\"\n" +
            "    },\n" +
            "     \"line9\": {\n" +
            "        \"Display\": \"R\",\n" +
            "        \"Font\": \"M\",\n" +
            "        \"Data\": \"Card Type: {cardtype}\"\n" +
            "    },\n" +
            "     \"line10\": {\n" +
            "        \"Display\": \"L\",\n" +
            "        \"Font\": \"M\",\n" +
            "        \"Data\": \"TXN ID : {txnid}\"\n" +
            "    },\n" +
            "     \"line10\": {\n" +
            "        \"Display\": \"R\",\n" +
            "        \"Font\": \"M\",\n" +
            "        \"Data\": \"RRN : {rrn}\"\n" +
            "    },\n" +
            "    \"line11\": {\n" +
            "        \"Display\": \"L\",\n" +
            "        \"Font\": \"M\",\n" +
            "        \"Data\": \"AID : {aid}\"\n" +
            "    },\n" +
            "    \"line12\": {\n" +
            "        \"Display\": \"C\",\n" +
            "        \"Font\": \"L\",\n" +
            "        \"Data\": \"--------------------\"\n" +
            "    },\n" +
            "    \"line13\": {\n" +
            "        \"Display\": \"L\",\n" +
            "        \"Font\": \"L\",\n" +
            "        \"Data\": \"TOTAL AMOUNT : {totalamount}\"\n" +
            "    },\n" +
            "    \"image1\": {\n" +
            "        \"Display\": \"L\",\n" +
            "        \"Data\": \"https://clf.rapipayuat.com/resources/img/rbl.png\"\n" +
            "    },\n" +
            "    \"imageCount\": \"1\",\n" +
            "    \"lineCount\": \"13\"\n" +
            "}";
    public static final CharSequence RESPONSECODE = "17";
    public static final String FAILURE = "Failure";
    public static final String TRANSACTIONMANAGER = "Transaction Manager";

    private Constants() {

    }
}
