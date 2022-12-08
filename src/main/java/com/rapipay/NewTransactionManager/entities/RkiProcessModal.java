package com.rapipay.NewTransactionManager.entities;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value="prototype")
public class RkiProcessModal {

	private String bdkKey;
	private String pinKey;
	private String ksn1;		//pinKsn
	private String ksn2;	//trackKsn
	private String ksn3;
	private String tidValue;
	private String processingCode;
	private String ipekValue;
	private String mtiValue;
	private String processName;
	private String requestUrl;
	private String useSSL;
	private String timeoutTime;
	private String masterKey;
	private String ksnRefNo;
	
	public String getBdkKey() {
		return bdkKey;
	}
	public void setBdkKey(String bdkKey) {
		this.bdkKey = bdkKey;
	}
	public String getPinKey() {
		return pinKey;
	}
	public void setPinKey(String pinKey) {
		this.pinKey = pinKey;
	}
	public String getKsn1() {
		return ksn1;
	}
	public void setKsn1(String ksn1) {
		this.ksn1 = ksn1;
	}
	public String getKsn2() {
		return ksn2;
	}
	public void setKsn2(String ksn2) {
		this.ksn2 = ksn2;
	}
	
	public String getKsn3() {
		return ksn3;
	}
	public void setKsn3(String ksn3) {
		this.ksn3 = ksn3;
	}
	public String getTidValue() {
		return tidValue;
	}
	public void setTidValue(String tidValue) {
		this.tidValue = tidValue;
	}
	public String getProcessingCode() {
		return processingCode;
	}
	public void setProcessingCode(String processingCode) {
		this.processingCode = processingCode;
	}
	public String getIpekValue() {
		return ipekValue;
	}
	public void setIpekValue(String ipekValue) {
		this.ipekValue = ipekValue;
	}
	public String getMtiValue() {
		return mtiValue;
	}
	public void setMtiValue(String mtiValue) {
		this.mtiValue = mtiValue;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
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
	public String getMasterKey() {
		return masterKey;
	}
	public void setMasterKey(String masterKey) {
		this.masterKey = masterKey;
	}
	public String getKsnRefNo() {
		return ksnRefNo;
	}
	public void setKsnRefNo(String ksnRefNo) {
		this.ksnRefNo = ksnRefNo;
	}
	
	
}
