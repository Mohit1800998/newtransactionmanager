package com.rapipay.NewTransactionManager.entities;

import org.springframework.stereotype.Component;

@Component
public class RoutingConfigDto {

	private String requestUrl;
	private String useSSL;
	private String timeoutTime;
	private String processName;
	private String masterKey;
	private String pinKey;
	private String bdk_lmk;
	private String zpk_lmk;
	private String dek_lmk;



	public String getDek_lmk() {
		return dek_lmk;
	}
	public void setDek_lmk(String dek_lmk) {
		this.dek_lmk = dek_lmk;
	}
	public String getBdk_lmk() {
		return bdk_lmk;
	}
	public void setBdk_lmk(String bdk_lmk) {
		this.bdk_lmk = bdk_lmk;
	}
	public String getZpk_lmk() {
		return zpk_lmk;
	}
	public void setZpk_lmk(String zpk_lmk) {
		this.zpk_lmk = zpk_lmk;
	}
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	public String getUseSSL() {
		return useSSL;
	}
	public void setUseSSL(String useSSL) {
		this.useSSL = useSSL;
	}
	public String getTimeoutTime() {
		return timeoutTime;
	}
	public void setTimeoutTime(String timeoutTime) {
		this.timeoutTime = timeoutTime;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public String getMasterKey() {
		return masterKey;
	}
	public void setMasterKey(String masterKey) {
		this.masterKey = masterKey;
	}
	public String getPinKey() {
		return pinKey;
	}
	public void setPinKey(String pinKey) {
		this.pinKey = pinKey;
	}


}
