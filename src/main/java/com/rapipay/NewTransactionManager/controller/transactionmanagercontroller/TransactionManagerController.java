package com.rapipay.NewTransactionManager.controller.transactionmanagercontroller;


import com.rapipay.NewTransactionManager.service.transactionManagerService.TransactionManagerService;
import com.rapipay.NewTransactionManager.service.transactionManagerService.UpdateAidTcService;
import com.rapipay.NewTransactionManager.utils.Constants;
import com.rapipay.NewTransactionManager.utils.ErrorCodes;
import com.rapipay.NewTransactionManager.responseModel.ApiResponseData;
import com.rapipay.NewTransactionManager.responseModel.ResponseDto;
import com.rapipay.NewTransactionManager.utils.InitInitializerUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class TransactionManagerController {

    public static final Logger log = LogManager.getLogger(TransactionManagerController.class);

    @Autowired
    TransactionManagerService transactionManagerService;

    @Autowired
    InitInitializerUtil initInitializerUtil;

    @Autowired
    UpdateAidTcService updateAidTcService;

    @PostMapping(value = "/payloadBody")
    public ResponseDto processTransaction(@RequestHeader String urn, @RequestParam String reqFor, @RequestHeader String aesKey, @RequestBody String encBody) {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setApiResponseData(new ApiResponseData());
        try {

            responseDto.setApiResponseCode(ErrorCodes.Success_Code.errorCodes);
            responseDto.setApiResponseMessage(ErrorCodes.Success.errorCodes);
            String requestBody = new JSONObject(encBody).optString("payloadBody");
            log.info("[URN_{}] Calling the main Method for decryption ", urn);
            transactionManagerService.main(urn, reqFor, aesKey, requestBody, responseDto);

        } catch (NullPointerException e) {
            log.error("[URN_{}] Error in Transaction manager api >>>> {} , {} ",urn, e.getMessage(), e);
            responseDto.getApiResponseData().setResponseCode(ErrorCodes.Failure_Code.errorCodes);
            responseDto.getApiResponseData().setResponseMessage(ErrorCodes.Something_Went_Wrong.errorCodes);
            responseDto.getApiResponseData().setResponseData("{}");

        } catch (Exception e) {
            log.error("[URN_{}] Error in Transaction manager api >>>> {}",urn, e.getMessage());
            responseDto.getApiResponseData().setResponseCode(ErrorCodes.Failure_Code.errorCodes);
            responseDto.getApiResponseData().setResponseMessage(ErrorCodes.Something_Went_Wrong.errorCodes);
            responseDto.getApiResponseData().setResponseData("{}");
        }
        responseDto.setApiResponseFrom(Constants.TRANSACTIONMANAGER);
        responseDto.setApiResponseDateTime(new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date()));
        return responseDto;
    }

    @GetMapping(value = "/updateInit")
    public ResponseDto updateInit(@RequestHeader String urn) {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setApiResponseData(new ApiResponseData());
        initInitializerUtil.setInitializer();
        try {
            JSONObject data = initInitializerUtil.map;
            log.info("[URN_{}] Inside Transaction Manager " +
                    " {}", urn, data);
            responseDto.getApiResponseData().setResponseCode(ErrorCodes.Success_Code.errorCodes);
            responseDto.getApiResponseData().setResponseMessage(ErrorCodes.Success.errorCodes);
            responseDto.getApiResponseData().setResponseData("{}");
        } catch (NullPointerException e) {
            log.error("[URN_{}] Error in Transaction manager api >>>> {} , {} ",urn, e.getMessage(), e);

            responseDto.getApiResponseData().setResponseCode(ErrorCodes.Failure_Code.errorCodes);
            responseDto.getApiResponseData().setResponseMessage(ErrorCodes.Something_Went_Wrong.errorCodes);
            responseDto.getApiResponseData().setResponseData("{}");

        } catch (Exception e) {
            log.error("[URN_{}] Error in Transaction manager api >>>> {}",urn, e.getMessage());
            responseDto.getApiResponseData().setResponseCode(ErrorCodes.Failure_Code.errorCodes);
            responseDto.getApiResponseData().setResponseMessage(ErrorCodes.Something_Went_Wrong.errorCodes);
            responseDto.getApiResponseData().setResponseData("{}");
        }
        responseDto.setApiResponseCode(ErrorCodes.Success_Code.errorCodes);
        responseDto.setApiResponseMessage(ErrorCodes.Success.errorCodes);
        responseDto.setApiResponseFrom(Constants.TRANSACTIONMANAGER);
        responseDto.setApiResponseDateTime(new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date()));
        log.info("[URN_{}] Response send from the transaction manager api : {}" +
                " {}", urn, responseDto);
        return responseDto;

    }

    @GetMapping(value = "/checkData")
    public ResponseDto checkUpdatedData(String urn) {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setApiResponseData(new ApiResponseData());
        try {
            JSONObject data = initInitializerUtil.map;
            log.info("[URN_{}] Inside Transaction Manager " +
                    " {}", urn, data);
            responseDto.getApiResponseData().setResponseCode(ErrorCodes.Success_Code.errorCodes);
            responseDto.getApiResponseData().setResponseMessage(ErrorCodes.Success.errorCodes);
            responseDto.getApiResponseData().setResponseData(data.toString());
        } catch (NullPointerException e) {

            responseDto.getApiResponseData().setResponseCode(ErrorCodes.Failure_Code.errorCodes);
            responseDto.getApiResponseData().setResponseMessage(ErrorCodes.Something_Went_Wrong.errorCodes);
            responseDto.getApiResponseData().setResponseData("{}");

        } catch (Exception e) {
            responseDto.getApiResponseData().setResponseCode(ErrorCodes.Failure_Code.errorCodes);
            responseDto.getApiResponseData().setResponseMessage(ErrorCodes.Something_Went_Wrong.errorCodes);
            responseDto.getApiResponseData().setResponseData("{}");
        }
        responseDto.setApiResponseCode(ErrorCodes.Success_Code.errorCodes);
        responseDto.setApiResponseMessage(ErrorCodes.Success.errorCodes);
        responseDto.setApiResponseFrom(Constants.TRANSACTIONMANAGER);
        responseDto.setApiResponseDateTime(new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date()));
        return responseDto;

    }

    @PostMapping("/updateAidTc")
    public ResponseDto updateAidTC(@RequestBody String updateAidTcRequest,
                                   @RequestHeader String urn) {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setApiResponseData(new ApiResponseData());
        try {
            log.info("[URN_{}] Inside Update AID TC API: {}", urn, new JSONObject(updateAidTcRequest));
            responseDto.setApiResponseCode(ErrorCodes.Success_Code.errorCodes);
            responseDto.setApiResponseMessage(ErrorCodes.Success.errorCodes);
            updateAidTcService.updateAidAndTc(updateAidTcRequest,urn, responseDto);
        } catch (NullPointerException e) {
            log.error("[URN_{}] Null Pointer Exception while updating AID and TC in API: {}", urn, e.getMessage());
            responseDto.getApiResponseData().setResponseCode(ErrorCodes.Failure_Code.errorCodes);
            responseDto.getApiResponseData().setResponseMessage(ErrorCodes.Something_Went_Wrong.errorCodes);
            responseDto.getApiResponseData().setResponseData("{}");
        } catch (Exception e) {
            log.error("[URN_{}] EXCEPTION OCCURRED while updating AID and TC in API {}", urn, e.getMessage());
            responseDto.getApiResponseData().setResponseCode(ErrorCodes.Failure_Code.errorCodes);
            responseDto.getApiResponseData().setResponseMessage(ErrorCodes.Something_Went_Wrong.errorCodes);
            responseDto.getApiResponseData().setResponseData("{}");
        }
        responseDto.setApiResponseFrom("UPDATE_AID_TC");
        responseDto.setApiResponseDateTime((new SimpleDateFormat(Constants.RESPONSEDATEFORMAT)).format(new Date()));
        log.info("Response from Update AID and TC API {}", new JSONObject(responseDto));
        return responseDto;
    }
}
