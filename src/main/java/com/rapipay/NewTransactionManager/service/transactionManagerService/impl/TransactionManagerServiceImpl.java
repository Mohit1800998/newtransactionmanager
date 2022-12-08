package com.rapipay.NewTransactionManager.service.transactionManagerService.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapipay.NewTransactionManager.dbLayer.InsertRequestDataToDB;
import com.rapipay.NewTransactionManager.entities.CardDetails;
import com.rapipay.NewTransactionManager.entities.TransactionRequest;
import com.rapipay.NewTransactionManager.entities.TransactionRequestResponse;
import com.rapipay.NewTransactionManager.entities.TransactionResponse;
import com.rapipay.NewTransactionManager.repository.VasDetailsRepository;
import com.rapipay.NewTransactionManager.service.reversalService.PaynextHost;
import com.rapipay.NewTransactionManager.service.transactionManagerService.TransactionManagerService;
import com.rapipay.NewTransactionManager.utils.CollectionName;
import com.rapipay.NewTransactionManager.utils.Constants;
import com.rapipay.NewTransactionManager.utils.ErrorCodes;
import com.rapipay.NewTransactionManager.responseModel.ResponseDto;
import com.rapipay.NewTransactionManager.utils.InitInitializerUtil;
import com.rapipay.NewTransactionManager.utils.PaynextMethodUtil;
import com.rapipay.NewTransactionManager.utils.ReadApplicationProperties;
import com.rapipay.NewTransactionManager.utils.RedisUtil;
import com.rapipay.NewTransactionManager.utils.Util;
import com.rapipay.security.asym.RSA;
import com.rapipay.security.sym.AES256;
import com.rapipay.webcaller.AccessRestApi;
import com.rapipay.webcaller.AccessRestEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class TransactionManagerServiceImpl implements TransactionManagerService {

    Logger log = LogManager.getLogger(TransactionManagerServiceImpl.class);

    @Autowired
    InsertRequestDataToDB insertRequestDataToDB;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    VasDetailsRepository vasDetailsRepository;

    @Autowired
    ReadApplicationProperties rpt;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    PaynextHost paynextHost;

    @Autowired
    Util util;

    @Override
    public void main(String urn, String reqFor, String aesKey, String encryptedData, ResponseDto responseDto) {

        try {
            checkBody(aesKey, encryptedData, responseDto);

            if (responseDto.getApiResponseData().getResponseCode().equals("401")) {
                log.error("[URN_{}] Error in Request Body or Request Header ", urn);
                return;
            }

            log.info("[URN_{}] Calling Decryption Method to decrypt the Payload {}", urn, encryptedData);

            decryptPayload(urn, aesKey, encryptedData, responseDto);

            if (responseDto.getApiResponseData().getResponseCode().equals("401")) {
                log.error("[URN_{}] Error in Decryption method to decrypt the Payload {}", urn, encryptedData);
                return;
            }

            if (reqFor.equals("SALE") || reqFor.equals("CASHATPOS") || reqFor.equals("VOID")) {

                TransactionRequest transactionRequest = insertPayloadRequestToDb(reqFor, urn, responseDto);

                if (responseDto.getApiResponseData().getResponseCode().equals("401")) {
                    return;
                }

                if(!reqFor.equals("VOID")) {
                    hsmManagerAPICall(urn,transactionRequest, responseDto);
                }

                if (responseDto.getApiResponseData().getResponseCode().equals("401")) {
                    log.error("[URN_{}] Error in HSM Manager API Calling", urn);
                    return;
                }

                log.info("[URN_{}] Calling the Paynext API ", urn);

                String paynextResponse = callPaynextApi(reqFor, transactionRequest, urn, responseDto);

                if (responseDto.getApiResponseData().getResponseCode().equals("401")) {
                    log.error("[URN_{}] Error in Paynext API Calling", urn);
                }

                String receiptData = setReceiptData(paynextResponse, urn, responseDto);

                if (responseDto.getApiResponseData().getResponseCode().equals("401")) {
                    log.error("[URN_{}] Error in setting the receipt data {} : ", urn, paynextResponse);
                }

                log.info("[URN_{}] Calling database to insert request and response {} : ", urn, paynextResponse);

                insertPaynextResponseAndRequest(urn,reqFor,transactionRequest, paynextResponse, receiptData, responseDto);

                if(new JSONObject(paynextResponse).optString("F039").equals("00")) {

                    setMerchantLimit(urn,reqFor, String.valueOf(transactionRequest.getRequestAmount()), String.valueOf(transactionRequest.getTid()), String.valueOf(transactionRequest.getMid()), responseDto);

                    if(responseDto.getApiResponseData().getResponseCode().equals(ErrorCodes.Failure_Code.errorCodes)){
                        log.error("[URN_{}] Error in setting up merchant limit ", urn);
                        return ;
                    }

                    setCardDetails(paynextResponse, responseDto);

                    if(responseDto.getApiResponseData().getResponseCode().equals(ErrorCodes.Failure_Code.errorCodes)){
                        log.error("[URN_{}] Error in setting up card details ", urn);
                        return ;
                    }

                    setMerchantCardDetails(paynextResponse, responseDto);

                    if(responseDto.getApiResponseData().getResponseCode().equals(ErrorCodes.Failure_Code.errorCodes)){
                        log.error("[URN_{}] Error in setting up merchant card details ", urn);

                        return ;
                    }

                }

                log.info("[URN_{}] Calling Insert Paynext Response PaynextMethodUtil {} : ", urn, paynextResponse);

                insertPaynextResponseToDb(paynextResponse, urn, responseDto, transactionRequest);

                if (responseDto.getApiResponseData().getResponseCode().equals("401")) {
                    log.error("[URN_{}] Error in Insert Paynext Response PaynextMethodUtil {} : ", urn, paynextResponse);
                }

                if (setPaynextResponse(urn, receiptData, paynextResponse, responseDto).getApiResponseData().getResponseCode().equals("401")) {
                    log.error("[URN_{}] Error in setting up Paynext Response", urn);
                    return;
                }

            } else{

                TransactionRequest transactionRequest = insertPayloadRequestToDb(reqFor, urn, responseDto);

                if (responseDto.getApiResponseData().getResponseCode().equals("401")) {
                    responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Something_Went_Wrong.errorCodes,"{}"));
                    return;
                }else {

                    JSONObject dataForPaynext = new JSONObject(responseDto.getApiResponseData().getResponseData().toString());

                    responseDto.setApiResponseData(util.setResponse(ErrorCodes.Success_Code.errorCodes,ErrorCodes.Success.errorCodes,"{}"));

                    String treqId = String.valueOf(transactionRequest.getTreqId());

                    callPaynextApiForReversal(urn, treqId, reqFor, dataForPaynext);
                }
            }


        } catch (NullPointerException e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

    } catch (Exception e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        }
    }

    public void checkBody(String aesKey, String encBody, ResponseDto res) {
        try {
            if (aesKey.equals("")) {
                res.getApiResponseData().setResponseCode(ErrorCodes.Failure_Code.errorCodes);
                res.getApiResponseData().setResponseMessage(ErrorCodes.Request_Header_Failure.errorCodes);
            } else if (encBody == null) {
                res.getApiResponseData().setResponseCode(ErrorCodes.Failure_Code.errorCodes);
                res.getApiResponseData().setResponseMessage(ErrorCodes.Request_Body_Failure.errorCodes);
            } else {
                res.getApiResponseData().setResponseCode("");
                res.getApiResponseData().setResponseMessage("");
            }
        } catch (NullPointerException e) {
            res.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));


        } catch (Exception e) {
            res.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        }
    }

    @Override
    public String decryptPayload(String urn, String aesKey, String encryptedData, ResponseDto responseDto) {
        String decData = encryptedData;
        String sessionKey = "";


        try {
            String stringBefore = readFile(rpt.getPropertyData1("PRIVATE_KEY_PATH"), responseDto);
            log.info("[URN_{}] Aes key for decryption  {}", urn, aesKey);

            String reqBody1 = RSA.decrypt(aesKey, stringBefore);

            sessionKey = reqBody1.substring(reqBody1.length() - 32);


            log.info("[URN_{}] Session Key decrypted  {}", urn, sessionKey);

            log.info("[URN_{}] decrypting the encrypted data using session key ", urn);
            decData = AES256.decode(encryptedData, sessionKey);


            if (rpt.getPropertyData1("GET_REQUEST").equals("0")) {
                log.info("[URN_{}] Decrypted Request body using AES {}", urn, decData);
            }
            responseDto.setApiResponseData(util.setResponse("","",decData));

        } catch (NullPointerException e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        } catch (Exception e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        }

        return sessionKey;
    }

    public String readFile(String filePath, ResponseDto responseDto) {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader buffer = new BufferedReader(new FileReader(filePath))) {
            String str;
            while ((str = buffer.readLine()) != null) {
                builder.append(str);
            }
        } catch (IOException e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        } catch (NullPointerException e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));


        } catch (Exception e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        }
        return builder.toString();
    }

    public TransactionRequest setDecryptedData(String reqFor, String urn, ResponseDto responseDto) {

        log.info("[URN_{}] Inside the Set Decrypted Data method ", urn);
        TransactionRequest transactionRequest=null;
        try {

            JSONObject data = new JSONObject(responseDto.getApiResponseData().getResponseData().toString());

            String stan="";
            JSONObject dbData = new JSONObject();

            if (reqFor.equals("HARDWARE_FAILURE") || reqFor.equals("TIMEOUT") || reqFor.equals("ARPC_FAILURE") || reqFor.equals("NO_TC_FAILURE") || reqFor.equals("VOID")) {
                Query query = new Query();
                query.addCriteria(Criteria.where("mid").is(data.optString("f042")));
                query.addCriteria(Criteria.where("tid").is(data.optString("f041")));
                query.addCriteria(Criteria.where("amount").is(data.optString("f004")));

                query.addCriteria(Criteria.where("status").is(ErrorCodes.Success.errorCodes));
                query.addCriteria(Criteria.where("txnId").is(data.optString("txnId")));
                query.addCriteria(Criteria.where("isReversed").is(Constants.ISREVERSEDFALSE));

                JSONObject transactionRequestResponses = new JSONObject(mongoTemplate.find(query, TransactionRequestResponse.class, CollectionName.TRANSACTIONREQUESTRESPONSE.collectionName).get(0));
                String txnId = data.optString("txnId");
                stan = data.optString("f011");

                data = new JSONObject(transactionRequestResponses.optString("request"));
                data.put("txnId",txnId);
                data.put("f011", stan);
                JSONObject responseData = new JSONObject(transactionRequestResponses.optString("response"));
                data.put("f062", responseData.optString("F062"));
                dbData.put("invoiceNumber", responseData.optString("F062"));
                dbData.put("rrNo", PaynextMethodUtil.generateRrn());

                String cardNumber = data.optString("f002");
                int masked = cardNumber.substring(0,cardNumber.length() - 4).length() - 6;
                String mask = new String(new char[masked]).replace("\0", "*");
                String maskedCardNumber=cardNumber.substring(0, 6) + mask + cardNumber.substring(cardNumber.length() - 4, cardNumber.length());


                if(data.optString("serviceType").equals("CASHATPOS")) {
                    dbData.put("otherAmount", Double.valueOf(data.optString("f054").substring(8)) / 100);
                }
                dbData.put("mskcardnumber", maskedCardNumber);
                dbData.put("otherAmount", "");
                dbData.put("requestAmount", String.valueOf(Double.valueOf(data.optString("f004")) / 100));
                dbData.put("requestcode",data.optString("f003"));
                dbData.put("requestdatetime", data.optString("f012"));
                dbData.put("requestDate", data.optString("f013"));
                dbData.put("emvCardExpdt", data.optString("f014"));
                dbData.put("pointOfServiceEntryMode", data.optString("f022"));
                dbData.put("pointOfServiceConditionCode", data.optString("f025"));
                dbData.put("trackksn", data.optString("f035")); // Will be done using hsm manager for track2 data
                dbData.put("tid", data.optString("f041"));
                dbData.put("mid", data.optString("f042"));
                dbData.put("pinBlockKsn", data.optString("f052"));
                dbData.put("pinBlockKsn", data.optString("f053"));

                dbData.put("tlvData", data.optString("f053"));
                dbData.put("readcardType", data.optString("cardReadType"));
                dbData.put("cardBrand", data.optString("cardBrand"));

                dbData.put("requestType",reqFor);
                dbData.put("userId", data.optString("userId"));
                dbData.put("serviceType", data.optString("serviceType"));
                dbData.put("terminalSerialNo", data.optString("deviceSerialNumber"));
                dbData.put("reqLatitude", data.optString("requestLatitude"));
                dbData.put("reqLongitude", data.optString("requestLongitude"));
                dbData.put("voucherNumber", data.optString("voucherNumber"));
                dbData.put("urn",urn);


            } else {

                String cardNumber = data.optString("f002");
                int masked = cardNumber.substring(0,cardNumber.length() - 4).length() - 6;
                String mask = new String(new char[masked]).replace("\0", "*");
                String maskedCardNumber=cardNumber.substring(0, 6) + mask + cardNumber.substring(cardNumber.length() - 4, cardNumber.length());

                dbData.put("rrNo", PaynextMethodUtil.generateRrn()); // Will be done by java for rr_no
                dbData.put("invoiceNumber", PaynextMethodUtil.generateRrn().substring(0, 15)); // Will be set by java for invoice_number
                dbData.put("requestcode",data.optString("f003"));
                dbData.put("mskcardnumber", maskedCardNumber);
                dbData.put("requestAmount", String.valueOf(Double.valueOf(data.optString("f004")) / 100));
                dbData.put("requestdatetime", data.optString("f012"));
                dbData.put("requestDate", data.optString("f013"));
                dbData.put("emvCardExpdt", data.optString("f014"));
                dbData.put("pointOfServiceEntryMode", data.optString("f022"));
                dbData.put("pointOfServiceConditionCode", data.optString("f025"));
                dbData.put("trackksn", data.optString("f035")); // Will be done using hsm manager for track2 data
                dbData.put("tid", data.optString("f041"));
                dbData.put("mid", data.optString("f042"));
                dbData.put("pinBlockKsn", data.optString("f052"));
                dbData.put("pinBlockKsn", data.optString("f053"));
                if(reqFor.equals("CASHATPOS")) {
                    dbData.put("otherAmount", Double.valueOf(data.optString("f054").substring(8)) / 100);
                }
                dbData.put("otherAmount", "");
                dbData.put("tlvData", data.optString("f053"));
                dbData.put("readcardType", data.optString("cardReadType"));
                dbData.put("cardBrand", data.optString("cardBrand"));

                dbData.put("requestType",reqFor);
                dbData.put("userId", data.optString("userId"));
                dbData.put("serviceType", data.optString("serviceType"));
                dbData.put("terminalSerialNo", data.optString("deviceSerialNumber"));
                dbData.put("reqLatitude", data.optString("requestLatitude"));
                dbData.put("reqLongitude", data.optString("requestLongitude"));
                dbData.put("voucherNumber", data.optString("voucherNumber"));
                dbData.put("urn",urn);

            }
            stan = PaynextMethodUtil.stan();
            dbData.put("stan", stan);
            data.put("f011", stan);
            dbData.put("switchId" ,1001);

            dbData.put("requestBatchno", PaynextMethodUtil.stan());


            ObjectMapper objectMapper = new ObjectMapper();

            transactionRequest = objectMapper.readValue(dbData.toString(), TransactionRequest.class);
            transactionRequest.setBroadComSerialNo("");
            transactionRequest.setRunningSessionId("");


            responseDto.setApiResponseData(util.setResponse("","",data));

        } catch (NullPointerException e) {
            log.error("[URN_{}] Data is invalid {} : {} {} ",urn
                    , e.getMessage(), e);
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));


        } catch (Exception e) {
            log.error("[URN_{}] Data is invalid {} : {}",urn
                    , e.getMessage());
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        }
        return transactionRequest;
    }

    @Override
    public TransactionRequest insertPayloadRequestToDb(String reqFor, String urn, ResponseDto responseDto) {

        try {

            TransactionRequest transactionRequest = setDecryptedData(reqFor, urn, responseDto);

            if(responseDto.getApiResponseData().getResponseCode().equals(ErrorCodes.Failure_Code.errorCodes)){
                return null;
            }


            JSONObject data = (JSONObject) responseDto.getApiResponseData().getResponseData();

            if(!(reqFor.equals("HARDWARE_FAILURE") || reqFor.equals("TIMEOUT") || reqFor.equals("ARPC_FAILURE") || reqFor.equals("NO_TC_FAILURE") ||  reqFor.equals("VOID"))) {
                log.info("[URN_{} Checking merchant limit {} : ", urn, transactionRequest);

                checkMerchantLimit(urn, String.valueOf(transactionRequest.getRequestAmount()), String.valueOf(transactionRequest.getTid()), String.valueOf(transactionRequest.getMid()), responseDto);
            }
            if(responseDto.getApiResponseData().getResponseCode().equals(ErrorCodes.Failure_Code.errorCodes)){
                return null;
            }


            log.info("[URN_{}] Calling DB to insert the Data {}", urn, transactionRequest);

            String hashedCard = util.toHexString(Util.getSHA(data.optString("f002")));

            CardDetails cardDetails = setCardData(hashedCard, transactionRequest, responseDto);

            if(responseDto.getApiResponseData().getResponseCode().equals(ErrorCodes.Failure_Code.errorCodes)){
                return null;
            }

            return insertRequestDataToDB.insertPayloadData(reqFor, cardDetails, transactionRequest, urn, responseDto);

        } catch (NullPointerException e) {
            log.error("[URN_{}] Error in inserting data to database {} : {} {} ",urn
                    , e.getMessage(), e);
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));


        } catch (Exception e) {
            log.error("[URN_{}] Error in inserting data to database {} : {}",urn
                    , e.getMessage());
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        }
        return null;
    }

    public CardDetails setCardData(String hashedData, TransactionRequest payloadData, ResponseDto responseDto){
        CardDetails cardDetails=new CardDetails();
        try {

           cardDetails.setHashed_data(hashedData);
           cardDetails.setRrn(String.valueOf(payloadData.getRrNo()));
        } catch (NullPointerException e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));


        } catch (Exception e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        }

        return cardDetails;
    }

    @Override
    public void hsmManagerAPICall(String urn,TransactionRequest transactionRequest, ResponseDto responseDto) {
        try {
            JSONObject keyData = new JSONObject(responseDto.getApiResponseData().getResponseData().toString());

            log.info("[URN_{}] Calling the HSM Manager for Pinblock data if present {} ", urn, keyData.optString("pinBlock"));
            String pinData;
            if ((keyData.optString("f052").length() != 0)) {
                pinData = hsmPinBlockApiCalling(urn, keyData, responseDto);
                keyData.put("f052", pinData);
                transactionRequest.setPinBlockKsn(pinData);
                if (responseDto.getApiResponseData().getResponseCode().equals("401")) {
                    return;
                }
            }

            log.info("[URN_{}] Calling the HSM Manager for track2 data {} ", urn, keyData.optString("f035"));

            String trackData = HsmTrack2ApiCalling(urn, keyData, responseDto);

            if (responseDto.getApiResponseData().getResponseCode().equals("401")) {
                return;
            }
            keyData.put("f035", trackData);
            transactionRequest.setTrackksn(trackData);
            responseDto.getApiResponseData().setResponseData(keyData.toString());

        } catch (NullPointerException e) {
            log.error("[URN_{}] Error in HSM Manager api call {} : {} {} ",urn
                    , e.getMessage(), e);
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));


        } catch (Exception e) {
            log.error("[URN_{}] Error in HSM Manager api call {} : {}",urn
                    , e.getMessage());
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        }

        return;
    }


    public String hsmPinBlockApiCalling(String urn, JSONObject keyData, ResponseDto responseDto) {

        String pinData = "";
        try {

            InitInitializerUtil initInitializerUtil = new InitInitializerUtil();
            JSONObject jsonData = initInitializerUtil.getJsonData().getJSONArray("PIN").optJSONObject(0);
            String tpa = jsonData.optString("CALLING_CREDENTIAL");
            String url = new JSONObject(tpa).optString("callUrl");
            JSONObject tpaCredJson = new JSONObject();
            String reqDataForHSM = requiredHSMPinData(urn, keyData, responseDto);

            log.info("[URN_{}] Setting tpa Cred for Pin Block {} : ", urn, new JSONObject(reqDataForHSM));

            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_REQUESTBODY.apiCallEnum, new JSONObject(reqDataForHSM));
            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_REQUEST_TIMEOUT.apiCallEnum, "70");
            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_CALLURL.apiCallEnum, url);

            Map<String, Object> headerDatMap = new HashMap<>();

            log.info("[URN_{}] Setting header Data for Pin Block ", urn);
            headerDatMap.put(AccessRestEnum.RESTAPI_CONTENTTYPE.apiCallEnum, AccessRestEnum.RESTAPI_APPLICATIONJSON.apiCallEnum);

            log.info("[URN_{}] Calling HSM Pin Block API {} : ", urn, headerDatMap);
            String responseFromHSM = AccessRestApi.apiCallPostPlainMethod("URN", urn, tpaCredJson, headerDatMap, false);

            log.info("[URN_{}] Response from HSM -> {}", urn, responseFromHSM);

            JSONObject responseFromHSMjson = new JSONObject(responseFromHSM);

            if (responseFromHSMjson.optJSONObject("apiResponseData").optString("ResponseCode").equals("200")) {
                String res = responseFromHSMjson.optJSONObject("apiResponseData").optString("ResponseData");
                JSONObject responseJson = new JSONObject(res);

                pinData = responseJson.optString("AL");
            } else {

                log.info("[URN_{}] Error response received from HSM", urn);

                responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.HSM_PIN_Excpetion.errorCodes,"{}"));

                return "";
            }
        } catch (NullPointerException e) {
            log.error("[URN_{}] Error in HSM Pin block api calling {} : {} {} ",urn
                    , e.getMessage(), e);
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));


        } catch (Exception e) {
            log.error("[URN_{}] Error in HSM Pin block api calling {} : {}",urn
                    , e.getMessage());
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        }
        return pinData;
    }


    public String HsmTrack2ApiCalling(String urn, JSONObject keyData, ResponseDto responseDto) {

        String hsmData = "";
        try {

            InitInitializerUtil initInitializerUtil = new InitInitializerUtil();
            JSONObject jsonData = initInitializerUtil.getJsonData().getJSONArray("TRACK").optJSONObject(0);
            String tpa = jsonData.optString("CALLING_CREDENTIAL");

            String url = new JSONObject(tpa).optString("callUrl");
            JSONObject tpaCredJson = new JSONObject();
            String reqDataForHSM = requiredHSMTrackData(urn, keyData, responseDto);

            log.info("[URN_{}] Setting tpa Cred for track2 {} : ", urn, new JSONObject(reqDataForHSM));

            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_REQUESTBODY.apiCallEnum, new JSONObject(reqDataForHSM));
            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_REQUEST_TIMEOUT.apiCallEnum, "70");
            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_CALLURL.apiCallEnum, url);

            Map<String, Object> headerDatMap = new HashMap<>();

            log.info("[URN_{}] Setting header Data for Pin Block ", urn);
            headerDatMap.put(AccessRestEnum.RESTAPI_CONTENTTYPE.apiCallEnum, AccessRestEnum.RESTAPI_APPLICATIONJSON.apiCallEnum);

            log.info("[URN_{}] Calling HSM track2 API {} : ", urn, headerDatMap);
            String responseFromHSM = AccessRestApi.apiCallPostPlainMethod("URN", urn, tpaCredJson, headerDatMap, true);

            log.info("[URN_{}] Response from HSM for track2 {} : ", urn, responseFromHSM);

            JSONObject responseFromHSMjson = new JSONObject(responseFromHSM);

            if (responseFromHSMjson.optJSONObject("apiResponseData").optString("ResponseCode").equals("200")) {
                String res = responseFromHSMjson.optJSONObject("apiResponseData").optString("ResponseData");
                JSONObject responseJson = new JSONObject(res);

                return responseJson.optString("AK");

            } else {

                log.info("[URN_{}] Error response received from HSM", urn);

                responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.HSM_TRACK_Excpetion.errorCodes,"{}"));


                return "";
            }

        } catch (NullPointerException e) {
            log.error("[URN_{}] Error in HSM Track 2 api calling {} : {} {} ",urn
                    , e.getMessage(), e);
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));


        } catch (Exception e) {
            log.error("[URN_{}] Error in HSM Track 2 api calling {} : {}",urn
                    , e.getMessage());
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        }

        return hsmData;
    }

    public String requiredHSMPinData(String urn, JSONObject HSMreq, ResponseDto responseDto) {

        String reqBodyData = "";

        try {
            InitInitializerUtil initInitializerUtil = new InitInitializerUtil();
            JSONObject jsonData = initInitializerUtil.getJsonData().getJSONArray("PIN").optJSONObject(0);
            String reqBody = jsonData.opt("BODY_DATA").toString();

            String tpa = jsonData.optString("CALLING_CREDENTIAL");

            reqBodyData = reqBody.replace("{ksn1}", HSMreq.optString("f053"));
            reqBodyData = reqBodyData.replace("{pinBlockData}", HSMreq.optString("f052"));
            reqBodyData = reqBodyData.replace("{track2data}", "");
            reqBodyData = reqBodyData.replace("{zpkLmk}", new JSONObject(tpa).optString("zpkLmk"));
            reqBodyData = reqBodyData.replace("{bdkLmk}", new JSONObject(tpa).optString("bdkLmk"));
            reqBodyData = reqBodyData.replace("{dekLmk}", "");
            reqBodyData = reqBodyData.replace("{accountId}", HSMreq.optString("f002").substring(HSMreq.optString("f002").length() - 13, HSMreq.optString("f002").length() - 1));
            reqBodyData = reqBodyData.replace("{command}", "G0");
            log.info(" [URN_{}] This is request Body data for HSM : {} ", urn, new JSONObject(reqBodyData));
        } catch (NullPointerException e) {
            log.error("[URN_{}] Error in Setting up HSM Pin data {} : {} {} ",urn
                    , e.getMessage(), e);
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));


        } catch (Exception e) {
            log.error("[URN_{}] Error in Setting up HSM Pin data {} : {}",urn
                    , e.getMessage());
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        }
        return reqBodyData;

    }

    public String requiredHSMTrackData(String urn, JSONObject HSMreq, ResponseDto responseDto) {

        String reqBodyData = "";

        try {
            InitInitializerUtil initInitializerUtil = new InitInitializerUtil();
            JSONObject jsonData = initInitializerUtil.getJsonData().getJSONArray("TRACK").optJSONObject(0);
            String reqBody = jsonData.opt("BODY_DATA").toString();

            String tpa = jsonData.optString("CALLING_CREDENTIAL");

            reqBodyData = reqBody.replace("{ksn1}", HSMreq.optString("f061"));
            reqBodyData = reqBodyData.replace("{pinBlockData}", "");
            reqBodyData = reqBodyData.replace("{track2data}", HSMreq.optString("f035"));
            reqBodyData = reqBodyData.replace("{zpkLmk}", new JSONObject(tpa).optString("dekLmk"));
            reqBodyData = reqBodyData.replace("{bdkLmk}", new JSONObject(tpa).optString("bdkLmk"));
            reqBodyData = reqBodyData.replace("{dekLmk}", new JSONObject(tpa).optString("dekLmk"));
            reqBodyData = reqBodyData.replace("{accountId}", "");
            reqBodyData = reqBodyData.replace("{command}", "G5");
            log.info(" [URN_{}] This is request Body data for HSM Track2 {} : ", urn, new JSONObject(reqBodyData));

        } catch (NullPointerException e) {
            log.error("[URN_{}] Error in Setting up HSM Track data {} : {} {} ",urn
                    , e.getMessage(), e);
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));


        } catch (Exception e) {
            log.error("[URN_{}] Error in Setting up HSM Track data {} : {}",urn
                    , e.getMessage());
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        }
        return reqBodyData;

    }

    @Override
    public String callPaynextApi(String reqFor,TransactionRequest transactionRequest, String urn, ResponseDto res) {
        String responseString = "";
        try {
            switch (reqFor) {
                case "SALE":
                    responseString = callPaynextApiForSale(urn,transactionRequest, res);
                    break;
                case "CASHATPOS":
                    responseString = callPaynextApiCashAtPOS(urn,transactionRequest, res);
                    break;
                case "VOID":
                    responseString = callPaynextApiForVoid(urn,transactionRequest, res);
                    break;

                default:

                    res.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.PAYNEXT_EXCEPTION.errorCodes,"{}"));

            }

        } catch (NullPointerException e) {
            log.error("[URN_{}] Error in Setting up HSM Track data {} : {} {} ",urn
                    , e.getMessage(), e);
            res.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.PAYNEXT_EXCEPTION.errorCodes,"{}"));


        } catch (Exception e) {
            log.error("[URN_{}] Error in Setting up HSM Track data {} : {}",urn
                    , e.getMessage());
            res.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.PAYNEXT_EXCEPTION.errorCodes,"{}"));

        }

        return responseString;
    }

    public String callPaynextApiForSale(String urn,TransactionRequest transactionRequest, ResponseDto responseDto) {
        String responseString = "";
        try {

            JSONObject reqBody = new JSONObject(responseDto.getApiResponseData().getResponseData().toString());

            InitInitializerUtil initInitializerUtil = new InitInitializerUtil();
            JSONObject jsonData = initInitializerUtil.getJsonData().getJSONArray("SALE").optJSONObject(0);
            String saleRequest = jsonData.opt("BODY_DATA").toString();

            saleRequest = saleRequest.replace("{msgType}", reqBody.optString("msgType"));
            saleRequest = saleRequest.replace("{processingCode}", Constants.SALEPROCESSINGCODE);
            saleRequest = saleRequest.replace("{transactionAmount}", reqBody.optString("f004"));
            saleRequest = saleRequest.replace("{stan}", String.valueOf(transactionRequest.getStan()));
            saleRequest = saleRequest.replace("{posEntryMode}", transactionRequest.getPointOfServiceEntryMode());
            saleRequest = saleRequest.replace("{posConditionCode}", Constants.POSCONDITIONCODE);
            saleRequest = saleRequest.replace("{track2}",transactionRequest.getTrackksn());
            saleRequest = saleRequest.replace("{terminalID}", String.valueOf(transactionRequest.getTid()));
            saleRequest = saleRequest.replace("{cardAcceptorIDCode}", String.valueOf(transactionRequest.getMid()));
            saleRequest = saleRequest.replace("{encryptionCode}", reqBody.optString("f047"));
            saleRequest = saleRequest.replace("{pinBlock}", transactionRequest.getPinBlockKsn());
            saleRequest = saleRequest.replace("{emvData}", reqBody.optString("f055"));
            saleRequest = saleRequest.replace("{batchNumber}", transactionRequest.getRequestBatchno());
            saleRequest = saleRequest.replace("{invoiceNumber}", String.valueOf(transactionRequest.getInvoiceNumber()));

            if (reqBody.optString("f052").equals("")) {
                JSONObject request = new JSONObject(saleRequest);
                request.remove("F052");
                saleRequest = request.toString();
            }
            if (reqBody.optString("cardReadType").equals("Mag")) {
                JSONObject request = new JSONObject(saleRequest);
                request.remove("F055");
                saleRequest = request.toString();
            }

            String tpacred = jsonData.opt("CALLING_CREDENTIAL").toString();
            log.info(" [URN_{}] This is Sale Request for Paynext api call {} : ", urn, new JSONObject(saleRequest));

            JSONObject tpaCred = new JSONObject(tpacred);
            JSONObject tpaCredJson = new JSONObject();
            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_REQUESTBODY.apiCallEnum, new JSONObject(saleRequest));
            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_REQUEST_TIMEOUT.apiCallEnum, "70");
            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_CALLURL.apiCallEnum, tpaCred.optString("callUrl", "http://localhost:7575/hi"));

            Map<String, Object> headerDatMap = new HashMap<>();
            headerDatMap.put("apikey", tpaCred.optString("apiKey"));
            headerDatMap.put(AccessRestEnum.RESTAPI_CONTENTTYPE.apiCallEnum, AccessRestEnum.RESTAPI_APPLICATIONJSON.apiCallEnum);

            log.info("[URN_{}] Calling Paynext API : {} ", urn, new JSONObject(saleRequest));
            responseString = AccessRestApi.apiCallPostPlainMethod("URN", urn, tpaCredJson, headerDatMap, true);
            log.info("[URN_{}] Response received from paynext {} : ", urn, responseString);
            JSONObject responseJson = new JSONObject(responseString);
            if (responseJson.optString("responseCode").equals("200")) {
                responseString = new JSONObject(responseString).optString("apiResponseData");
            } else {
                responseDto.getApiResponseData().setResponseCode(ErrorCodes.Failure_Code.errorCodes);
                responseDto.getApiResponseData().setResponseMessage(ErrorCodes.PAYNEXT_EXCEPTION.errorCodes);
                responseString = new JSONObject(responseString).optString("apiResponseData");

            }
        } catch (NullPointerException e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.PAYNEXT_EXCEPTION.errorCodes,"{}"));


        } catch (Exception e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.PAYNEXT_EXCEPTION.errorCodes,"{}"));


        }
        return responseString;
    }

    public String callPaynextApiCashAtPOS(String urn,TransactionRequest transactionRequest, ResponseDto responseDto) {
        String responseString = "";
        try {
            JSONObject reqBody = new JSONObject(responseDto.getApiResponseData().getResponseData().toString());

            InitInitializerUtil initInitializerUtil = new InitInitializerUtil();
            JSONObject jsonData = initInitializerUtil.getJsonData().getJSONArray("CASHATPOS").optJSONObject(0);
            String cashAtPOSRequest = jsonData.opt("BODY_DATA").toString();

            cashAtPOSRequest = cashAtPOSRequest.replace("{additionalAmount}", reqBody.optString("f054"));

            cashAtPOSRequest = cashAtPOSRequest.replace("{msgType}", reqBody.optString("msgType"));
            cashAtPOSRequest = cashAtPOSRequest.replace("{processingCode}", Constants.CASHATPOSPROCESSINGCODE);
            cashAtPOSRequest = cashAtPOSRequest.replace("{transactionAmount}",reqBody.optString("f004"));
            cashAtPOSRequest = cashAtPOSRequest.replace("{stan}", String.valueOf(transactionRequest.getStan()));
            cashAtPOSRequest = cashAtPOSRequest.replace("{posEntryMode}", transactionRequest.getPointOfServiceEntryMode());
            cashAtPOSRequest = cashAtPOSRequest.replace("{posConditionCode}", Constants.POSCONDITIONCODE);
            cashAtPOSRequest = cashAtPOSRequest.replace("{track2}",transactionRequest.getTrackksn());
            cashAtPOSRequest = cashAtPOSRequest.replace("{terminalID}", String.valueOf(transactionRequest.getTid()));
            cashAtPOSRequest = cashAtPOSRequest.replace("{cardAcceptorIDCode}", String.valueOf(transactionRequest.getMid()));
            cashAtPOSRequest = cashAtPOSRequest.replace("{encryptionCode}", reqBody.optString("f047"));
            cashAtPOSRequest = cashAtPOSRequest.replace("{pinBlock}", transactionRequest.getPinBlockKsn());
            cashAtPOSRequest = cashAtPOSRequest.replace("{emvData}", reqBody.optString("f055"));
            cashAtPOSRequest = cashAtPOSRequest.replace("{batchNumber}", transactionRequest.getRequestBatchno());
            cashAtPOSRequest = cashAtPOSRequest.replace("{invoiceNumber}", String.valueOf(transactionRequest.getInvoiceNumber()));

            if (reqBody.optString("pinBlock").equals("")) {
                JSONObject request = new JSONObject(cashAtPOSRequest);
                request.remove("F052");
                cashAtPOSRequest = request.toString();
            }
            if (reqBody.optString("cardReadType").equals("mag")) {
                JSONObject request = new JSONObject(cashAtPOSRequest);
                request.remove("F055");
                cashAtPOSRequest = request.toString();
            }
            String tpacred = jsonData.opt("CALLING_CREDENTIAL").toString();
            log.info(" [URN_{}] This is Cash@POS Request for Paynext api call {} : ", urn, cashAtPOSRequest);

            JSONObject tpaCred = new JSONObject(tpacred);
            JSONObject tpaCredJson = new JSONObject();
            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_REQUESTBODY.apiCallEnum, cashAtPOSRequest);
            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_REQUEST_TIMEOUT.apiCallEnum, "70");
            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_CALLURL.apiCallEnum, tpaCred.optString("callUrl", "http://localhost:7575/hi"));

            Map<String, Object> headerDatMap = new HashMap<>();
            headerDatMap.put("apikey", tpaCred.optString("apiKey"));
            headerDatMap.put(AccessRestEnum.RESTAPI_CONTENTTYPE.apiCallEnum, AccessRestEnum.RESTAPI_APPLICATIONJSON.apiCallEnum);

            log.info("[URN_{}] Calling Paynext API : {} ", urn, new JSONObject(cashAtPOSRequest));
            responseString = AccessRestApi.apiCallPostPlainMethod("URN", urn, tpaCredJson, headerDatMap, true);
            log.info("[URN_{}] Response received from paynext {} : ", urn, responseString);
            JSONObject responseJson = new JSONObject(responseString);
            if (responseJson.optString("responseCode").equals("200")) {
                responseString = new JSONObject(responseString).optString("apiResponseData");
            } else {
                responseDto.getApiResponseData().setResponseCode(ErrorCodes.Failure_Code.errorCodes);
                responseDto.getApiResponseData().setResponseMessage(ErrorCodes.PAYNEXT_EXCEPTION.errorCodes);
                responseString = new JSONObject(responseString).optString("apiResponseData");

            }
        } catch (NullPointerException e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.PAYNEXT_EXCEPTION.errorCodes,"{}"));


        } catch (Exception e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.PAYNEXT_EXCEPTION.errorCodes,"{}"));


        }

        return responseString;
    }

    public String callPaynextApiForVoid(String urn,TransactionRequest transactionRequest, ResponseDto responseDto) {
        String responseString = "";
        try {
            JSONObject reqBody = new JSONObject(responseDto.getApiResponseData().getResponseData().toString());

            InitInitializerUtil initInitializerUtil = new InitInitializerUtil();
            JSONObject jsonData = initInitializerUtil.getJsonData().getJSONArray("VOID").optJSONObject(0);
            String voidRequest = jsonData.opt("BODY_DATA").toString();

            voidRequest = voidRequest.replace("{msgType}", Constants.VOIDMSGTYPE);
            voidRequest = voidRequest.replace("{processingCode}", reqBody.optString("f003"));
            voidRequest = voidRequest.replace("{transactionAmount}", reqBody.optString("f004"));
            voidRequest = voidRequest.replace("{stan}", String.valueOf(transactionRequest.getStan()));
            voidRequest = voidRequest.replace("{terminalID}", String.valueOf(transactionRequest.getTid()));
            voidRequest = voidRequest.replace("{cardAcceptorIDCode}", String.valueOf(transactionRequest.getMid()));
            voidRequest = voidRequest.replace("{emvData}", reqBody.optString("f055"));
            voidRequest = voidRequest.replace("{batchNumber}", transactionRequest.getRequestBatchno());
            voidRequest = voidRequest.replace("{invoiceNumber}", String.valueOf(transactionRequest.getInvoiceNumber()));
            voidRequest = voidRequest.replace("{responseCode}", Constants.RESPONSECODE);

            JSONObject request = new JSONObject(voidRequest);
            request.remove("F047");
            voidRequest = request.toString();

            String tpacred = jsonData.opt("CALLING_CREDENTIAL").toString();
            log.info(" [URN_{}] This is Void Request for Paynext api call {} : ", urn, voidRequest);

            JSONObject tpaCred = new JSONObject(tpacred);
            JSONObject tpaCredJson = new JSONObject();
            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_REQUESTBODY.apiCallEnum, voidRequest);
            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_REQUEST_TIMEOUT.apiCallEnum, "70");
            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_CALLURL.apiCallEnum, tpaCred.optString("callUrl", "http://localhost:7575/hi"));

            Map<String, Object> headerDatMap = new HashMap<>();
            headerDatMap.put("apikey", tpaCred.optString("apiKey"));
            headerDatMap.put(AccessRestEnum.RESTAPI_CONTENTTYPE.apiCallEnum, AccessRestEnum.RESTAPI_APPLICATIONJSON.apiCallEnum);

            log.info("[URN_{}] Calling Paynext API {} : ", urn, new JSONObject(voidRequest));
            responseString = AccessRestApi.apiCallPostPlainMethod("URN", urn, tpaCredJson, headerDatMap, true);
            log.info("[URN_{}] Response received from paynext {} : ", urn, responseString);
            JSONObject responseJson = new JSONObject(responseString);
            if (responseJson.optString("responseCode").equals("200")) {
                responseString = new JSONObject(responseString).optString("apiResponseData");
            } else {
                responseDto.getApiResponseData().setResponseCode(ErrorCodes.Failure_Code.errorCodes);
                responseDto.getApiResponseData().setResponseMessage(ErrorCodes.PAYNEXT_EXCEPTION.errorCodes);
                responseString = new JSONObject(responseString).optString("apiResponseData");

            }
        } catch (NullPointerException e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.PAYNEXT_EXCEPTION.errorCodes,"{}"));

        } catch (Exception e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.PAYNEXT_EXCEPTION.errorCodes,"{}"));


        }

        return responseString;
    }

    public void callPaynextApiForReversal(String urn,String treqId, String reqFor, JSONObject payloadData) {
        try {

            new Thread(() -> {
                paynextHost.reversalRequest(urn,treqId, reqFor, payloadData);
                Thread.yield();
            }).start();

        } catch (NullPointerException e) {
            log.error("[URN_{}] error occurred in service layer : {} {}", urn, e.getMessage(), e);


        } catch (Exception e) {
            log.error("[URN_{}] error occurred in service layer : {}", urn, e.getMessage());

        }
    }


    @Override
    public void insertPaynextResponseToDb(String response, String urn, ResponseDto responseDto, TransactionRequest transactionRequest) {
        try {

            TransactionResponse transactionResponse = util.setPaynextResponseForDatabase(urn, transactionRequest, new JSONObject(response));

            insertRequestDataToDB.insertPaynextResponseData(transactionResponse);



        } catch (NullPointerException e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        } catch (Exception e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        }
    }

    private String convertJsonToXml(JSONObject responseFromPaynext, String urn, ResponseDto responseDto) {

        String rexml = "";
        try {

            log.info("[URN_{}] Convert Paynext Response to XML : {}", urn, responseFromPaynext);
            rexml = XML.toString(responseFromPaynext, "FLAT_RESPONSE");

        } catch (NullPointerException e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Something_Went_Wrong.errorCodes,"{}"));

        } catch (Exception e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Something_Went_Wrong.errorCodes,"{}"));

        }
        return rexml;
    }

    public void insertPaynextResponseAndRequest(String urn,String reqFor,TransactionRequest transactionRequest, String paynextResponse,String receiptData, ResponseDto responseDto) {
        try {
            log.info("[URN_{}] Inside the database to insert request and response {} : ", urn, paynextResponse);
            JSONObject jsonObject = new JSONObject(responseDto.getApiResponseData().getResponseData().toString());

            TransactionRequestResponse txn = new TransactionRequestResponse();
            JSONObject paynextJsonResponse = new JSONObject(responseDto.getApiResponseData().getResponseData().toString());
            if(new JSONObject(paynextResponse).optString("F039").equals("00")) {
                txn.setStatus(ErrorCodes.Success.errorCodes);
                if(reqFor.equals("VOID")){
                    Query query = new Query();
                    query.addCriteria(Criteria.where("mid").is(jsonObject.optString("f042")));
                    query.addCriteria(Criteria.where("tid").is(jsonObject.optString("f041")));
                    query.addCriteria(Criteria.where("amount").is(String.valueOf(Double.valueOf(jsonObject.optString("f004"))/100)));
                    query.addCriteria(Criteria.where("status").is(ErrorCodes.Success.errorCodes));
                    query.addCriteria(Criteria.where("txnId").is(jsonObject.optString("txnId")));

                    Update update = new Update();
                    update.set("isReversed",Constants.ISREVERSEDTRUE);

                    mongoTemplate.updateFirst(query, update, TransactionRequestResponse.class);

                    txn.setIsReversed(Constants.ISREVERSEDTRUE);
                } else{
                    txn.setTreqId(String.valueOf(transactionRequest.getTreqId()));
                    txn.setIsReversed(Constants.ISREVERSEDFALSE);
                }
                txn.setTxnId(new JSONObject(paynextResponse).optString("F058").split("\\n")[12].replace(" ","").split(":")[2]);
            }else {
                txn.setIsReversed(Constants.ISREVERSEDFALSE);
                txn.setStatus(ErrorCodes.Failure.errorCodes);
            }

            String cardNumber = jsonObject.optString("f002");
            int masked = cardNumber.substring(0,cardNumber.length() - 4).length() - 6;
            String mask = new String(new char[masked]).replace("\0", "*");
            String maskedCardNumber=cardNumber.substring(0, 6) + mask + cardNumber.substring(cardNumber.length() - 4, cardNumber.length());
            jsonObject.put("f002",maskedCardNumber);
            txn.setRequestFor(reqFor);
            txn.setResponse(paynextResponse);
            txn.setRequest(jsonObject.toString());
            String posId = getPosId(jsonObject.optString("f042"), jsonObject.optString("f041"), urn, responseDto);
            txn.setPosId(posId);
            txn.setCreatedOn(new SimpleDateFormat(Constants.RESPONSEDATEFORMAT).format(new Date()));
            txn.setMid(paynextJsonResponse.optString("f042"));
            txn.setTid(paynextJsonResponse.optString("f041"));
            txn.setReceiptData(receiptData);
            txn.setAmount(String.valueOf(Double.valueOf(paynextJsonResponse.optString("f004")) / 100));

            mongoTemplate.save(txn, CollectionName.TRANSACTIONREQUESTRESPONSE.collectionName);

        } catch (NullPointerException e) {
            log.error("[URN_{}] Error the inserting data into the database. {} : {} ", urn, e.getMessage());

            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        } catch (Exception e) {
            log.error("[URN_{}] Error the inserting data into the database. {} : {}", urn, e.getMessage(),e);
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        }
    }

    public String getPosId(String mid, String tid, String urn, ResponseDto responseDto){
        String posId ="";

        try {

            posId = vasDetailsRepository.getIdFromDatabase(mid, tid);

        } catch (NullPointerException e) {
            log.error("[URN_{}] Error in getting posId from the database. {} : {} ", urn, e.getMessage());

            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        } catch (Exception e) {
            log.error("[URN_{}] Error in getting posId from the database. {} : {}", urn, e.getMessage(),e);
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        }
        return posId;
    }

    @Override
    public ResponseDto setPaynextResponse(String urn, String receiptData, String response, ResponseDto res) {
        try {
            log.info("[URN_{}] Setting up Paynext response data : {}", urn, response);
            log.info("[URN_{}] Setting up Paynext receipt data", urn);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("paynextResponse", new JSONObject(response));
            jsonObject.put("receiptData", receiptData);

            if(new JSONObject(response).optString("F039").equals("")){
                res.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes, ErrorCodes.PAYNEXT_EXCEPTION.errorCodes, jsonObject.toMap()));
            } else {
                res.setApiResponseData(util.setResponse(ErrorCodes.Success_Code.errorCodes, ErrorCodes.Success.errorCodes, jsonObject.toMap()));
            }
            log.info("[URN_{}] Data for response {} : ", urn, jsonObject);

        } catch (NullPointerException e) {
            log.error("[URN_{}] Error in setting up response data. {} : {} ", urn, e.getMessage());

            res.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Something_Went_Wrong.errorCodes,"{}"));

        } catch (Exception e) {
            log.error("[URN_{}] Error in setting up response data. {} : {}", urn, e.getMessage(),e);
            res.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Something_Went_Wrong.errorCodes,"{}"));

        }
        return res;
    }

    public String setReceiptData(String paynextResponse, String urn, ResponseDto responseDto) {
        String receiptData = "";
        try {
            receiptData = new JSONObject(paynextResponse).optString("F058");
            if (receiptData.equals("")) {
                JSONObject res = new JSONObject();
                res.put("responseMessage", ErrorCodes.Receipt_Data_Does_Not_Exist);
                receiptData = res.toString();
            } else {
                receiptData = segregateField58(receiptData, responseDto);
            }
        } catch (NullPointerException e) {
            log.error("[URN_{}] Error in setting up receipt data. {} : {} ", urn, e.getMessage());

            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Something_Went_Wrong.errorCodes,"{}"));

        } catch (Exception e) {
            log.error("[URN_{}] Error in setting up receipt data. {} : {}", urn, e.getMessage(),e);
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Something_Went_Wrong.errorCodes,"{}"));

        }
        return receiptData;
    }

    public String segregateField58(String field58String, ResponseDto responseDto) {
        String chargeSlip="";

        try {
            String[] parts = field58String.split("\n");


            chargeSlip = Constants.chargeSlip;


            chargeSlip = chargeSlip.replace("{merchantoutletname}",parts[0].trim())
                    .replace("{city}",parts[2].trim())
                    .replace("{date}",parts[3].split(": ")[1].split(" ")[0])
                    .replace("{time}",parts[3].split(": ")[1].split(" ")[1])
                    .replace("{mid}",parts[5].split(":")[1].substring(0,parts[5].split(":")[1].length()-3))
                    .replace("{tid}",parts[5].split(":")[2])
                    .replace("{batchno}",parts[6].split(":")[1].split(" ")[0])
                    .replace("{invoiceno}",parts[6].split(":")[2])
                    .replace("{cardno}",parts[8].substring(0,16))
                    .replace("{cardtype}",parts[9].split(":")[1].trim())
                    .replace("{txnid}",parts[12].split(":")[2])
                    .replace("{rrn}",parts[12].split(":")[1].split(" ")[0])
                    .replace("{aid}",parts[10].split(":")[1].trim())
                    .replace("{totalamount}",parts[13].split(":")[1].trim())
                    .replace("{transactionType}",parts[7].trim());
          } catch (NullPointerException e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Something_Went_Wrong.errorCodes,"{}"));

        } catch (Exception e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Something_Went_Wrong.errorCodes,"{}"));

        }
        return chargeSlip;
    }

    public void checkMerchantLimit(String urn, String amount,String tid,String mid, ResponseDto responseModal) {
        try {
            String dailykey = Constants.MERCHANTLIMITDAILY + mid + "_" + tid;
            String weeklyKey = Constants.MERCHANTLIMITWEEKLY + mid + "_" + tid;
            String monthlyKey = Constants.MERCHANTLIMITMONTHLY + mid + "_" + tid;
            String yearlyKey = Constants.MERCHANTLIMITDAILYYEARLY + mid + "_" + tid;
            JSONObject userDailyData;
           if(Objects.isNull(redisUtil.getValue(dailykey))){
               userDailyData = new JSONObject(Constants.DAILYKEY);
           }
           else{
               userDailyData = new JSONObject(redisUtil.getValue(dailykey).toString());
           }
            String dailyRemainingLimit = userDailyData.optString("remainingLimit");
            String dailyUsedLimit = userDailyData.optString("usedLimit");

            JSONObject userWeeklyData;
            if(Objects.isNull(redisUtil.getValue(weeklyKey))){
                userWeeklyData = new JSONObject(Constants.DAILYKEY);
            }
            else{
                userWeeklyData = new JSONObject(redisUtil.getValue(weeklyKey).toString());
            }

            String weeklyRemainingLimit = userWeeklyData.optString("remainingLimit");
            String weeklyUsedLimit = userWeeklyData.optString("usedLimit");

            JSONObject userMonthlyData;
            if(Objects.isNull(redisUtil.getValue(monthlyKey))){
                userMonthlyData = new JSONObject(Constants.DAILYKEY);
            }
            else{
                userMonthlyData = new JSONObject(redisUtil.getValue(monthlyKey).toString());
            }

            String monthlyRemainingLimit = userMonthlyData.optString("remainingLimit");
            String monthlyUsedLimit = userMonthlyData.optString("usedLimit");

            JSONObject userYearlyData;
            if(Objects.isNull(redisUtil.getValue(yearlyKey))){
                userYearlyData = new JSONObject(Constants.DAILYKEY);
            }
            else{
                userYearlyData = new JSONObject(redisUtil.getValue(yearlyKey).toString());
            }

            String yearlyRemainingLimit = userYearlyData.optString("remainingLimit");
            String yearlyUsedLimit = userYearlyData.optString("usedLimit");

            dailyUsedLimit = String.valueOf(Double.valueOf(dailyUsedLimit) + Double.valueOf(amount));
            dailyRemainingLimit = String.valueOf(Double.valueOf(dailyRemainingLimit) - Double.valueOf(amount));

            weeklyUsedLimit = String.valueOf(Double.valueOf(weeklyUsedLimit) + Double.valueOf(amount));
            weeklyRemainingLimit = String.valueOf(Double.valueOf(weeklyRemainingLimit) - Double.valueOf(amount));

            monthlyUsedLimit = String.valueOf(Double.valueOf(monthlyUsedLimit) + Double.valueOf(amount));
            monthlyRemainingLimit = String.valueOf(Double.valueOf(monthlyRemainingLimit) - Double.valueOf(amount));


            yearlyUsedLimit = String.valueOf(Double.valueOf(yearlyUsedLimit) + Double.valueOf(amount));
            yearlyRemainingLimit = String.valueOf(Double.valueOf(yearlyRemainingLimit) - Double.valueOf(amount));
            if(Double.valueOf(dailyRemainingLimit) <= 0 || Double.valueOf(monthlyRemainingLimit) <= 0 || Double.valueOf(yearlyRemainingLimit) <= 0 || Double.valueOf(weeklyRemainingLimit) <= 0){
                responseModal.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Something_Went_Wrong.errorCodes,"{}"));
                log.error("[URN_{}] Merchant Limit Exceeded. ",urn);
                return ;
            }

        } catch (Exception e) {
            responseModal.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Something_Went_Wrong.errorCodes,"{}"));
            log.error("[URN_{}] Exception while checking redis key : {}", urn,e.getMessage());
        }
    }

    public void setMerchantLimit(String urn,String reqFor, String amount,String tid,String mid, ResponseDto responseModal) {
        try {
            String dailykey = Constants.MERCHANTLIMITDAILY + mid + "_" + tid;
            String weeklyKey = Constants.MERCHANTLIMITWEEKLY + mid + "_" + tid;
            String monthlyKey = Constants.MERCHANTLIMITMONTHLY + mid + "_" + tid;
            String yearlyKey = Constants.MERCHANTLIMITDAILYYEARLY + mid + "_" + tid;
            JSONObject userDailyData;
            if(Objects.isNull(redisUtil.getValue(dailykey))){
                userDailyData = new JSONObject(Constants.DAILYKEY);
            }
            else{
                userDailyData = new JSONObject(redisUtil.getValue(dailykey).toString());
            }
            String dailyRemainingLimit = userDailyData.optString("remainingLimit");
            String dailyUsedLimit = userDailyData.optString("usedLimit");

            JSONObject userWeeklyData;
            if(Objects.isNull(redisUtil.getValue(weeklyKey))){
                userWeeklyData = new JSONObject(Constants.DAILYKEY);
            }
            else{
                userWeeklyData = new JSONObject(redisUtil.getValue(weeklyKey).toString());
            }

            String weeklyRemainingLimit = userWeeklyData.optString("remainingLimit");
            String weeklyUsedLimit = userWeeklyData.optString("usedLimit");

            JSONObject userMonthlyData;
            if(Objects.isNull(redisUtil.getValue(monthlyKey))){
                userMonthlyData = new JSONObject(Constants.DAILYKEY);
            }
            else{
                userMonthlyData = new JSONObject(redisUtil.getValue(monthlyKey).toString());
            }

            String monthlyRemainingLimit = userMonthlyData.optString("remainingLimit");
            String monthlyUsedLimit = userMonthlyData.optString("usedLimit");

            JSONObject userYearlyData;
            if(Objects.isNull(redisUtil.getValue(yearlyKey))){
                userYearlyData = new JSONObject(Constants.DAILYKEY);
            }
            else{
                userYearlyData = new JSONObject(redisUtil.getValue(yearlyKey).toString());
            }

            String yearlyRemainingLimit = userYearlyData.optString("remainingLimit");
            String yearlyUsedLimit = userYearlyData.optString("usedLimit");

            if(reqFor.equals("VOID")){
                amount = "-" + amount;
            }

            dailyUsedLimit = String.valueOf(Double.valueOf(dailyUsedLimit) + Double.valueOf(amount));
            dailyRemainingLimit = String.valueOf(Double.valueOf(dailyRemainingLimit) - Double.valueOf(amount));

            weeklyUsedLimit = String.valueOf(Double.valueOf(weeklyUsedLimit) + Double.valueOf(amount));
            weeklyRemainingLimit = String.valueOf(Double.valueOf(weeklyRemainingLimit) - Double.valueOf(amount));

            monthlyUsedLimit = String.valueOf(Double.valueOf(monthlyUsedLimit) + Double.valueOf(amount));
            monthlyRemainingLimit = String.valueOf(Double.valueOf(monthlyRemainingLimit) - Double.valueOf(amount));


            yearlyUsedLimit = String.valueOf(Double.valueOf(yearlyUsedLimit) + Double.valueOf(amount));
            yearlyRemainingLimit = String.valueOf(Double.valueOf(yearlyRemainingLimit) - Double.valueOf(amount));

           if(Double.valueOf(dailyRemainingLimit) <= 0 || Double.valueOf(monthlyRemainingLimit) <= 0 || Double.valueOf(yearlyRemainingLimit) <= 0 || Double.valueOf(weeklyRemainingLimit) <= 0){
                responseModal.getApiResponseData().setResponseCode(ErrorCodes.Failure_Code.errorCodes);
                responseModal.getApiResponseData().setResponseMessage(ErrorCodes.Failure.errorCodes);
                return ;
            }
            userDailyData.put("remainingLimit",dailyRemainingLimit);
            userDailyData.put("usedLimit",dailyUsedLimit);
            userDailyData.put("updatedOn",new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            int midNightTime = 24 * 60 * 60;
            int today = new DateTime().getSecondOfDay();
            int time = midNightTime - today;
            if (Objects.nonNull(redisUtil.setValue(dailykey,userDailyData.toString(), Duration.ofSeconds(time)))) {
                log.info("[URN_{}] Key : {} is registered in redis Successfully .", urn, dailykey);
                responseModal.getApiResponseData().setResponseCode("");
                responseModal.getApiResponseData().setResponseMessage("");

            } else {
                log.error("[URN_{}] Key : {} not registered in redis .", urn, dailykey);
                responseModal.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Something_Went_Wrong.errorCodes,"{}"));

            }

            int weeklyTime = 30 * 24 * 60 * 60;
            userWeeklyData.put("remainingLimit",weeklyRemainingLimit);
            userWeeklyData.put("usedLimit",weeklyUsedLimit);
            userWeeklyData.put("updatedOn",new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            if (Objects.nonNull(redisUtil.setValue(weeklyKey,userWeeklyData.toString(), Duration.ofSeconds(weeklyTime)))) {
                log.info("[URN_{}] Key : {} is registered in redis Successfully .", urn, weeklyKey);
                responseModal.getApiResponseData().setResponseCode("");
                responseModal.getApiResponseData().setResponseMessage("");

            } else {
                log.error("[URN_{}] Key : {} not registered in redis .", urn, weeklyKey);
                responseModal.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Something_Went_Wrong.errorCodes,"{}"));

            }

            int monthlyTime = 365 * 24 * 60 * 60;
            userMonthlyData.put("remainingLimit",monthlyRemainingLimit);
            userMonthlyData.put("usedLimit",monthlyUsedLimit);
            userMonthlyData.put("updatedOn",new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            if (Objects.nonNull(redisUtil.setValue(monthlyKey,userMonthlyData.toString(), Duration.ofSeconds(monthlyTime)))) {
                log.info("[URN_{}] Key : {} is registered in redis Successfully .", urn, monthlyKey);
                responseModal.getApiResponseData().setResponseCode("");
                responseModal.getApiResponseData().setResponseMessage("");

            } else {
                log.error("[URN_{}] Key : {} not registered in redis .", urn, monthlyKey);
                responseModal.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Something_Went_Wrong.errorCodes,"{}"));

            }

            int yearlyTime = 7 * 24 * 60 * 60;

            userYearlyData.put("remainingLimit",yearlyRemainingLimit);
            userYearlyData.put("usedLimit",yearlyUsedLimit);
            userYearlyData.put("updatedOn",new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            if (Objects.nonNull(redisUtil.setValue(yearlyKey,userYearlyData.toString(), Duration.ofSeconds(yearlyTime)))) {
                log.info("[URN_{}] Key : {} is registered in redis Successfully .", urn, yearlyKey);
                responseModal.getApiResponseData().setResponseCode("");
                responseModal.getApiResponseData().setResponseMessage("");

            } else {
                log.error("[URN_{}] Key : {} not registered in redis .", urn, yearlyKey);
                responseModal.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Something_Went_Wrong.errorCodes,"{}"));

            }

        } catch (NullPointerException e) {
            log.error("[URN_{}] Exception while checking redis key : {}", urn,e.getMessage());
            responseModal.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Something_Went_Wrong.errorCodes,"{}"));

        } catch (Exception e) {
            log.error("[URN_{}] Exception while checking redis key {} : {}", urn,e.getMessage(),e);
            responseModal.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Something_Went_Wrong.errorCodes,"{}"));

        }
    }

    public void setCardDetails(String request, ResponseDto responseDto){
        try{
            String  rapiposDetails = String.valueOf(redisUtil.getValue(Constants.RAPIPOSSALEDETAILS));
            if(rapiposDetails.equals("null") || rapiposDetails.equals(null)){
                rapiposDetails = Constants.RAPIPOSDETAILS;
            }
            JSONObject cardDetailsObject = new JSONObject(rapiposDetails);
            String field58String = new JSONObject(request).optString("F058");

            if(field58String.equals("")){
                responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Something_Went_Wrong.errorCodes,"{}"));
                return ;
            }
            String[] parts = field58String.replace(" ","").split("\\n");

            String orgTxnAmount = parts[13].split(":")[1].replace("Rs.","");

            String txnType=parts[7];

            String appName = parts[11].split(":")[1];
            if(appName.equals("NA")){
                String txnCount = cardDetailsObject.optJSONObject("cardDetails").optJSONObject("otherCard").optString("txnCount");
                String txnAmount = cardDetailsObject.optJSONObject("cardDetails").optJSONObject("otherCard").optString("txnAmount");
                txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                cardDetailsObject.optJSONObject("cardDetails").optJSONObject("otherCard").put("txnCount",txnCount+1);
                cardDetailsObject.optJSONObject("cardDetails").optJSONObject("otherCard").put("txnAmount",txnAmount);
            }else if((appName.substring(parts[11].split(":")[1].length()-6).equals("CREDIT"))){
                String txnCount = cardDetailsObject.optJSONObject("cardDetails").optJSONObject("creditCard").optString("txnCount");
                String txnAmount = cardDetailsObject.optJSONObject("cardDetails").optJSONObject("creditCard").optString("txnAmount");
                txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                cardDetailsObject.optJSONObject("cardDetails").optJSONObject("creditCard").put("txnCount",txnCount+1);
                cardDetailsObject.optJSONObject("cardDetails").optJSONObject("creditCard").put("txnAmount",txnAmount);
            } else if((parts[11].split(":")[1].substring(parts[11].split(":")[1].length()-5).equals("DEBIT"))){
                String txnCount = cardDetailsObject.optJSONObject("cardDetails").optJSONObject("debitCard").optString("txnCount");
                String txnAmount = cardDetailsObject.optJSONObject("cardDetails").optJSONObject("debitCard").optString("txnAmount");
                txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                cardDetailsObject.optJSONObject("cardDetails").optJSONObject("debitCard").put("txnCount",txnCount+1);
                cardDetailsObject.optJSONObject("cardDetails").optJSONObject("debitCard").put("txnAmount",txnAmount);
            }else {
                String txnCount = cardDetailsObject.optJSONObject("cardDetails").optJSONObject("otherCard").optString("txnCount");
                String txnAmount = cardDetailsObject.optJSONObject("cardDetails").optJSONObject("otherCard").optString("txnAmount");
                txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                cardDetailsObject.optJSONObject("cardDetails").optJSONObject("otherCard").put("txnCount",txnCount+1);
                cardDetailsObject.optJSONObject("cardDetails").optJSONObject("otherCard").put("txnAmount",txnAmount);
            }
            if((parts[9].split(":")[1]).equals("VISA")){
                if(appName.equals("NA")){
                    String txnCount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").put("txnAmount", txnAmount);
                } else if((parts[11].split(":")[1].substring(parts[11].split(":")[1].length()-6).equals("CREDIT"))) {
                    String txnCount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditVisaCard").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditVisaCard").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditVisaCard").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditVisaCard").put("txnAmount", txnAmount);
                }else {
                    String txnCount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitVisaCard").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitVisaCard").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitVisaCard").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitVisaCard").put("txnAmount", txnAmount);
                }
            }else if((parts[9].split(":")[1]).equals("MASTERCARD")){
                if(appName.equals("NA")){
                    String txnCount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").put("txnAmount", txnAmount);
                } else if((parts[11].split(":")[1].substring(parts[11].split(":")[1].length()-6).equals("CREDIT"))) {
                    String txnCount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditMasterCard").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditMasterCard").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditMasterCard").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditMasterCard").put("txnAmount", txnAmount);
                }else {
                    String txnCount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitMasterCard").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitMasterCard").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitMasterCard").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitMasterCard").put("txnAmount", txnAmount);
                }
            }else if((parts[9].split(":")[1]).equals("RUPAY")) {
                if(appName.equals("NA")){
                    String txnCount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").put("txnAmount", txnAmount);
                } else if ((parts[11].split(":")[1].substring(parts[11].split(":")[1].length() - 6).equals("CREDIT"))) {
                    String txnCount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditRupayCard").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditRupayCard").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditRupayCard").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditRupayCard").put("txnAmount", txnAmount);
                } else {
                    String txnCount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitRupayCard").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitRupayCard").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitRupayCard").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitRupayCard").put("txnAmount", txnAmount);
                }
            }
            if(txnType.equals("SALE")){
                if((parts[9].split(":")[1]).equals("VISA")) {
                    String txnCount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaSale").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaSale").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaSale").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaSale").put("txnAmount", txnAmount);

                } else if((parts[9].split(":")[1]).equals("MASTERCARD")){
                    String txnCount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterSale").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterSale").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterSale").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterSale").put("txnAmount", txnAmount);

                } else{
                    String txnCount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupaySale").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupaySale").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupaySale").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupaySale").put("txnAmount", txnAmount);

                }
            } else if(txnType.equals("CASH AT POS")){
                if((parts[9].split(":")[1]).equals("VISA")) {
                    String txnCount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaCashatPos").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaCashatPos").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaCashatPos").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaCashatPos").put("txnAmount", txnAmount);

                } else if((parts[9].split(":")[1]).equals("MASTERCARD")){
                    String txnCount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterCashatPos").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterCashatPos").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterCashatPos").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterCashatPos").put("txnAmount", txnAmount);

                } else{
                    String txnCount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupayCashatPos").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupayCashatPos").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupayCashatPos").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupayCashatPos").put("txnAmount", txnAmount);

                }
            } else if(txnType.equals("VOID")) {
                if ((parts[9].split(":")[1]).equals("VISA")) {
                    String txnCount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaVoid").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaVoid").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaVoid").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaVoid").put("txnAmount", txnAmount);

                } else if ((parts[9].split(":")[1]).equals("MASTERCARD")) {
                    String txnCount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterVoid").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterVoid").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterVoid").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterVoid").put("txnAmount", txnAmount);

                } else {
                    String txnCount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupayVoid").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupayVoid").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupayVoid").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupayVoid").put("txnAmount", txnAmount);

                }
            }
        } catch (NullPointerException e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Something_Went_Wrong.errorCodes,"{}"));

        } catch (Exception e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Something_Went_Wrong.errorCodes,"{}"));

        }
    }

    public void setMerchantCardDetails(String request, ResponseDto responseDto){
        try{

            String field58String = new JSONObject(request).optString("F058");
            String[] parts = field58String.split("\\n");
            String mid = parts[5].split(":")[1].substring(0,parts[5].split(":")[1].length()-3);
            String tid = parts[5].split(":")[2];
            String merchantDetails = String.valueOf(redisUtil.getValue(Constants.MERCHANTSALEDETAILS+mid+"_"+tid));

            if(merchantDetails.equals("null") || merchantDetails.equals(null)){
                merchantDetails = Constants.RAPIPOSDETAILS;
            }
            if(field58String.equals("")){
                responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Something_Went_Wrong.errorCodes,"{}"));
                return ;
            }

            JSONObject cardDetailsObject = new JSONObject(merchantDetails);

            String orgTxnAmount = parts[13].split(":")[1].replace("Rs.","");

            String txnType=parts[7];

            String appName = parts[11].split(":")[1];
            if(appName.equals("NA")){
                String txnCount = cardDetailsObject.optJSONObject("cardDetails").optJSONObject("otherCard").optString("txnCount");
                String txnAmount = cardDetailsObject.optJSONObject("cardDetails").optJSONObject("otherCard").optString("txnAmount");
                txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                cardDetailsObject.optJSONObject("cardDetails").optJSONObject("otherCard").put("txnCount",txnCount+1);
                cardDetailsObject.optJSONObject("cardDetails").optJSONObject("otherCard").put("txnAmount",txnAmount);
            }else if((appName.substring(parts[11].split(":")[1].length()-6).equals("CREDIT"))){
                String txnCount = cardDetailsObject.optJSONObject("cardDetails").optJSONObject("creditCard").optString("txnCount");
                String txnAmount = cardDetailsObject.optJSONObject("cardDetails").optJSONObject("creditCard").optString("txnAmount");
                txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                cardDetailsObject.optJSONObject("cardDetails").optJSONObject("creditCard").put("txnCount",txnCount+1);
                cardDetailsObject.optJSONObject("cardDetails").optJSONObject("creditCard").put("txnAmount",txnAmount);
            } else if((parts[11].split(":")[1].substring(parts[11].split(":")[1].length()-5).equals("DEBIT"))){
                String txnCount = cardDetailsObject.optJSONObject("cardDetails").optJSONObject("debitCard").optString("txnCount");
                String txnAmount = cardDetailsObject.optJSONObject("cardDetails").optJSONObject("debitCard").optString("txnAmount");
                txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                cardDetailsObject.optJSONObject("cardDetails").optJSONObject("debitCard").put("txnCount",txnCount+1);
                cardDetailsObject.optJSONObject("cardDetails").optJSONObject("debitCard").put("txnAmount",txnAmount);
            }else {
                String txnCount = cardDetailsObject.optJSONObject("cardDetails").optJSONObject("otherCard").optString("txnCount");
                String txnAmount = cardDetailsObject.optJSONObject("cardDetails").optJSONObject("otherCard").optString("txnAmount");
                txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                cardDetailsObject.optJSONObject("cardDetails").optJSONObject("otherCard").put("txnCount",txnCount+1);
                cardDetailsObject.optJSONObject("cardDetails").optJSONObject("otherCard").put("txnAmount",txnAmount);
            }
            if((parts[9].split(":")[1]).equals("VISA")){
                if(appName.equals("NA")){
                    String txnCount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").put("txnAmount", txnAmount);
                } else if((parts[11].split(":")[1].substring(parts[11].split(":")[1].length()-6).equals("CREDIT"))) {
                    String txnCount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditVisaCard").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditVisaCard").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditVisaCard").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditVisaCard").put("txnAmount", txnAmount);
                }else {
                    String txnCount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitVisaCard").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitVisaCard").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitVisaCard").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitVisaCard").put("txnAmount", txnAmount);
                }
            }else if((parts[9].split(":")[1]).equals("MASTERCARD")){
                if(appName.equals("NA")){
                    String txnCount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").put("txnAmount", txnAmount);
                } else if((parts[11].split(":")[1].substring(parts[11].split(":")[1].length()-6).equals("CREDIT"))) {
                    String txnCount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditMasterCard").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditMasterCard").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditMasterCard").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditMasterCard").put("txnAmount", txnAmount);
                }else {
                    String txnCount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitMasterCard").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitMasterCard").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitMasterCard").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitMasterCard").put("txnAmount", txnAmount);
                }
            }else if((parts[9].split(":")[1]).equals("RUPAY")) {
                if(appName.equals("NA")){
                    String txnCount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("otherBrandCards").put("txnAmount", txnAmount);
                } else if ((parts[11].split(":")[1].substring(parts[11].split(":")[1].length() - 6).equals("CREDIT"))) {
                    String txnCount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditRupayCard").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditRupayCard").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditRupayCard").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("creditRupayCard").put("txnAmount", txnAmount);
                } else {
                    String txnCount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitRupayCard").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitRupayCard").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitRupayCard").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("brandCardDetails").optJSONObject("debitRupayCard").put("txnAmount", txnAmount);
                }
            }
            if(txnType.equals("SALE")){
                if((parts[9].split(":")[1]).equals("VISA")) {
                    String txnCount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaSale").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaSale").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaSale").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaSale").put("txnAmount", txnAmount);

                } else if((parts[9].split(":")[1]).equals("MASTERCARD")){
                    String txnCount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterSale").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterSale").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterSale").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterSale").put("txnAmount", txnAmount);

                } else{
                    String txnCount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupaySale").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupaySale").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupaySale").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupaySale").put("txnAmount", txnAmount);

                }
            } else if(txnType.equals("CASH AT POS")){
                if((parts[9].split(":")[1]).equals("VISA")) {
                    String txnCount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaCashatPos").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaCashatPos").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaCashatPos").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaCashatPos").put("txnAmount", txnAmount);

                } else if((parts[9].split(":")[1]).equals("MASTERCARD")){
                    String txnCount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterCashatPos").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterCashatPos").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterCashatPos").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterCashatPos").put("txnAmount", txnAmount);

                } else{
                    String txnCount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupayCashatPos").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupayCashatPos").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupayCashatPos").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupayCashatPos").put("txnAmount", txnAmount);

                }
            } else if(txnType.equals("VOID")) {
                if ((parts[9].split(":")[1]).equals("VISA")) {
                    String txnCount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaVoid").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaVoid").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaVoid").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("visaVoid").put("txnAmount", txnAmount);

                } else if ((parts[9].split(":")[1]).equals("MASTERCARD")) {
                    String txnCount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterVoid").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterVoid").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterVoid").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("masterVoid").put("txnAmount", txnAmount);

                } else {
                    String txnCount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupayVoid").optString("txnCount");
                    String txnAmount = cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupayVoid").optString("txnAmount");
                    txnAmount = String.valueOf(Double.valueOf(txnAmount) + Double.valueOf(orgTxnAmount));
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupayVoid").put("txnCount", txnCount + 1);
                    cardDetailsObject.optJSONObject("typeOfSaleDetails").optJSONObject("rupayVoid").put("txnAmount", txnAmount);

                }
            }
        } catch (NullPointerException e) {
            log.error("Error in setting up card details. {} : {} ", e.getMessage());

            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Something_Went_Wrong.errorCodes,"{}"));

        } catch (Exception e) {
            log.error("Error in setting up card details. {} : {}", e.getMessage(),e);
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Something_Went_Wrong.errorCodes,"{}"));

        }
    }

}