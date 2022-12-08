package com.rapipay.NewTransactionManager.entities;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigInteger;

import java.sql.Timestamp;

@Entity
@Table(name="request_transaction", schema = "txn")
public class TransactionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger treqId;
    private BigInteger switchId;
    private String terminalSerialNo;
    private BigInteger tid;
    private BigInteger mid;
    private String userId;
    private String reqLatitude;
    private String reqLongitude;
    private String deviceClientIp;
    private String broadComSerialNo;
    private String requestType;
    private Double requestAmount;
    private Integer requestDay;
    private Integer requestMonth;
    private Integer requestYear;
    private String requestFinacialYear;
    private String requestDate;
    private String requestBatchno;
    private String runningSessionId;
    private String versionCode;
    private String merchnatContact;
    private String firmwareVersion;
    private String requestcode;
    private String requestdatetime;
    private Timestamp localtransactiondatetime;
    private String merchantcategoryCode;
    private String bankCode;
    private String mskcardnumber;
    private String referenceNumber;
    private String countryCode;
    private String currencyType;
    private String aid;
    private String terminalType;
    private String cardBrand;
    private String cardType;
    private String cardHolderName;
    private String ipAddress;
    private String serviceType;
    private String appVersion;
    private String macAddress;
    private String deviceFirmware;
    private String imeiNo;
    private String iin;
    private BigInteger rrNo;
    private BigInteger stan;
    private String voucherNumber;
    private String issuerbankname;
    private String readcardType;
    private String trackksn;
    private String pinBlockKsn;
    private String emvAppidentifier;
    private String emvAppname;
    private String emvTxcertificate;
    private String emvTvr;
    private String emvTsi;
    private String emvCardExpdt;
    private BigInteger invoiceNumber;
    private String customerMobile;
    private String pointOfServiceConditionCode;
    private String pointOfServiceEntryMode;
    private String tlvData;
    private BigInteger exTxnRequest;
    private Timestamp createdOn=new Timestamp(System.currentTimeMillis());
    private Double otherAmount;
    private String txnStatus;
    private String urn;

    public TransactionRequest(){

    }

    public TransactionRequest(BigInteger treqId, BigInteger switchId, String terminalSerialNo, BigInteger tid, BigInteger mid, String userId, String reqLatitude, String reqLongitude, String deviceClientIp, String broadComSerialNo, String requestType, Double requestAmount, Integer requestDay, Integer requestMonth, Integer requestYear, String requestFinacialYear, String requestDate, String requestBatchno, String runningSessionId, String versionCode, String merchnatContact, String firmwareVersion, String requestcode, String requestdatetime, Timestamp localtransactiondatetime, String merchantcategoryCode, String bankCode, String mskcardnumber, String referenceNumber, String countryCode, String currencyType, String aid, String terminalType, String cardBrand, String cardType, String cardHolderName, String ipAddress, String serviceType, String appVersion, String macAddress, String deviceFirmware, String imeiNo, String iin, BigInteger rrNo, BigInteger stan, String voucherNumber, String issuerbankname, String readcardType, String trackksn, String pinBlockKsn, String emvAppidentifier, String emvAppname, String emvTxcertificate, String emvTvr, String emvTsi, String emvCardExpdt, BigInteger invoiceNumber, String customerMobile, String pointOfServiceConditionCode, String pointOfServiceEntryMode, String tlvData, BigInteger exTxnRequest, Timestamp createdOn, Double otherAmount, String txnStatus, String urn) {
        this.treqId = treqId;
        this.switchId = switchId;
        this.terminalSerialNo = terminalSerialNo;
        this.tid = tid;
        this.mid = mid;
        this.userId = userId;
        this.reqLatitude = reqLatitude;
        this.reqLongitude = reqLongitude;
        this.deviceClientIp = deviceClientIp;
        this.broadComSerialNo = broadComSerialNo;
        this.requestType = requestType;
        this.requestAmount = requestAmount;
        this.requestDay = requestDay;
        this.requestMonth = requestMonth;
        this.requestYear = requestYear;
        this.requestFinacialYear = requestFinacialYear;
        this.requestDate = requestDate;
        this.requestBatchno = requestBatchno;
        this.runningSessionId = runningSessionId;
        this.versionCode = versionCode;
        this.merchnatContact = merchnatContact;
        this.firmwareVersion = firmwareVersion;
        this.requestcode = requestcode;
        this.requestdatetime = requestdatetime;
        this.localtransactiondatetime = localtransactiondatetime;
        this.merchantcategoryCode = merchantcategoryCode;
        this.bankCode = bankCode;
        this.mskcardnumber = mskcardnumber;
        this.referenceNumber = referenceNumber;
        this.countryCode = countryCode;
        this.currencyType = currencyType;
        this.aid = aid;
        this.terminalType = terminalType;
        this.cardBrand = cardBrand;
        this.cardType = cardType;
        this.cardHolderName = cardHolderName;
        this.ipAddress = ipAddress;
        this.serviceType = serviceType;
        this.appVersion = appVersion;
        this.macAddress = macAddress;
        this.deviceFirmware = deviceFirmware;
        this.imeiNo = imeiNo;
        this.iin = iin;
        this.rrNo = rrNo;
        this.stan = stan;
        this.voucherNumber = voucherNumber;
        this.issuerbankname = issuerbankname;
        this.readcardType = readcardType;
        this.trackksn = trackksn;
        this.pinBlockKsn = pinBlockKsn;
        this.emvAppidentifier = emvAppidentifier;
        this.emvAppname = emvAppname;
        this.emvTxcertificate = emvTxcertificate;
        this.emvTvr = emvTvr;
        this.emvTsi = emvTsi;
        this.emvCardExpdt = emvCardExpdt;
        this.invoiceNumber = invoiceNumber;
        this.customerMobile = customerMobile;
        this.pointOfServiceConditionCode = pointOfServiceConditionCode;
        this.pointOfServiceEntryMode = pointOfServiceEntryMode;
        this.tlvData = tlvData;
        this.exTxnRequest = exTxnRequest;
        this.createdOn = createdOn;
        this.otherAmount = otherAmount;
        this.txnStatus = txnStatus;
        this.urn = urn;
    }

    public BigInteger getTreqId() {
        return treqId;
    }

    public void setTreqId(BigInteger treqId) {
        this.treqId = treqId;
    }

    public BigInteger getSwitchId() {
        return switchId;
    }

    public void setSwitchId(BigInteger switchId) {
        this.switchId = switchId;
    }

    public String getTerminalSerialNo() {
        return terminalSerialNo;
    }

    public void setTerminalSerialNo(String terminalSerialNo) {
        this.terminalSerialNo = terminalSerialNo;
    }

    public BigInteger getTid() {
        return tid;
    }

    public void setTid(BigInteger tid) {
        this.tid = tid;
    }

    public BigInteger getMid() {
        return mid;
    }

    public void setMid(BigInteger mid) {
        this.mid = mid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReqLatitude() {
        return reqLatitude;
    }

    public void setReqLatitude(String reqLatitude) {
        this.reqLatitude = reqLatitude;
    }

    public String getReqLongitude() {
        return reqLongitude;
    }

    public void setReqLongitude(String reqLongitude) {
        this.reqLongitude = reqLongitude;
    }

    public String getDeviceClientIp() {
        return deviceClientIp;
    }

    public void setDeviceClientIp(String deviceClientIp) {
        this.deviceClientIp = deviceClientIp;
    }

    public String getBroadComSerialNo() {
        return broadComSerialNo;
    }

    public void setBroadComSerialNo(String broadComSerialNo) {
        this.broadComSerialNo = broadComSerialNo;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public Double getRequestAmount() {
        return requestAmount;
    }

    public void setRequestAmount(Double requestAmount) {
        this.requestAmount = requestAmount;
    }

    public Integer getRequestDay() {
        return requestDay;
    }

    public void setRequestDay(Integer requestDay) {
        this.requestDay = requestDay;
    }

    public Integer getRequestMonth() {
        return requestMonth;
    }

    public void setRequestMonth(Integer requestMonth) {
        this.requestMonth = requestMonth;
    }

    public Integer getRequestYear() {
        return requestYear;
    }

    public void setRequestYear(Integer requestYear) {
        this.requestYear = requestYear;
    }

    public String getRequestFinacialYear() {
        return requestFinacialYear;
    }

    public void setRequestFinacialYear(String requestFinacialYear) {
        this.requestFinacialYear = requestFinacialYear;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestBatchno() {
        return requestBatchno;
    }

    public void setRequestBatchno(String requestBatchno) {
        this.requestBatchno = requestBatchno;
    }

    public String getRunningSessionId() {
        return runningSessionId;
    }

    public void setRunningSessionId(String runningSessionId) {
        this.runningSessionId = runningSessionId;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getMerchnatContact() {
        return merchnatContact;
    }

    public void setMerchnatContact(String merchnatContact) {
        this.merchnatContact = merchnatContact;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getRequestcode() {
        return requestcode;
    }

    public void setRequestcode(String requestcode) {
        this.requestcode = requestcode;
    }

    public String getRequestdatetime() {
        return requestdatetime;
    }

    public void setRequestdatetime(String requestdatetime) {
        this.requestdatetime = requestdatetime;
    }

    public Timestamp getLocaltransactiondatetime() {
        return localtransactiondatetime;
    }

    public void setLocaltransactiondatetime(Timestamp localtransactiondatetime) {
        this.localtransactiondatetime = localtransactiondatetime;
    }

    public String getMerchantcategoryCode() {
        return merchantcategoryCode;
    }

    public void setMerchantcategoryCode(String merchantcategoryCode) {
        this.merchantcategoryCode = merchantcategoryCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getMskcardnumber() {
        return mskcardnumber;
    }

    public void setMskcardnumber(String mskcardnumber) {
        this.mskcardnumber = mskcardnumber;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getDeviceFirmware() {
        return deviceFirmware;
    }

    public void setDeviceFirmware(String deviceFirmware) {
        this.deviceFirmware = deviceFirmware;
    }

    public String getImeiNo() {
        return imeiNo;
    }

    public void setImeiNo(String imeiNo) {
        this.imeiNo = imeiNo;
    }

    public String getIin() {
        return iin;
    }

    public void setIin(String iin) {
        this.iin = iin;
    }

    public BigInteger getRrNo() {
        return rrNo;
    }

    public void setRrNo(BigInteger rrNo) {
        this.rrNo = rrNo;
    }

    public BigInteger getStan() {
        return stan;
    }

    public void setStan(BigInteger stan) {
        this.stan = stan;
    }

    public String getVoucherNumber() {
        return voucherNumber;
    }

    public void setVoucherNumber(String voucherNumber) {
        this.voucherNumber = voucherNumber;
    }

    public String getIssuerbankname() {
        return issuerbankname;
    }

    public void setIssuerbankname(String issuerbankname) {
        this.issuerbankname = issuerbankname;
    }

    public String getReadcardType() {
        return readcardType;
    }

    public void setReadcardType(String readcardType) {
        this.readcardType = readcardType;
    }

    public String getTrackksn() {
        return trackksn;
    }

    public void setTrackksn(String trackksn) {
        this.trackksn = trackksn;
    }

    public String getPinBlockKsn() {
        return pinBlockKsn;
    }

    public void setPinBlockKsn(String pinBlockKsn) {
        this.pinBlockKsn = pinBlockKsn;
    }

    public String getEmvAppidentifier() {
        return emvAppidentifier;
    }

    public void setEmvAppidentifier(String emvAppidentifier) {
        this.emvAppidentifier = emvAppidentifier;
    }

    public String getEmvAppname() {
        return emvAppname;
    }

    public void setEmvAppname(String emvAppname) {
        this.emvAppname = emvAppname;
    }

    public String getEmvTxcertificate() {
        return emvTxcertificate;
    }

    public void setEmvTxcertificate(String emvTxcertificate) {
        this.emvTxcertificate = emvTxcertificate;
    }

    public String getEmvTvr() {
        return emvTvr;
    }

    public void setEmvTvr(String emvTvr) {
        this.emvTvr = emvTvr;
    }

    public String getEmvTsi() {
        return emvTsi;
    }

    public void setEmvTsi(String emvTsi) {
        this.emvTsi = emvTsi;
    }

    public String getEmvCardExpdt() {
        return emvCardExpdt;
    }

    public void setEmvCardExpdt(String emvCardExpdt) {
        this.emvCardExpdt = emvCardExpdt;
    }

    public BigInteger getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(BigInteger invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
    }

    public String getPointOfServiceConditionCode() {
        return pointOfServiceConditionCode;
    }

    public void setPointOfServiceConditionCode(String pointOfServiceConditionCode) {
        this.pointOfServiceConditionCode = pointOfServiceConditionCode;
    }

    public String getPointOfServiceEntryMode() {
        return pointOfServiceEntryMode;
    }

    public void setPointOfServiceEntryMode(String pointOfServiceEntryMode) {
        this.pointOfServiceEntryMode = pointOfServiceEntryMode;
    }

    public String getTlvData() {
        return tlvData;
    }

    public void setTlvData(String tlvData) {
        this.tlvData = tlvData;
    }

    public BigInteger getExTxnRequest() {
        return exTxnRequest;
    }

    public void setExTxnRequest(BigInteger exTxnRequest) {
        this.exTxnRequest = exTxnRequest;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public Double getOtherAmount() {
        return otherAmount;
    }

    public void setOtherAmount(Double otherAmount) {
        this.otherAmount = otherAmount;
    }

    public String getTxnStatus() {
        return txnStatus;
    }

    public void setTxnStatus(String txnStatus) {
        this.txnStatus = txnStatus;
    }

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    @Override
    public String toString() {
        return "TransactionRequest{" +
                "treqId=" + treqId +
                ", switchId=" + switchId +
                ", terminalSerialNo='" + terminalSerialNo + '\'' +
                ", tid=" + tid +
                ", mid=" + mid +
                ", userId='" + userId + '\'' +
                ", reqLatitude='" + reqLatitude + '\'' +
                ", reqLongitude='" + reqLongitude + '\'' +
                ", deviceClientIp='" + deviceClientIp + '\'' +
                ", broadComSerialNo='" + broadComSerialNo + '\'' +
                ", requestType='" + requestType + '\'' +
                ", requestAmount=" + requestAmount +
                ", requestDay=" + requestDay +
                ", requestMonth=" + requestMonth +
                ", requestYear=" + requestYear +
                ", requestFinacialYear='" + requestFinacialYear + '\'' +
                ", requestDate=" + requestDate +
                ", requestBatchno='" + requestBatchno + '\'' +
                ", runningSessionId='" + runningSessionId + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", merchnatContact='" + merchnatContact + '\'' +
                ", firmwareVersion='" + firmwareVersion + '\'' +
                ", requestcode='" + requestcode + '\'' +
                ", requestdatetime=" + requestdatetime +
                ", localtransactiondatetime=" + localtransactiondatetime +
                ", merchantcategoryCode='" + merchantcategoryCode + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", mskcardnumber='" + mskcardnumber + '\'' +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", currencyType='" + currencyType + '\'' +
                ", aid='" + aid + '\'' +
                ", terminalType='" + terminalType + '\'' +
                ", cardBrand='" + cardBrand + '\'' +
                ", cardType='" + cardType + '\'' +
                ", cardHolderName='" + cardHolderName + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", deviceFirmware='" + deviceFirmware + '\'' +
                ", imeiNo='" + imeiNo + '\'' +
                ", iin='" + iin + '\'' +
                ", rrNo=" + rrNo +
                ", stan=" + stan +
                ", voucherNumber='" + voucherNumber + '\'' +
                ", issuerbankname='" + issuerbankname + '\'' +
                ", readcardType='" + readcardType + '\'' +
                ", trackksn='" + trackksn + '\'' +
                ", pinBlockKsn='" + pinBlockKsn + '\'' +
                ", emvAppidentifier='" + emvAppidentifier + '\'' +
                ", emvAppname='" + emvAppname + '\'' +
                ", emvTxcertificate='" + emvTxcertificate + '\'' +
                ", emvTvr='" + emvTvr + '\'' +
                ", emvTsi='" + emvTsi + '\'' +
                ", emvCardExpdt='" + emvCardExpdt + '\'' +
                ", invoiceNumber=" + invoiceNumber +
                ", customerMobile='" + customerMobile + '\'' +
                ", pointOfServiceConditionCode='" + pointOfServiceConditionCode + '\'' +
                ", pointOfServiceEntryMode='" + pointOfServiceEntryMode + '\'' +
                ", tlvData='" + tlvData + '\'' +
                ", exTxnRequest=" + exTxnRequest +
                ", createdOn=" + createdOn +
                ", otherAmount=" + otherAmount +
                ", txnStatus='" + txnStatus + '\'' +
                ", urn='" + urn + '\'' +
                '}';
    }
}
