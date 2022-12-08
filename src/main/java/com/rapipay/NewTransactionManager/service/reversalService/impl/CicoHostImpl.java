package com.rapipay.NewTransactionManager.service.reversalService.impl;

import com.rapipay.NewTransactionManager.service.reversalService.CicoHost;
import com.rapipay.NewTransactionManager.utils.InitInitializerUtil;
import com.rapipay.webcaller.AccessRestApi;
import com.rapipay.webcaller.AccessRestEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CicoHostImpl implements CicoHost {

    private static final Logger log = LogManager.getLogger(CicoHostImpl.class);


    public void cicoReversalRequest(String urn, String paynextRequest, JSONObject requestData) {
        try {

            InitInitializerUtil initInitializerUtil = new InitInitializerUtil();
            JSONObject jsonData = initInitializerUtil.getJsonData().getJSONArray("CICOURL").optJSONObject(0);
            String url = jsonData.optJSONObject("CALLING_CREDENTIAL").optString("callUrl");

            String cicoBody = getRequestBody(urn,requestData);

            String request = new JSONObject(cicoBody).toString();
            log.info("[URN_{}] Request Body for cico",urn, request);

            JSONObject tpaCredJson = new JSONObject();
            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_REQUESTBODY.apiCallEnum, request);
            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_REQUEST_TIMEOUT.apiCallEnum, "70");
            tpaCredJson.putOpt(AccessRestEnum.RESTAPI_CALLURL.apiCallEnum, url);

            Map<String, Object> headerDatMap = new HashMap<>();
            headerDatMap.put(AccessRestEnum.RESTAPI_CONTENTTYPE.apiCallEnum, AccessRestEnum.RESTAPI_APPLICATIONJSON.apiCallEnum);
            log.info("[URN_{}] Calling  API Cico: {}", urn, request);
            String responseString = AccessRestApi.apiCallPostPlainMethod("URN", urn, tpaCredJson, headerDatMap, true);
            log.info("[URN_{}] Response received from cico {} : ", urn, responseString);

            log.info("[URN_{}] exiting from cico method",urn);

        } catch (Exception e) {
            log.error("[URN_{}] Error in cico api calling {}",urn,e.getMessage());

        }
    }

    public String getRequestBody(String urn,JSONObject requestData){
        String cicoBody = "{\"tranId\":\"{tranId}\",\"tranDate\":\"{tranDate}\",\"serviceType\":\"{serviceType}\",\"rmn\":\"{rmn}\",\"amount\":\"{amount}\",\"mdr\":\"{mdr}\",\"mdrAmount\":\"{mdrAmount}\",\"settlementAmount\":\"{settlementAmount}\",\"cardType\":\"{cardType}\",\"cardHolderName\":\"{cardHolderName}\",\"cardNumber\":\"{cardNumber}\",\"tranStatus\":\"{tranStatus}\",\"rrn\":\"{rrn}\",\"tranType\":\"{tranType}\",\"aid\":\"{aid}\",\"tc\":\"{tc}\",\"smid\":\"{smid}\"}";
        try{

            log.info("[URN_{}] Setting up request body for cico api calling",urn);
            cicoBody = cicoBody.replace("{tranId}",requestData.optString("tranId"));
            cicoBody = cicoBody.replace("{tranDate}",requestData.optString("tranDate"));
            cicoBody = cicoBody.replace("{serviceType}",requestData.optString("cicoServiceType"));
            cicoBody = cicoBody.replace("{rmn}",requestData.optString("rmn"));
            cicoBody = cicoBody.replace("{amount}",requestData.optString("amount"));
            cicoBody = cicoBody.replace("{mdr}",requestData.optString("mdr"));
            cicoBody = cicoBody.replace("{mdrAmount}",requestData.optString("mdrAmount"));
            cicoBody = cicoBody.replace("{settlementAmount}",requestData.optString("settlementAmount"));
            cicoBody = cicoBody.replace("{cardType}",requestData.optString("cardType"));
            cicoBody = cicoBody.replace("{cardHolderName}",requestData.optString("cardHolderName"));
            cicoBody = cicoBody.replace("{cardNumber}",requestData.optString("cardNumber"));
            cicoBody = cicoBody.replace("{tranStatus}",requestData.optString("tranStatus"));
            cicoBody = cicoBody.replace("{rrn}",requestData.optString("rrn"));
            cicoBody = cicoBody.replace("{tranType}",requestData.optString("tranType"));
            cicoBody = cicoBody.replace("{tc}",requestData.optString("tc"));
            cicoBody = cicoBody.replace("{aid}",requestData.optString("aid"));
            cicoBody = cicoBody.replace("{smid}",requestData.optString("smid"));

        } catch (Exception e) {
            log.error("[URN_{}] Error in setting up request body for cico api calling {}",urn,e.getMessage());
        }
        return cicoBody;
    }
}
