package com.rapipay.NewTransactionManager.service.rkiservice.impl;

import com.rapipay.NewTransactionManager.entities.RkiProcessDetails;
import com.rapipay.NewTransactionManager.entities.RkiProcessModal;
import com.rapipay.NewTransactionManager.repository.InsertRkiResponseRepository;
import com.rapipay.NewTransactionManager.repository.RkiProcessDetailsRepository;
import com.rapipay.NewTransactionManager.responseModel.ResponseDto;
import com.rapipay.NewTransactionManager.security.Cryptography;
import com.rapipay.NewTransactionManager.service.rkiservice.RkiProcessService;
import com.rapipay.NewTransactionManager.utils.ErrorCodes;
import com.rapipay.NewTransactionManager.utils.MisLogic;
import com.rapipay.NewTransactionManager.utils.ReadApplicationProperties;
import com.rapipay.NewTransactionManager.utils.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RkiProcessServiceImpl implements RkiProcessService {

    Logger log = LogManager.getLogger(RkiProcessServiceImpl.class);

    @Autowired
    RkiProcessDetailsRepository rkiProcessDetailsRepository;

    @Autowired
    MisLogic objMisLogic;

    @Autowired
    ReadApplicationProperties rpt;

    @Autowired
    RkiProcessModal objRKiProcess;

    @Autowired
    InsertRkiResponseRepository insertRkiResponseRepository;

    @Autowired
    Util util;

    @Override
    public void main(String urn, String deviceSerialNo, ResponseDto responseDto) {
        RkiProcessDetails rkiProcessDetails = new RkiProcessDetails();
        try {

            log.info("[URN_{}] Calling the Insert Data method for inserting data {}", urn, deviceSerialNo);

            insertPayloadRequestToDb(urn,deviceSerialNo, rkiProcessDetails, responseDto);

            if (responseDto.getApiResponseData().getResponseCode().equals("401")) {
                responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

                return;
            }

            objRKiProcess.setBdkKey(rkiProcessDetails.getBdk_emv_value());
            objRKiProcess.setProcessName(rpt.getPropertyData1("SWITCH_PROCESS_NAME"));
            objRKiProcess.setKsn1(deviceSerialNo);
            objMisLogic.createIpekValues(urn, objRKiProcess, responseDto);

            if (responseDto.getApiResponseData().getResponseCode().equals("401")) {
                responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

                return;
            }

            getMasterKeyFromDatabase(urn, objRKiProcess,deviceSerialNo, responseDto);

            if (responseDto.getApiResponseData().getResponseCode().equals("401")) {
                responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

                return;
            }

            setResponseData(urn, responseDto);


        } catch (NullPointerException e) {
            log.error("[URN_{}] Error while Getting RKI Key : {} {}",urn, e.getMessage(), e);
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));


        } catch (Exception e) {
            log.error("[URN_{}] Error while Getting RKI Key : {}",urn, e);
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        }

    }

    @Override
    public void insertPayloadRequestToDb(String urn,String deviceSerialNo, RkiProcessDetails rkiProcessDetails, ResponseDto responseDto) {
        try {


            log.info("[URN_{}] Calling the Database for deviceSerialNo : {}", urn, deviceSerialNo);

            JSONArray arrayList = new JSONArray(rkiProcessDetailsRepository.getRkiProcessDetails(deviceSerialNo));

            JSONArray rkiProcessData = new JSONArray(arrayList.optString(0));

            rkiProcessDetails.setBdk_emv_value(rkiProcessData.optString(0));
            rkiProcessDetails.setBdk_mag_value(rkiProcessData.optString(1));
            rkiProcessDetails.setPin_emv_value(rkiProcessData.optString(2));
            rkiProcessDetails.setPin_mag_value(rkiProcessData.optString(3));
            rkiProcessDetails.setRequest_url(rkiProcessData.optString(4));
            rkiProcessDetails.setUse_ssl(rkiProcessData.optString(5));
            rkiProcessDetails.setTimeout_value(rkiProcessData.optString(6));
            rkiProcessDetails.setKsn_ref_value(rkiProcessData.optString(7));

        } catch (NullPointerException e) {
            log.error("[URN_{}] Error while fetching Key Data: {} {}",urn, e.getMessage(), e);
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));


        } catch (Exception e) {
            log.error("[URN_{}] Error while fetching Key Data: {}",urn, e.getMessage());
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        }

    }


    public void getMasterKeyFromDatabase(String urn, RkiProcessModal rkiProcessDetails,String deviceSerialNo, ResponseDto responseDto) {
        String masterKey = null;
        try {


            log.info("[URN_{}] Calling the Database for Payload data ", urn);

            masterKey = insertRkiResponseRepository.insertRkiResponse(deviceSerialNo, rkiProcessDetails.getIpekValue());
            rkiProcessDetails.setMasterKey(masterKey);
        } catch (NullPointerException e) {
            log.error("[URN_{}] Error while getting Master Key From Database: {} {}",urn, e.getMessage(), e);
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));


        } catch (Exception e) {
            log.error("[URN_{}] Error while getting Master Key From Database: {}",urn, e.getMessage());
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        }

    }

    public void setResponseData(String urn, ResponseDto responseDto) {
        try {


            log.info("[URN_{}] Calling the Set Response Data Method ",urn);

            JSONObject objJSonResponse = new JSONObject();
            objJSonResponse.put("F048", Cryptography
                    .encryptByRsa(objRKiProcess.getIpekValue().concat("|").concat(objRKiProcess.getMasterKey())));
            objJSonResponse.put("F003", "930000");
            objJSonResponse.put("F039", "00");
            objJSonResponse.put("F063", Cryptography.encryptByRsa(objRKiProcess.getKsn1()));
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Success_Code.errorCodes,ErrorCodes.Success.errorCodes,"{}"));

            responseDto.getApiResponseData().setResponseData(objJSonResponse.toMap());
        } catch (NullPointerException e) {
            log.error("[URN_{}] Error while setting response : {} {}",urn, e.getMessage(), e);
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));


        } catch (Exception e) {
            log.error("[URN_{}] Error while setting response : {}",urn, e.getMessage());
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        }

    }
}
