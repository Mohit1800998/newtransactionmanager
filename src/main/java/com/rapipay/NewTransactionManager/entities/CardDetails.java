package com.rapipay.NewTransactionManager.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="hashed_card_details", schema = "pos")
public class CardDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String rrn;
    private String hashed_data;

    public CardDetails(){

    }

    public CardDetails(Long id, String rrn, String hashed_data) {
        this.id = id;
        this.rrn = rrn;
        this.hashed_data = hashed_data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getHashed_data() {
        return hashed_data;
    }

    public void setHashed_data(String hashed_data) {
        this.hashed_data = hashed_data;
    }

    @Override
    public String toString() {
        return "CardDetails{" +
                "id=" + id +
                ", rrn='" + rrn + '\'' +
                ", hashed_data='" + hashed_data + '\'' +
                '}';
    }
}
