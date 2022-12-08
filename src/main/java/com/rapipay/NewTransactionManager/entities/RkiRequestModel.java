package com.rapipay.NewTransactionManager.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(schema = "RKI", name = "RkiProcessDetails")
public class RkiRequestModel {

    @Id
    private Integer id;
    private String tid;
    private String ksn;
    private String ipek_value;
    private String response_code;
    private String ksn_ref_value;
    private String created_on;
    private String updated_on;

    public RkiRequestModel(){

    }

    public RkiRequestModel(Integer id, String tid, String ksn, String ipek_value, String response_code, String ksn_ref_value, String created_on, String updated_on) {
        this.id = id;
        this.tid = tid;
        this.ksn = ksn;
        this.ipek_value = ipek_value;
        this.response_code = response_code;
        this.ksn_ref_value = ksn_ref_value;
        this.created_on = created_on;
        this.updated_on = updated_on;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getKsn() {
        return ksn;
    }

    public void setKsn(String ksn) {
        this.ksn = ksn;
    }

    public String getIpek_value() {
        return ipek_value;
    }

    public void setIpek_value(String ipek_value) {
        this.ipek_value = ipek_value;
    }

    public String getResponse_code() {
        return response_code;
    }

    public void setResponse_code(String response_code) {
        this.response_code = response_code;
    }

    public String getKsn_ref_value() {
        return ksn_ref_value;
    }

    public void setKsn_ref_value(String ksn_ref_value) {
        this.ksn_ref_value = ksn_ref_value;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getUpdated_on() {
        return updated_on;
    }

    public void setUpdated_on(String updated_on) {
        this.updated_on = updated_on;
    }

    @Override
    public String toString() {
        return "RkiRequestModel{" +
                "id=" + id +
                ", tid='" + tid + '\'' +
                ", ksn='" + ksn + '\'' +
                ", ipek_value='" + ipek_value + '\'' +
                ", response_code='" + response_code + '\'' +
                ", ksn_ref_value='" + ksn_ref_value + '\'' +
                ", created_on='" + created_on + '\'' +
                ", updated_on='" + updated_on + '\'' +
                '}';
    }
}
