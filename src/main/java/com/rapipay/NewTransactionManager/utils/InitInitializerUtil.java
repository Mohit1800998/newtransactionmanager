package com.rapipay.NewTransactionManager.utils;

import com.rapipay.NewTransactionManager.repository.InitRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class InitInitializerUtil {

    Logger log = LogManager.getLogger(InitInitializerUtil.class);

    @Autowired
    InitRepository initRepository;

    public static JSONObject map;

    public void setInitializer(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("SALE",initRepository.getHostCred(100001));
        jsonObject.put("CASHATPOS", initRepository.getHostCred(100004));
        jsonObject.put("VOID", initRepository.getHostCred(100002));
        jsonObject.put("REVERSAL", initRepository.getHostCred(100003));
        jsonObject.put("TRACK", initRepository.getHostCred(100008));
        jsonObject.put("PIN", initRepository.getHostCred(100007));
        jsonObject.put("RECEIPTDATA", initRepository.getHostCred(100009));
        jsonObject.put("CICOURL", initRepository.getHostCred(100010));

        map = jsonObject;
        log.info("Initializing the Required data {}",map);

    }

    public JSONObject getJsonData(){
        return map;
    }
}
