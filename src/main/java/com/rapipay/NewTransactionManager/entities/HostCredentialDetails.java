package com.rapipay.NewTransactionManager.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="host_credential_details")
public class HostCredentialDetails {

    @Id
    private Long HOST_CREDENTIAL_ID;
    private String SERVICE_PROVIDER_ID;
    private String CREDENTIAL_TYPE;
    private String CREDENTIAL_NAME;
    private String CALLING_CREDENTIAL;
    private String CALLING_TYPE;
    private String BODY_DATA;
    private String CREATED_DATE;

    public HostCredentialDetails(){

    }

    public HostCredentialDetails(Long HOST_CREDENTIAL_ID, String SERVICE_PROVIDER_ID, String CREDENTIAL_TYPE, String CREDENTIAL_NAME, String CALLING_CREDENTIAL, String CALLING_TYPE, String BODY_DATA, String CREATED_DATE) {
        this.HOST_CREDENTIAL_ID = HOST_CREDENTIAL_ID;
        this.SERVICE_PROVIDER_ID = SERVICE_PROVIDER_ID;
        this.CREDENTIAL_TYPE = CREDENTIAL_TYPE;
        this.CREDENTIAL_NAME = CREDENTIAL_NAME;
        this.CALLING_CREDENTIAL = CALLING_CREDENTIAL;
        this.CALLING_TYPE = CALLING_TYPE;
        this.BODY_DATA = BODY_DATA;
        this.CREATED_DATE = CREATED_DATE;
    }

    public Long getHOST_CREDENTIAL_ID() {
        return HOST_CREDENTIAL_ID;
    }

    public void setHOST_CREDENTIAL_ID(Long HOST_CREDENTIAL_ID) {
        this.HOST_CREDENTIAL_ID = HOST_CREDENTIAL_ID;
    }

    public String getSERVICE_PROVIDER_ID() {
        return SERVICE_PROVIDER_ID;
    }

    public void setSERVICE_PROVIDER_ID(String SERVICE_PROVIDER_ID) {
        this.SERVICE_PROVIDER_ID = SERVICE_PROVIDER_ID;
    }

    public String getCREDENTIAL_TYPE() {
        return CREDENTIAL_TYPE;
    }

    public void setCREDENTIAL_TYPE(String CREDENTIAL_TYPE) {
        this.CREDENTIAL_TYPE = CREDENTIAL_TYPE;
    }

    public String getCREDENTIAL_NAME() {
        return CREDENTIAL_NAME;
    }

    public void setCREDENTIAL_NAME(String CREDENTIAL_NAME) {
        this.CREDENTIAL_NAME = CREDENTIAL_NAME;
    }

    public String getCALLING_CREDENTIAL() {
        return CALLING_CREDENTIAL;
    }

    public void setCALLING_CREDENTIAL(String CALLING_CREDENTIAL) {
        this.CALLING_CREDENTIAL = CALLING_CREDENTIAL;
    }

    public String getCALLING_TYPE() {
        return CALLING_TYPE;
    }

    public void setCALLING_TYPE(String CALLING_TYPE) {
        this.CALLING_TYPE = CALLING_TYPE;
    }

    public String getBODY_DATA() {
        return BODY_DATA;
    }

    public void setBODY_DATA(String BODY_DATA) {
        this.BODY_DATA = BODY_DATA;
    }

    public String getCREATED_DATE() {
        return CREATED_DATE;
    }

    public void setCREATED_DATE(String CREATED_DATE) {
        this.CREATED_DATE = CREATED_DATE;
    }

    @Override
    public String toString() {
        return "HostCredentialDetails{" +
                "HOST_CREDENTIAL_ID=" + HOST_CREDENTIAL_ID +
                ", SERVICE_PROVIDER_ID='" + SERVICE_PROVIDER_ID + '\'' +
                ", CREDENTIAL_TYPE='" + CREDENTIAL_TYPE + '\'' +
                ", CREDENTIAL_NAME='" + CREDENTIAL_NAME + '\'' +
                ", CALLING_CREDENTIAL='" + CALLING_CREDENTIAL + '\'' +
                ", CALLING_TYPE='" + CALLING_TYPE + '\'' +
                ", BODY_DATA='" + BODY_DATA + '\'' +
                ", CREATED_DATE='" + CREATED_DATE + '\'' +
                '}';
    }
}
