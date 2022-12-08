package com.rapipay.NewTransactionManager.service.reversalService;

import org.json.JSONObject;

public interface PaynextHost {

    void reversalRequest(String urn,String treqId, String reqFor, JSONObject payloadData);
}
