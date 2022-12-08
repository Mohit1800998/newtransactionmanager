package com.rapipay.NewTransactionManager.service.transactionManagerService;

import com.rapipay.NewTransactionManager.responseModel.ResponseDto;

/**
 * - Update Aid and Tc service which will update the tc in the database for api calling.
 * @author Mohit Yadav
 * @created on 10/11/22
 */
public interface UpdateAidTcService {

    /**
     * - Update Aid and Tc service which will update the tc in the database for api calling.
     * @param updateAidTcRequest - Update request data which contains aid, tc, rrn, mid, and tid.
     * @param urn - Unique Reference Number that will be generated for each request
     * @param responseDto - Response Dto that will send back the response whether success or failure
     */
    void updateAidAndTc(String updateAidTcRequest, String urn, ResponseDto responseDto);
}
