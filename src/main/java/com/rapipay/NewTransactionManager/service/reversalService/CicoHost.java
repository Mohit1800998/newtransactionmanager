package com.rapipay.NewTransactionManager.service.reversalService;

import org.json.JSONObject;

public interface CicoHost {

    void cicoReversalRequest(String urn, String paynextRequest, JSONObject requestData);

}
