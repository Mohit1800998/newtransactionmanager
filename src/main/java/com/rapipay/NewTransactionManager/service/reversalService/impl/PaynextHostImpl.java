package com.rapipay.NewTransactionManager.service.reversalService.impl;

import com.rapipay.NewTransactionManager.dbLayer.InsertRequestDataToDB;
import com.rapipay.NewTransactionManager.entities.TransactionRequestResponse;
import com.rapipay.NewTransactionManager.entities.TransactionResponse;
import com.rapipay.NewTransactionManager.repository.PayloadDataRepository;
import com.rapipay.NewTransactionManager.repository.VasDetailsRepository;
import com.rapipay.NewTransactionManager.service.reversalService.PaynextHost;
import com.rapipay.NewTransactionManager.utils.CollectionName;
import com.rapipay.NewTransactionManager.utils.Constants;
import com.rapipay.NewTransactionManager.utils.ErrorCodes;
import com.rapipay.NewTransactionManager.utils.InitInitializerUtil;
import com.rapipay.NewTransactionManager.utils.RedisUtil;
import com.rapipay.NewTransactionManager.utils.Util;
import com.rapipay.webcaller.AccessRestApi;
import com.rapipay.webcaller.AccessRestEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class PaynextHostImpl implements PaynextHost {

    private static final Logger log = LogManager.getLogger(PaynextHostImpl.class);

    @Autowired
    InsertRequestDataToDB insertRequestDataToDB;

    @Autowired
    PayloadDataRepository payloadDataRepository;

    @Autowired
    VasDetailsRepository vasDetailsRepository;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    Util util;

    @Override
    public void reversalRequest(String urn,String treqId, String reqFor, JSONObject payloadData) {
        log.info("[URN_{}] Inside request trigger for {} txn to paynext ", urn, reqFor);
        String responseString = "";
        try {

            InitInitializerUtil initInitializerUtil = new InitInitializerUtil();
            JSONObject jsonData = initInitializerUtil.getJsonData().getJSONArray("REVERSAL").optJSONObject(0);
            String reversalRequest = jsonData.opt("BODY_DATA").toString();


            reversalRequest = reversalRequest.replace("{msgType}", Constants.VOIDMSGTYPE);
            reversalRequest = reversalRequest.replace("{processingCode}", payloadData.optString("f003"));
            reversalRequest = reversalRequest.replace("{transactionAmount}", payloadData.optString("f004"));
            reversalRequest = reversalRequest.replace("{stan}", payloadData.optString("f011"));
            reversalRequest = reversalRequest.replace("{terminalID}", payloadData.optString("f041"));
            reversalRequest = reversalRequest.replace("{cardAcceptorIDCode}", payloadData.optString("f042"));
            reversalRequest = reversalRequest.replace("{emvData}", payloadData.optString("f055"));
            reversalRequest = reversalRequest.replace("{batchNumber}", payloadData.optString("f057"));
            reversalRequest = reversalRequest.replace("{invoiceNumber}", payloadData.optString("f062"));
            if (reqFor.equals("TIMEOUT")) {
                reversalRequest = reversalRequest.replace("{responseCode}", Constants.TIMEOUT);
            }
            if (reqFor.equals("HARDWARE_FAILURE")) {
                reversalRequest = reversalRequest.replace("{responseCode}", Constants.HARDWARE_FAILURE);
            }
            if (reqFor.equals("ARPC_FAILURE")) {
                reversalRequest = reversalRequest.replace("{responseCode}", Constants.ARPC_FAILURE);
            }
            if (reqFor.equals("NO_TC_FAILURE")) {
                reversalRequest = reversalRequest.replace("{responseCode}", Constants.NO_TC_FAILURE);
            }
            JSONObject request = new JSONObject(reversalRequest);
            request.remove("F047");
            reversalRequest = request.toString();


            String tpacred = jsonData.opt("CALLING_CREDENTIAL").toString();
            log.info(" [URN_{}] This is Reversal Request for Paynext api call {} : ", urn, reversalRequest);

            JSONObject tpaCred = new JSONObject(tpacred);
            JSONObject tpaCredJson = new JSONObject();
            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_REQUESTBODY.apiCallEnum, reversalRequest);
            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_REQUEST_TIMEOUT.apiCallEnum, "70");
            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_CALLURL.apiCallEnum, tpaCred.optString("callUrl", "http://localhost:7575/hi"));

            Map<String, Object> headerDatMap = new HashMap<>();
            headerDatMap.put("apikey", tpaCred.optString("apiKey"));
            headerDatMap.put(AccessRestEnum.RESTAPI_CONTENTTYPE.apiCallEnum, AccessRestEnum.RESTAPI_APPLICATIONJSON.apiCallEnum);

            log.info("[URN_{}] Calling Paynext API {} : ", urn, new JSONObject(reversalRequest));
            responseString = AccessRestApi.apiCallPostPlainMethod("URN", urn, tpaCredJson, headerDatMap, true);
            log.info("[URN_{}] Response received from paynext {} : ", urn, responseString);
            responseString = new JSONObject(responseString).optString("apiResponseData");

            setMerchantLimit(urn, payloadData);

            TransactionResponse transactionResponse = util.setPaynextReversalResponseForDatabase(urn,treqId, payloadData, new JSONObject(responseString));

            insertRequestDataToDB.insertPaynextResponseData(transactionResponse);


            log.info("[URN_{}] Inserting Request and response to the collection", urn);

            insertPaynextResponseAndRequest(urn, reqFor, responseString, payloadData);


            log.info("[URN_{}] Calling cico api for : {}", urn, reqFor);

            cicoReversalRequest(reqFor, urn, responseString);

        } catch (JSONException e) {
            log.error("[URN_{}] Error in payenxt api calling", urn);
        }

    }

    public void insertPaynextResponseAndRequest(String urn,String reqFor, String paynextResponse,JSONObject jsonObject) {
        try {
            log.info("[URN_{}] Inside the database to insert request and response {} : ", urn, paynextResponse);

            TransactionRequestResponse txn = new TransactionRequestResponse();
            if(new JSONObject(paynextResponse).optString("F039").equals("00")) {
                txn.setStatus(ErrorCodes.Success.errorCodes);
                txn.setTxnId(new JSONObject(paynextResponse).optString("F058").split("\\n")[12].replace(" ","").split(":")[2]);
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
            }else {
                txn.setStatus(ErrorCodes.Failure.errorCodes);
                txn.setIsReversed("False");
            }
            String receiptdata = setReceiptData(paynextResponse);
            txn.setReceiptData(receiptdata);
            txn.setResponse(paynextResponse);
            txn.setRequestFor(reqFor);
            txn.setRequest(jsonObject.toString());
            String posId = getPosId(jsonObject.optString("f042"), jsonObject.optString("f041"), urn);
            txn.setPosId(posId);
            txn.setCreatedOn(new SimpleDateFormat(Constants.RESPONSEDATEFORMAT).format(new Date()));
            txn.setMid(jsonObject.optString("f042"));
            txn.setTid(jsonObject.optString("f041"));
            txn.setAmount(String.valueOf(Double.valueOf(jsonObject.optString("f004")) / 100));

            mongoTemplate.save(txn, CollectionName.TRANSACTIONREQUESTRESPONSE.collectionName);

        }  catch (NullPointerException e) {
            log.error("[URN_{}] error occurred in service layer : {} {}", urn, e.getMessage(), e);


        } catch (Exception e) {
            log.error("[URN_{}] error occurred in service layer : {}", urn, e.getMessage());

        }
    }

    public String setReceiptData(String paynextResponse) {
        String receiptData = "";
        try {
            receiptData = new JSONObject(paynextResponse).optString("F058");
            if (receiptData.equals("")) {
                JSONObject res = new JSONObject();
                res.put("responseMessage", ErrorCodes.Receipt_Data_Does_Not_Exist);
                receiptData = res.toString();
            } else {
                receiptData = segregateField58(receiptData);
            }
        }  catch (NullPointerException e) {
            log.error("[URN_{}] error occurred while setting up receipt data : {} {}",e.getMessage(), e);


        } catch (Exception e) {
            log.error("[URN_{}] error occurred while setting up receipt data : {}", e.getMessage());

        }
        return receiptData;
    }

    public String segregateField58(String field58String) {
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

        }  catch (NullPointerException e) {
            log.error("[URN_{}] error occurred while setting up receipt data : {} {}",e.getMessage(), e);


        } catch (Exception e) {
            log.error("[URN_{}] error occurred while setting up receipt data : {}", e.getMessage());

        }
        return chargeSlip;
    }

    public String getPosId(String mid, String tid, String urn){
        String posId ="";

        try {

            posId = vasDetailsRepository.getIdFromDatabase(mid, tid);

        } catch (NullPointerException e) {
            log.error("[URN_{}] Error in getting posId from the database. {} : {} ", urn, e.getMessage());


        } catch (Exception e) {
            log.error("[URN_{}] Error in getting posId from the database. {} : {}", urn, e.getMessage(),e);

        }
        return posId;
    }


    public void setMerchantLimit(String urn, JSONObject paynextResponse) {
        try {

            String mid = String.valueOf(paynextResponse.optString("f042"));
            String tid = String.valueOf(paynextResponse.optString("f041"));
            String amount = String.valueOf(Double.valueOf(paynextResponse.optString("f004"))/100);

            String dailykey = Constants.MERCHANTLIMITDAILY + mid + "_" + tid;
            String weeklyKey = Constants.MERCHANTLIMITDAILY + mid + "_" + tid;
            String monthlyKey = Constants.MERCHANTLIMITMONTHLY + mid + "_" + tid;
            String yearlyKey = Constants.MERCHANTLIMITDAILYYEARLY + mid + "_" + tid;

            JSONObject userDailyData = new JSONObject(redisUtil.getValue(dailykey).toString());

            if (userDailyData.equals(null)) {
                userDailyData = new JSONObject(Constants.DAILYKEY);
            }
            String dailyRemainingLimit = userDailyData.optString("remainingLimit");
            String dailyUsedLimit = userDailyData.optString("usedLimit");

            JSONObject userWeeklyData = new JSONObject(redisUtil.getValue(weeklyKey).toString());

            if (userWeeklyData.equals(null)) {
                userWeeklyData = new JSONObject(Constants.WEEKLYKEY);
            }

            String weeklyRemainingLimit = userWeeklyData.optString("remainingLimit");
            String weeklyUsedLimit = userWeeklyData.optString("usedLimit");

            JSONObject userMonthlyData = new JSONObject(redisUtil.getValue(monthlyKey).toString());

            if (userMonthlyData.equals(null)) {
                userMonthlyData = new JSONObject(Constants.MONTHLYKEY);
            }

            String monthlyRemainingLimit = userMonthlyData.optString("remainingLimit");
            String monthlyUsedLimit = userMonthlyData.optString("usedLimit");

            JSONObject userYearlyData = new JSONObject(redisUtil.getValue(yearlyKey).toString());

            if (userYearlyData.equals(null)) {
                userYearlyData = new JSONObject(Constants.YEARLYKEY);
            }

            String yearlyRemainingLimit = userYearlyData.optString("remainingLimit");
            String yearlyUsedLimit = userYearlyData.optString("usedLimit");

            dailyUsedLimit = String.valueOf(Double.valueOf(dailyUsedLimit) - Double.valueOf(amount));
            dailyRemainingLimit = String.valueOf(Double.valueOf(dailyRemainingLimit) + Double.valueOf(amount));

            weeklyUsedLimit = String.valueOf(Double.valueOf(weeklyUsedLimit) - Double.valueOf(amount));
            weeklyRemainingLimit = String.valueOf(Double.valueOf(weeklyRemainingLimit) + Double.valueOf(amount));

            monthlyUsedLimit = String.valueOf(Double.valueOf(monthlyUsedLimit) - Double.valueOf(amount));
            monthlyRemainingLimit = String.valueOf(Double.valueOf(monthlyRemainingLimit) + Double.valueOf(amount));


            yearlyUsedLimit = String.valueOf(Double.valueOf(yearlyUsedLimit) - Double.valueOf(amount));
            yearlyRemainingLimit = String.valueOf(Double.valueOf(yearlyRemainingLimit) + Double.valueOf(amount));
            if (Double.valueOf(dailyRemainingLimit) <= 0 || Double.valueOf(monthlyRemainingLimit) <= 0 || Double.valueOf(yearlyRemainingLimit) <= 0 || Double.valueOf(weeklyRemainingLimit) <= 0) {
                return;
            }
            userDailyData.put("remainingLimit", dailyRemainingLimit);
            userDailyData.put("usedLimit", dailyUsedLimit);
            userDailyData.put("updatedOn", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            if (Objects.nonNull(redisUtil.setValue(dailykey, userDailyData.toString()))) {
                log.info("[URN_{}] Key : {} is registered in redis Successfully .", urn, dailykey);

            } else {
                log.error("[URN_{}] Key : {} not registered in redis .", urn, dailykey);

            }

            userWeeklyData.put("remainingLimit", weeklyRemainingLimit);
            userWeeklyData.put("usedLimit", weeklyUsedLimit);
            userWeeklyData.put("updatedOn", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            if (Objects.nonNull(redisUtil.setValue(weeklyKey, userWeeklyData.toString()))) {
                log.info("[URN_{}] Key : {} is registered in redis Successfully .", urn, weeklyKey);

            } else {
                log.error("[URN_{}] Key : {} not registered in redis .", urn, weeklyKey);

            }

            userMonthlyData.put("remainingLimit", monthlyRemainingLimit);
            userMonthlyData.put("usedLimit", monthlyUsedLimit);
            userMonthlyData.put("updatedOn", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            if (Objects.nonNull(redisUtil.setValue(monthlyKey, userMonthlyData.toString()))) {
                log.info("[URN_{}] Key : {} is registered in redis Successfully .", urn, monthlyKey);

            } else {
                log.error("[URN_{}] Key : {} not registered in redis .", urn, monthlyKey);

            }
            userYearlyData.put("remainingLimit", yearlyRemainingLimit);
            userYearlyData.put("usedLimit", yearlyUsedLimit);
            userYearlyData.put("updatedOn", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
            if (Objects.nonNull(redisUtil.setValue(yearlyKey, userYearlyData.toString()))) {
                log.info("[URN_{}] Key : {} is registered in redis Successfully .", urn, yearlyKey);

            } else {
                log.error("[URN_{}] Key : {} not registered in redis .", urn, yearlyKey);

            }

        } catch (NullPointerException e) {
            log.error("[URN_{}] Exception while checking redis key : {}", urn, e.getMessage());


        } catch (Exception e) {
            log.error("[URN_{}] Exception while checking redis key {} : {}", urn, e.getMessage(), e);


        }
    }

    public void cicoReversalRequest(String reqFor, String urn, String paynextRequest) {
        try {

            String url = "";
            JSONObject responseString = new JSONObject(paynextRequest).optJSONObject("apiResponseData");
            String cicoBody = getRequestBody(urn, responseString, "Success", reqFor);

            String request = new JSONObject(cicoBody).toString();
            log.info("[URN_{}] Request Body for cico", urn, request);

            JSONObject tpaCredJson = new JSONObject();
            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_REQUESTBODY.apiCallEnum, request);
            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_REQUEST_TIMEOUT.apiCallEnum, "70");
            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_CALLURL.apiCallEnum, url);

            Map<String, Object> headerDatMap = new HashMap<>();
            headerDatMap.put(AccessRestEnum.RESTAPI_CONTENTTYPE.apiCallEnum, AccessRestEnum.RESTAPI_APPLICATIONJSON.apiCallEnum);
            log.info("[URN_{}] Calling  API Cico: {}", urn, request);
            String responsedata = AccessRestApi.apiCallPostPlainMethod("URN", urn, tpaCredJson, headerDatMap, true);
            log.info("[URN_{}] Response received from cico {} : ", urn, responsedata);
            JSONObject responseJson = new JSONObject(responsedata);

            log.info("[URN_{}] exiting from cico method", urn);

        } catch (Exception e) {
            log.error("[URN_{}] Error in cico api calling {}", urn, e.getMessage());

        }
    }

    public String getRequestBody(String urn, JSONObject requestData, String status, String reqFor) {
        String cicoBody = "{\"tranId\":\"{tranId}\",\"tranDate\":\"{tranDate}\",\"serviceType\":\"{serviceType}\",\"rmn\":\"{rmn}\",\"amount\":\"{amount}\",\"mdr\":\"{mdr}\",\"mdrAmount\":\"{mdrAmount}\",\"settlementAmount\":\"{settlementAmount}\",\"cardType\":\"{cardType}\",\"cardHolderName\":\"{cardHolderName}\",\"cardNumber\":\"{cardNumber}\",\"tranStatus\":\"{tranStatus}\",\"rrn\":\"{rrn}\",\"tranType\":\"{tranType}\",\"aid\":\"{aid}\",\"tc\":\"{tc}\",\"smid\":\"{smid}\"}";
        try {

            String[] parts = null;
            log.info("[URN_{}] Setting up request body for cico api calling", urn);
            cicoBody = cicoBody.replace("{tranId}", parts[0])
                    .replace("{tranDate}", parts[3].split(" ")[2])
                    .replace("{serviceType}", requestData.optString("cicoServiceType"))
                    .replace("{rmn}", "")
                    .replace("{amount}", parts[13].replace(" ", "").split(":")[1].substring(3))
                    .replace("{mdr}", "")
                    .replace("{mdrAmount}", "")
                    .replace("{settlementAmount}", "")
                    .replace("{cardType}", parts[9].replace(" ", "").split(":")[1])
                    .replace("{cardHolderName}", "")
                    .replace("{cardNumber}", parts[8].substring(0, 16))
                    .replace("{tranStatus}", status)
                    .replace("{rrn}", parts[12].split(":")[1].substring(0, 12))
                    .replace("{tranType}", reqFor)
                    .replace("{tc}", "");
            cicoBody = cicoBody.replace("{aid}", parts[10].replace(" ", "").split(":")[1]);
            cicoBody = cicoBody.replace("{smid}", "");

        } catch (Exception e) {
            log.error("[URN_{}] Error in setting up request body for cico api calling {}", urn, e.getMessage());
        }
        return cicoBody;
    }

}
