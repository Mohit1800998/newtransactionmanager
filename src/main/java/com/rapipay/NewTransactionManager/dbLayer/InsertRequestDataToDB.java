package com.rapipay.NewTransactionManager.dbLayer;

import com.rapipay.NewTransactionManager.entities.CardDetails;
import com.rapipay.NewTransactionManager.entities.TransactionRequest;
import com.rapipay.NewTransactionManager.entities.TransactionResponse;
import com.rapipay.NewTransactionManager.repository.CardDetailsRepository;
import com.rapipay.NewTransactionManager.repository.PayloadDataRepository;
import com.rapipay.NewTransactionManager.repository.TransactionResponseRepository;
import com.rapipay.NewTransactionManager.responseModel.ResponseDto;
import com.rapipay.NewTransactionManager.utils.ErrorCodes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InsertRequestDataToDB {

    Logger log = LogManager.getLogger(InsertRequestDataToDB.class);

    @Autowired
    PayloadDataRepository saleRepository;

    @Autowired
    TransactionResponseRepository transactionResponseRepository;

    @Autowired
    CardDetailsRepository cardDetailsRepository;

    public TransactionRequest insertPayloadData(String reqFor, CardDetails cardDetails, TransactionRequest payloadData, String urn, ResponseDto responseDto) {

        try {

            if(reqFor.equals("HARDWARE_FAILURE") || reqFor.equals("TIMEOUT") || reqFor.equals("ARPC_FAILURE") || reqFor.equals("NO_TC_FAILURE") ||  reqFor.equals("VOID")) {
                log.info("[URN_{}] Inserting Data in the hashed_card_details table {}", urn, cardDetails);
                cardDetailsRepository.save(cardDetails);
            }

            log.info("[URN_{}] Inserting Data in the payload table {}", urn, payloadData);

            payloadData = saleRepository.save(payloadData);


            log.info("[URN_{}] Data Inserted in Database Successfully {}", urn, payloadData);

            responseDto.getApiResponseData().setResponseCode("");
            responseDto.getApiResponseData().setResponseMessage("");

        } catch (NullPointerException e) {
            responseDto.getApiResponseData().setResponseCode(ErrorCodes.Failure_Code.errorCodes);
            responseDto.getApiResponseData().setResponseMessage(ErrorCodes.DB_Exception.errorCodes);
            responseDto.getApiResponseData().setResponseData("{}");

        } catch (Exception e) {
            responseDto.getApiResponseData().setResponseCode(ErrorCodes.Failure_Code.errorCodes);
            responseDto.getApiResponseData().setResponseMessage(ErrorCodes.DB_Exception.errorCodes);
            responseDto.getApiResponseData().setResponseData("{}");
        }

        return payloadData;
    }

    public void insertPaynextResponseData(TransactionResponse transactionResponse) {
        try {



            log.info("Inserting Data in the payload table {}", transactionResponse);
            TransactionResponse s = transactionResponseRepository.save(transactionResponse);


            log.info("Data Inserted in Database Successfully {}", s);


        } catch (NullPointerException e) {
            log.error("Null Pointer Exception {}  ----> {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Exception {}  ----> {}", e.getMessage(), e);
        }

    }

}
