package com.rapipay.NewTransactionManager.responseModel;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ResponseDto {
    private String apiResponseCode;
    private String apiResponseMessage;
    private ApiResponseData apiResponseData;
    private String apiResponseFrom;
    private String apiResponseDateTime;

    public ResponseDto() {
        super();

    }

    public String getApiResponseCode() {
        return apiResponseCode;
    }

    public void setApiResponseCode(String apiResponseCode) {
        this.apiResponseCode = apiResponseCode;
    }

    public String getApiResponseMessage() {
        return apiResponseMessage;
    }

    public void setApiResponseMessage(String apiResponseMessage) {
        this.apiResponseMessage = apiResponseMessage;
    }

    public ApiResponseData getApiResponseData() {
        return apiResponseData;
    }

    public void setApiResponseData(ApiResponseData apiResponseData) {
        this.apiResponseData = apiResponseData;
    }

    public String getApiResponseFrom() {
        return apiResponseFrom;
    }

    public void setApiResponseFrom(String apiResponseFrom) {
        this.apiResponseFrom = apiResponseFrom;
    }

    public String getApiResponseDateTime() {
        return apiResponseDateTime;
    }

    public void setApiResponseDateTime(String apiResponseDateTime) {
        this.apiResponseDateTime = apiResponseDateTime;
    }

}
