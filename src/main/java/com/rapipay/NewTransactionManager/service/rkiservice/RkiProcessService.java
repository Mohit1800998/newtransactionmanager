package com.rapipay.NewTransactionManager.service.rkiservice;

import com.rapipay.NewTransactionManager.entities.RkiProcessDetails;
import com.rapipay.NewTransactionManager.responseModel.ResponseDto;

public interface RkiProcessService {

    /**
     *
     * @param urn - Unique Reference Number that will be generated for each request.
     * @param deviceSerialNo - Device Serial Number which will be used to get the data from the database.
     * @param responseDto - Response Dto that will send back the response whether success or failure
     */
    void main(String urn,String deviceSerialNo, ResponseDto responseDto);

    /**
     *
     * @param urn - Unique Reference Number that will be generated for each request
     * @param deviceSerialNo - Device Serial Number which will be used to get the data from the database.
     * @param rkiProcessDetails - It contains the details about rki like ipek, ksn etc.
     * @param responseDto - Response Dto that will send back the response whether success or failure
     */
    void insertPayloadRequestToDb(String urn, String deviceSerialNo, RkiProcessDetails rkiProcessDetails, ResponseDto responseDto);

    }
