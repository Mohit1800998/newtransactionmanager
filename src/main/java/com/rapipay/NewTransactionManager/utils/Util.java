package com.rapipay.NewTransactionManager.utils;

import com.rapipay.NewTransactionManager.entities.TransactionRequest;
import com.rapipay.NewTransactionManager.entities.TransactionResponse;
import com.rapipay.NewTransactionManager.responseModel.ApiResponseData;
import com.rapipay.NewTransactionManager.service.reversalService.impl.PaynextHostImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Objects;

@Component
public class Util {

    private static final Logger log = LogManager.getLogger(Util.class);


    public ApiResponseData setResponse(String code, String message, Object data) {
        ApiResponseData apiResponseData=new ApiResponseData();
        apiResponseData.setResponseCode(code);
        apiResponseData.setResponseMessage(message);
        apiResponseData.setResponseData(data);
        return apiResponseData;
    }

    public static byte[] getSHA(String input) throws NoSuchAlgorithmException
    {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 64)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    public TransactionResponse setPaynextResponseForDatabase(String urn, TransactionRequest transactionRequest, JSONObject paynextResponse){
        TransactionResponse transactionResponse=new TransactionResponse();
        try {

            String[] parts = paynextResponse.optString("F058").split("\n");
            log.info("[URN_{}] Setting up Transaction response for Database : {}", urn, transactionRequest);

            transactionResponse.setTreq_id(transactionRequest.getTreqId());
            transactionResponse.setSwitch_id(BigInteger.ONE);
            transactionResponse.setInvoice_number(String.valueOf(transactionRequest.getInvoiceNumber()));
            transactionResponse.setMessage_type(paynextResponse.optString("MsgType"));
            transactionResponse.setProcessing_code(paynextResponse.optString("F003"));
            transactionResponse.setReterival_refno(paynextResponse.optString("F037"));
            transactionResponse.setService_type(transactionRequest.getServiceType());
            transactionResponse.setVoucher_number(transactionRequest.getVoucherNumber());
            transactionResponse.setCreated_on(new Timestamp(System.currentTimeMillis()));
            if(!paynextResponse.equals("") && paynextResponse.optString("F039").equals("00")) {
                transactionResponse.setRr_no(parts[12].split(":")[1].split(" ")[0]);
                transactionResponse.setResponse_batchno(parts[6].split(":")[1].split(" ")[0]);
                transactionResponse.setAid(parts[10].split(":")[1].trim());
                transactionResponse.setCard_type(parts[9].split(":")[1].trim());
                transactionResponse.setRes_txn_amount(Double.valueOf(parts[13].split(":")[1].replace("Rs.", "").trim()));
                transactionResponse.setAuth_number(parts[12].split(":")[2]);
            }

        } catch (NullPointerException e) {
            log.error("[URN_{}] error occurred in service layer : {} {}", urn, e.getMessage(), e);


        } catch (Exception e) {

            log.error("[URN_{}] error occurred in service layer : {}", urn, e.getMessage());


        }

        return transactionResponse;
    }

    public TransactionResponse setPaynextReversalResponseForDatabase(String urn, String treqId, JSONObject requestData, JSONObject paynextResponse){
        TransactionResponse transactionResponse=new TransactionResponse();
        try {

            String[] parts = paynextResponse.optString("F058").split("\n");
            log.info("[URN_{}] Setting up Transaction response for Database.", urn);

            transactionResponse.setTreq_id(BigInteger.valueOf(Long.parseLong(treqId)));
            transactionResponse.setSwitch_id(BigInteger.ONE);
            transactionResponse.setInvoice_number(String.valueOf(paynextResponse.optString("F062")));
            transactionResponse.setMessage_type(paynextResponse.optString("MsgType"));
            transactionResponse.setProcessing_code(paynextResponse.optString("F003"));
            transactionResponse.setReterival_refno(paynextResponse.optString("F037"));
            transactionResponse.setService_type(requestData.optString("serviceType"));
            transactionResponse.setVoucher_number(requestData.optString("voucherNumber"));
            transactionResponse.setCreated_on(new Timestamp(System.currentTimeMillis()));
            if(!paynextResponse.equals("") && paynextResponse.optString("F039").equals("00")) {
                transactionResponse.setRr_no(parts[12].split(":")[1].split(" ")[0]);
                transactionResponse.setResponse_batchno(parts[6].split(":")[1].split(" ")[0]);
                transactionResponse.setAid(parts[10].split(":")[1].trim());
                transactionResponse.setCard_type(parts[9].split(":")[1].trim());
                transactionResponse.setRes_txn_amount(Double.valueOf(parts[13].split(":")[1].replace("Rs.", "").trim()));
                transactionResponse.setAuth_number(parts[12].split(":")[2]);
            }

        } catch (NullPointerException e) {
            log.error("[URN_{}] error occurred in service layer : {} {}", urn, e.getMessage(), e);


        } catch (Exception e) {

            log.error("[URN_{}] error occurred in service layer : {}", urn, e.getMessage());


        }

        return transactionResponse;
    }

}
