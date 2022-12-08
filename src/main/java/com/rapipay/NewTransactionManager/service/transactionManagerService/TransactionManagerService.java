package com.rapipay.NewTransactionManager.service.transactionManagerService;

import com.rapipay.NewTransactionManager.entities.TransactionRequest;
import com.rapipay.NewTransactionManager.responseModel.ResponseDto;

/**
 * - This service will call paynext api for transaction and save the request and response to the database and also set the merchant limit and card details.
 * @author Mohit Yadav
 * @created on 10/11/22
 **/
public interface TransactionManagerService {

    /**
     *
     * @param urn - Unique Reference Number that will be generated for each request
     * @param reqFor - This defines the request for sale, cashatpos, void and reversal.
     * @param aesKey - Aes key which will be used the decrypt the encrypted request body.
     * @param encryptedData - Encrypted transaction data.
     * @param responseDto - Response Dto that will send back the response whether success or failure
     */
    void main(String urn,String reqFor,String aesKey, String encryptedData, ResponseDto responseDto);

    /**
     *
     * @param urn - Unique Reference Number that will be generated for each request
     * @param aesKey - Aes key which will be used the decrypt the encrypted request body.
     * @param encryptedData - Encrypted transaction data.
     * @param responseDto - Response Dto that will send back the response whether success or failure
     * @return
     */
    String decryptPayload(String urn,String aesKey, String encryptedData, ResponseDto responseDto);

    /**
     *
     * @param reqFor - This defines the request for sale, cashatpos, void and reversal.
     * @param urn - Unique Reference Number that will be generated for each request
     * @param responseDto - Response Dto that will send back the response whether success or failure
     * @return
     */
    TransactionRequest insertPayloadRequestToDb(String reqFor, String urn, ResponseDto responseDto);

    /**
     *
     * @param urn - Unique Reference Number that will be generated for each request
     * @param transactionRequest - Transaction Request for paynext api calling and Database Insertion.
     * @param responseDto - Response Dto that will send back the response whether success or failure
     */
    void hsmManagerAPICall(String urn,TransactionRequest transactionRequest, ResponseDto responseDto);

    /**
     *
     * @param reqFor - This defines the request for sale, cashatpos, void and reversal.
     * @param transactionRequest - Transaction Request for paynext api calling and Database Insertion.
     * @param urn - Unique Reference Number that will be generated for each request
     * @param responseDto - Response Dto that will send back the response whether success or failure
     * @return
     */
    String callPaynextApi(String reqFor,TransactionRequest transactionRequest, String urn, ResponseDto responseDto);

    /**
     *
     * @param paynextResponse - Response that is received from the paynext api.
     * @param urn - Unique Reference Number that will be generated for each request
     * @param responseDto - Response Dto that will send back the response whether success or failure
     * @param transactionRequest - Transaction Request for paynext api calling and Database Insertion.
     */
    void insertPaynextResponseToDb(String paynextResponse, String urn, ResponseDto responseDto, TransactionRequest transactionRequest); //return type should be void

    /**
     *
     * @param urn - Unique Reference Number that will be generated for each request
     * @param receiptData - Received data that is fetched from the paynext response.
     * @param paynextResponse - Response that is received from the paynext api.
     * @param responseDto - Response Dto that will send back the response whether success or failure
     * @return
     */
    ResponseDto setPaynextResponse(String urn, String receiptData, String paynextResponse, ResponseDto responseDto) ;

    }
