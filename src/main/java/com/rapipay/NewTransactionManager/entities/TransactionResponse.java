package com.rapipay.NewTransactionManager.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Table(name="response_transaction", schema = "txn")
public class TransactionResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger tres_id;
    private BigInteger treq_id;
    private BigInteger switch_id;
    private String invoice_number;
    private String terminal_id;
    private String rr_no;
    private String response_arpc;
    private String message_type;
    private String time_data;
    private String date_data;
    private String response_batchno;
    private String response_message;
    private String processing_code;
    private Double res_txn_amount;
    private String reterival_refno;
    private String acquirer_bank;
    private String switch_response_code;
    private String print_date;
    private String auth_number;
    private String status;
    private String service_type;
    private String voucher_number;
    private String is_settled;
    private String is_void;
    private String is_reversal;
    private String card_brand;
    private String issuer_bank;
    private String card_type;
    private String response_txn_id;
    private String feedback;
    private String comments;
    private String response_emv;
    private Timestamp created_on;
    private String created_by;
    private Timestamp modified_on;
    private String tc;
    private String aid;
    private String modified_by;

    public TransactionResponse(){

    }

    public TransactionResponse(BigInteger tres_id, BigInteger treq_id, BigInteger switch_id, String invoice_number, String terminal_id, String rr_no, String response_arpc, String message_type, String time_data, String date_data, String response_batchno, String response_message, String processing_code, Double res_txn_amount, String reterival_refno, String acquirer_bank, String switch_response_code, String print_date, String auth_number, String status, String service_type, String voucher_number, String is_settled, String is_void, String is_reversal, String card_brand, String issuer_bank, String card_type, String response_txn_id, String feedback, String comments, String response_emv, Timestamp created_on, String created_by, Timestamp modified_on, String tc, String aid, String modified_by) {
        this.tres_id = tres_id;
        this.treq_id = treq_id;
        this.switch_id = switch_id;
        this.invoice_number = invoice_number;
        this.terminal_id = terminal_id;
        this.rr_no = rr_no;
        this.response_arpc = response_arpc;
        this.message_type = message_type;
        this.time_data = time_data;
        this.date_data = date_data;
        this.response_batchno = response_batchno;
        this.response_message = response_message;
        this.processing_code = processing_code;
        this.res_txn_amount = res_txn_amount;
        this.reterival_refno = reterival_refno;
        this.acquirer_bank = acquirer_bank;
        this.switch_response_code = switch_response_code;
        this.print_date = print_date;
        this.auth_number = auth_number;
        this.status = status;
        this.service_type = service_type;
        this.voucher_number = voucher_number;
        this.is_settled = is_settled;
        this.is_void = is_void;
        this.is_reversal = is_reversal;
        this.card_brand = card_brand;
        this.issuer_bank = issuer_bank;
        this.card_type = card_type;
        this.response_txn_id = response_txn_id;
        this.feedback = feedback;
        this.comments = comments;
        this.response_emv = response_emv;
        this.created_on = created_on;
        this.created_by = created_by;
        this.modified_on = modified_on;
        this.tc = tc;
        this.aid = aid;
        this.modified_by = modified_by;
    }

    public BigInteger getTres_id() {
        return tres_id;
    }

    public void setTres_id(BigInteger tres_id) {
        this.tres_id = tres_id;
    }

    public BigInteger getTreq_id() {
        return treq_id;
    }

    public void setTreq_id(BigInteger treq_id) {
        this.treq_id = treq_id;
    }

    public BigInteger getSwitch_id() {
        return switch_id;
    }

    public void setSwitch_id(BigInteger switch_id) {
        this.switch_id = switch_id;
    }

    public String getInvoice_number() {
        return invoice_number;
    }

    public void setInvoice_number(String invoice_number) {
        this.invoice_number = invoice_number;
    }

    public String getTerminal_id() {
        return terminal_id;
    }

    public void setTerminal_id(String terminal_id) {
        this.terminal_id = terminal_id;
    }

    public String getRr_no() {
        return rr_no;
    }

    public void setRr_no(String rr_no) {
        this.rr_no = rr_no;
    }

    public String getResponse_arpc() {
        return response_arpc;
    }

    public void setResponse_arpc(String response_arpc) {
        this.response_arpc = response_arpc;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getTime_data() {
        return time_data;
    }

    public void setTime_data(String time_data) {
        this.time_data = time_data;
    }

    public String getDate_data() {
        return date_data;
    }

    public void setDate_data(String date_data) {
        this.date_data = date_data;
    }

    public String getResponse_batchno() {
        return response_batchno;
    }

    public void setResponse_batchno(String response_batchno) {
        this.response_batchno = response_batchno;
    }

    public String getResponse_message() {
        return response_message;
    }

    public void setResponse_message(String response_message) {
        this.response_message = response_message;
    }

    public String getProcessing_code() {
        return processing_code;
    }

    public void setProcessing_code(String processing_code) {
        this.processing_code = processing_code;
    }

    public Double getRes_txn_amount() {
        return res_txn_amount;
    }

    public void setRes_txn_amount(Double res_txn_amount) {
        this.res_txn_amount = res_txn_amount;
    }

    public String getReterival_refno() {
        return reterival_refno;
    }

    public void setReterival_refno(String reterival_refno) {
        this.reterival_refno = reterival_refno;
    }

    public String getAcquirer_bank() {
        return acquirer_bank;
    }

    public void setAcquirer_bank(String acquirer_bank) {
        this.acquirer_bank = acquirer_bank;
    }

    public String getSwitch_response_code() {
        return switch_response_code;
    }

    public void setSwitch_response_code(String switch_response_code) {
        this.switch_response_code = switch_response_code;
    }

    public String getPrint_date() {
        return print_date;
    }

    public void setPrint_date(String print_date) {
        this.print_date = print_date;
    }

    public String getAuth_number() {
        return auth_number;
    }

    public void setAuth_number(String auth_number) {
        this.auth_number = auth_number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getVoucher_number() {
        return voucher_number;
    }

    public void setVoucher_number(String voucher_number) {
        this.voucher_number = voucher_number;
    }

    public String getIs_settled() {
        return is_settled;
    }

    public void setIs_settled(String is_settled) {
        this.is_settled = is_settled;
    }

    public String getIs_void() {
        return is_void;
    }

    public void setIs_void(String is_void) {
        this.is_void = is_void;
    }

    public String getIs_reversal() {
        return is_reversal;
    }

    public void setIs_reversal(String is_reversal) {
        this.is_reversal = is_reversal;
    }

    public String getCard_brand() {
        return card_brand;
    }

    public void setCard_brand(String card_brand) {
        this.card_brand = card_brand;
    }

    public String getIssuer_bank() {
        return issuer_bank;
    }

    public void setIssuer_bank(String issuer_bank) {
        this.issuer_bank = issuer_bank;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public String getResponse_txn_id() {
        return response_txn_id;
    }

    public void setResponse_txn_id(String response_txn_id) {
        this.response_txn_id = response_txn_id;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getResponse_emv() {
        return response_emv;
    }

    public void setResponse_emv(String response_emv) {
        this.response_emv = response_emv;
    }

    public Timestamp getCreated_on() {
        return created_on;
    }

    public void setCreated_on(Timestamp created_on) {
        this.created_on = created_on;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public Timestamp getModified_on() {
        return modified_on;
    }

    public void setModified_on(Timestamp modified_on) {
        this.modified_on = modified_on;
    }

    public String getTc() {
        return tc;
    }

    public void setTc(String tc) {
        this.tc = tc;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getModified_by() {
        return modified_by;
    }

    public void setModified_by(String modified_by) {
        this.modified_by = modified_by;
    }

    @Override
    public String toString() {
        return "TransactionResponseRepository{" +
                "tres_id=" + tres_id +
                ", treq_id=" + treq_id +
                ", switch_id=" + switch_id +
                ", invoice_number='" + invoice_number + '\'' +
                ", terminal_id='" + terminal_id + '\'' +
                ", rr_no='" + rr_no + '\'' +
                ", response_arpc='" + response_arpc + '\'' +
                ", message_type='" + message_type + '\'' +
                ", time_data='" + time_data + '\'' +
                ", date_data='" + date_data + '\'' +
                ", response_batchno='" + response_batchno + '\'' +
                ", response_message='" + response_message + '\'' +
                ", processing_code='" + processing_code + '\'' +
                ", res_txn_amount=" + res_txn_amount +
                ", reterival_refno='" + reterival_refno + '\'' +
                ", acquirer_bank='" + acquirer_bank + '\'' +
                ", switch_response_code='" + switch_response_code + '\'' +
                ", print_date='" + print_date + '\'' +
                ", auth_number='" + auth_number + '\'' +
                ", status='" + status + '\'' +
                ", service_type='" + service_type + '\'' +
                ", voucher_number='" + voucher_number + '\'' +
                ", is_settled='" + is_settled + '\'' +
                ", is_void='" + is_void + '\'' +
                ", is_reversal='" + is_reversal + '\'' +
                ", card_brand='" + card_brand + '\'' +
                ", issuer_bank='" + issuer_bank + '\'' +
                ", card_type='" + card_type + '\'' +
                ", response_txn_id='" + response_txn_id + '\'' +
                ", feedback='" + feedback + '\'' +
                ", comments='" + comments + '\'' +
                ", response_emv='" + response_emv + '\'' +
                ", created_on=" + created_on +
                ", created_by='" + created_by + '\'' +
                ", modified_on=" + modified_on +
                ", tc='" + tc + '\'' +
                ", aid='" + aid + '\'' +
                ", modified_by='" + modified_by + '\'' +
                '}';
    }
}
