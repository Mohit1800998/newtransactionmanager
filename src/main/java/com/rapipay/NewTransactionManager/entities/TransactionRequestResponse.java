package com.rapipay.NewTransactionManager.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Document("transaction_request_response")
public class TransactionRequestResponse {

    @Id
    private String id;
    private String mid;
    private String tid;
    private String txnId;
    private String amount;
    private String posId;
    private String request;
    private String response;
    private String status;
    private String receiptData;
    private String createdOn;
    private String requestFor;
    private String isReversed;
    private String treqId;

    public TransactionRequestResponse(){

    }

    public TransactionRequestResponse(String id, String mid, String tid, String txnId, String amount, String posId, String request, String response, String status, String receiptData, String createdOn, String requestFor, String isReversed, String treqId) {
        this.id = id;
        this.mid = mid;
        this.tid = tid;
        this.txnId = txnId;
        this.amount = amount;
        this.posId = posId;
        this.request = request;
        this.response = response;
        this.status = status;
        this.receiptData = receiptData;
        this.createdOn = createdOn;
        this.requestFor = requestFor;
        this.isReversed = isReversed;
        this.treqId = treqId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPosId() {
        return posId;
    }

    public void setPosId(String posId) {
        this.posId = posId;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReceiptData() {
        return receiptData;
    }

    public void setReceiptData(String receiptData) {
        this.receiptData = receiptData;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getRequestFor() {
        return requestFor;
    }

    public void setRequestFor(String requestFor) {
        this.requestFor = requestFor;
    }

    public String getIsReversed() {
        return isReversed;
    }

    public void setIsReversed(String isReversed) {
        this.isReversed = isReversed;
    }

    public String getTreqId() {
        return treqId;
    }

    public void setTreqId(String treqId) {
        this.treqId = treqId;
    }

    @Override
    public String toString() {
        return "TransactionRequestResponse{" +
                "id='" + id + '\'' +
                ", mid='" + mid + '\'' +
                ", tid='" + tid + '\'' +
                ", txnId='" + txnId + '\'' +
                ", amount='" + amount + '\'' +
                ", posId='" + posId + '\'' +
                ", request='" + request + '\'' +
                ", response='" + response + '\'' +
                ", status='" + status + '\'' +
                ", receiptData='" + receiptData + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", requestFor='" + requestFor + '\'' +
                ", isReversed='" + isReversed + '\'' +
                ", treqId='" + treqId + '\'' +
                '}';
    }
}