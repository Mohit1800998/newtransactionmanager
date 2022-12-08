package com.rapipay.NewTransactionManager.service.transactionManagerService.impl;


import com.rapipay.NewTransactionManager.entities.TransactionRequestResponse;
import com.rapipay.NewTransactionManager.repository.TransactionResponseRepository;
import com.rapipay.NewTransactionManager.responseModel.ResponseDto;
import com.rapipay.NewTransactionManager.service.transactionManagerService.UpdateAidTcService;
import com.rapipay.NewTransactionManager.utils.CollectionName;
import com.rapipay.NewTransactionManager.utils.ErrorCodes;
import com.rapipay.NewTransactionManager.utils.InitInitializerUtil;
import com.rapipay.webcaller.AccessRestApi;
import com.rapipay.webcaller.AccessRestEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UpdateAidTcServiceImpl implements UpdateAidTcService {

    private static final Logger log = LogManager.getLogger(UpdateAidTcServiceImpl.class);

    @Autowired
    TransactionResponseRepository transactionResponseRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void updateAidAndTc(String updateAidTcRequest, String urn, ResponseDto responseDto) {
        try {
            log.info("[URN_{}] Inside Service of Update AID and TC", urn);
            JSONObject jsonUpdateAidTc = new JSONObject(updateAidTcRequest);
            int updateCount=transactionResponseRepository.updateAidTcInDB(jsonUpdateAidTc.optString("tc"),
                    new Date(),
                    "Android Device",
                    jsonUpdateAidTc.optString("rrn"),
                    jsonUpdateAidTc.optString("serviceType"),
                    jsonUpdateAidTc.optDouble("amount"));

            if(updateCount==0) {

                responseDto.getApiResponseData().setResponseCode(ErrorCodes.Failure_Code.errorCodes);
                responseDto.getApiResponseData().setResponseMessage(ErrorCodes.Something_Went_Wrong.errorCodes);
                responseDto.getApiResponseData().setResponseData("{}");

            } else {
                responseDto.getApiResponseData().setResponseCode(ErrorCodes.Success_Code.errorCodes);
                responseDto.getApiResponseData().setResponseMessage(ErrorCodes.Success.errorCodes);
                responseDto.getApiResponseData().setResponseData("{}");

                Query query = new Query();
                query.addCriteria(Criteria.where("mid").is(jsonUpdateAidTc.optString("mid")));
                query.addCriteria(Criteria.where("tid").is(jsonUpdateAidTc.optString("tid")));
                query.addCriteria(Criteria.where("amount").is(String.valueOf(Double.valueOf(jsonUpdateAidTc.optString("amount")))));
                query.addCriteria(Criteria.where("requestFor").is(jsonUpdateAidTc.optString("serviceType")));
                query.addCriteria(Criteria.where("status").is(ErrorCodes.Success.errorCodes));
                List<TransactionRequestResponse> transactionRequestResponseList = mongoTemplate.find(query, TransactionRequestResponse.class, CollectionName.TRANSACTIONREQUESTRESPONSE.collectionName);

                JSONObject transactionRequestResponses = new JSONObject(transactionRequestResponseList.get(transactionRequestResponseList.size() - 1));

                String tc = jsonUpdateAidTc.optString("tc");
                String reqFor = jsonUpdateAidTc.optString("serviceType");

                JSONObject responseData = new JSONObject(transactionRequestResponses.optString("response"));
                log.info("[URN_{}] Calling Cico api.", urn);

                cicoRequest(reqFor, tc, urn, responseData);

            }
            log.info("[URN_{}] Updating process of AID and TC completed", urn);
        } catch (Exception e) {
            log.error("[URN_{}] Exception Occurred inside service of Update AID and TC: {}", urn, e.getMessage());
            responseDto.getApiResponseData().setResponseCode(ErrorCodes.Failure_Code.errorCodes);
            responseDto.getApiResponseData().setResponseMessage(ErrorCodes.Something_Went_Wrong.errorCodes);
            responseDto.getApiResponseData().setResponseData("{}");
        }

    }

    public void cicoRequest(String reqFor,String tc, String urn, JSONObject responseString) {
        try {

            InitInitializerUtil initInitializerUtil = new InitInitializerUtil();
            JSONObject jsonData = initInitializerUtil.getJsonData().getJSONArray("CICOURL").optJSONObject(0);
            String url = new JSONObject(jsonData.optString("CALLING_CREDENTIAL")).optString("callUrl");

            String cicoBody = getRequestBody(urn, tc, responseString, "Success", reqFor);

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

    public String getRequestBody(String urn,String tc, JSONObject requestData, String status, String reqFor) {
        String cicoBody = "{\"tranId\":\"{tranId}\",\"tranDate\":\"{tranDate}\",\"serviceType\":\"{serviceType}\",\"rmn\":\"{rmn}\",\"amount\":\"{amount}\",\"mdr\":\"{mdr}\",\"mdrAmount\":\"{mdrAmount}\",\"settlementAmount\":\"{settlementAmount}\",\"cardType\":\"{cardType}\",\"cardHolderName\":\"{cardHolderName}\",\"cardNumber\":\"{cardNumber}\",\"tranStatus\":\"{tranStatus}\",\"rrn\":\"{rrn}\",\"tranType\":\"{tranType}\",\"aid\":\"{aid}\",\"tc\":\"{tc}\",\"smid\":\"{smid}\"}";
        try {

            String[] parts = requestData.optString("F058").split("\n");
            log.info("[URN_{}] Setting up request body for cico api calling", urn);
            cicoBody = cicoBody.replace("{tranId}", parts[0].trim());
            cicoBody = cicoBody.replace("{tranDate}", parts[3].split(" ")[2]);
            cicoBody = cicoBody.replace("{serviceType}", "");
            cicoBody = cicoBody.replace("{rmn}", "");
            cicoBody = cicoBody.replace("{amount}", parts[13].replace(" ", "").split(":")[1].substring(3));
            cicoBody = cicoBody.replace("{mdr}", "");
            cicoBody = cicoBody.replace("{mdrAmount}", "");
            cicoBody = cicoBody.replace("{settlementAmount}", "");
            cicoBody = cicoBody.replace("{cardType}", parts[9].replace(" ", "").split(":")[1]);
            cicoBody = cicoBody.replace("{cardHolderName}", "");
            cicoBody = cicoBody.replace("{cardNumber}", parts[8].substring(0, 16));
            cicoBody = cicoBody.replace("{tranStatus}", status);
            cicoBody = cicoBody.replace("{rrn}", parts[12].split(":")[1].substring(0, 12));
            cicoBody = cicoBody.replace("{tranType}", reqFor);
            cicoBody = cicoBody.replace("{tc}", tc);
            cicoBody = cicoBody.replace("{aid}", parts[10].replace(" ", "").split(":")[1]);
            cicoBody = cicoBody.replace("{smid}", "");

        } catch (Exception e) {
            log.error("[URN_{}] Error in setting up request body for cico api calling {}", urn, e.getMessage());
        }
        return cicoBody;
    }

}