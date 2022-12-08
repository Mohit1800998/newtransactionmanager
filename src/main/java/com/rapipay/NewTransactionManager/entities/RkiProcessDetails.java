package com.rapipay.NewTransactionManager.entities;

import org.springframework.stereotype.Component;

@Component
public class RkiProcessDetails {

    private String bdk_emv_value;
    private String bdk_mag_value;
    private String pin_emv_value;
    private String pin_mag_value;
    private String request_url;
    private String use_ssl;
    private String timeout_value;
    private String ksn_ref_value;


    public RkiProcessDetails(){

    }

    public RkiProcessDetails(String bdk_emv_value, String bdk_mag_value, String pin_emv_value, String pin_mag_value, String request_url, String use_ssl, String timeout_value, String ksn_ref_value) {
        this.bdk_emv_value = bdk_emv_value;
        this.bdk_mag_value = bdk_mag_value;
        this.pin_emv_value = pin_emv_value;
        this.pin_mag_value = pin_mag_value;
        this.request_url = request_url;
        this.use_ssl = use_ssl;
        this.timeout_value = timeout_value;
        this.ksn_ref_value = ksn_ref_value;
    }

    public String getBdk_emv_value() {
        return bdk_emv_value;
    }

    public void setBdk_emv_value(String bdk_emv_value) {
        this.bdk_emv_value = bdk_emv_value;
    }

    public String getBdk_mag_value() {
        return bdk_mag_value;
    }

    public void setBdk_mag_value(String bdk_mag_value) {
        this.bdk_mag_value = bdk_mag_value;
    }

    public String getPin_emv_value() {
        return pin_emv_value;
    }

    public void setPin_emv_value(String pin_emv_value) {
        this.pin_emv_value = pin_emv_value;
    }

    public String getPin_mag_value() {
        return pin_mag_value;
    }

    public void setPin_mag_value(String pin_mag_value) {
        this.pin_mag_value = pin_mag_value;
    }

    public String getRequest_url() {
        return request_url;
    }

    public void setRequest_url(String request_url) {
        this.request_url = request_url;
    }

    public String getUse_ssl() {
        return use_ssl;
    }

    public void setUse_ssl(String use_ssl) {
        this.use_ssl = use_ssl;
    }

    public String getTimeout_value() {
        return timeout_value;
    }

    public void setTimeout_value(String timeout_value) {
        this.timeout_value = timeout_value;
    }

    public String getKsn_ref_value() {
        return ksn_ref_value;
    }

    public void setKsn_ref_value(String ksn_ref_value) {
        this.ksn_ref_value = ksn_ref_value;
    }

    @Override
    public String toString() {
        return "RkiProcessDetails{" +
                "bdk_emv_value='" + bdk_emv_value + '\'' +
                ", bdk_mag_value='" + bdk_mag_value + '\'' +
                ", pin_emv_value='" + pin_emv_value + '\'' +
                ", pin_mag_value='" + pin_mag_value + '\'' +
                ", request_url='" + request_url + '\'' +
                ", use_ssl='" + use_ssl + '\'' +
                ", timeout_value='" + timeout_value + '\'' +
                ", ksn_ref_value='" + ksn_ref_value + '\'' +
                '}';
    }
}
