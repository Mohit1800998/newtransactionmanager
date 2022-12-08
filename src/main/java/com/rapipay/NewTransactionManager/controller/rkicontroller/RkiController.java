package com.rapipay.NewTransactionManager.controller.rkicontroller;

import com.rapipay.NewTransactionManager.responseModel.ApiResponseData;
import com.rapipay.NewTransactionManager.responseModel.ResponseDto;
import com.rapipay.NewTransactionManager.service.rkiservice.RkiProcessService;
import com.rapipay.NewTransactionManager.utils.ErrorCodes;
import com.rapipay.NewTransactionManager.utils.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class RkiController {

    public static final Logger log = LogManager.getLogger(RkiController.class);

    @Autowired
    RkiProcessService rkiProcessService;

    @Autowired
    Util util;

    @GetMapping(value = "/rkiprocess")
    public ResponseDto processRki(@RequestHeader String urn, @RequestHeader String deviceSerialNo) {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setApiResponseData(new ApiResponseData());
        try {
            responseDto.setApiResponseCode(ErrorCodes.Success_Code.errorCodes);
            responseDto.setApiResponseMessage(ErrorCodes.Success.errorCodes);
            responseDto.setApiResponseData(util.setResponse("","","{}"));

            log.info("[URN_{}] Calling the main Method for decryption ", urn);
            rkiProcessService.main(urn, deviceSerialNo, responseDto);

        } catch (Exception e) {
            responseDto.setApiResponseData(util.setResponse(ErrorCodes.Failure_Code.errorCodes,ErrorCodes.Request_Body_Failure.errorCodes,"{}"));

        }
        responseDto.setApiResponseFrom("NEW RKI PROCESS");
        responseDto.setApiResponseDateTime(new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date()));
        log.info("[URN_{}] Response send from rki api. : {}" +
                " {}", urn, responseDto);
        return responseDto;
    }

}
