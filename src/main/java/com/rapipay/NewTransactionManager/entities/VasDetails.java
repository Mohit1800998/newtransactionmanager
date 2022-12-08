package com.rapipay.NewTransactionManager.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="vas_detail", schema = "mst")
public class VasDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long posId;
    private String mongoPosId;
    private int program;
    private int subProgram;
    @Column(length = 20)
    private String acquirerMid;
    @Column(length = 20)
    private String acquirerTid;
    @Column(length = 20)
    private String tenure;
    @Column(length = 50)
    private String vpa;
    private short deploymentStatus;
    private String applicationId;
    private short status;
    private Date createdOn;
    private Date updatedOn;
    @Column(length = 50)
    private String createdBy;
    @Column(length = 50)
    private String updatedBy;


    public VasDetails(){

    }

    public VasDetails(Long id, Long posId, String mongoPosId, int program, int subProgram, String acquirerMid, String acquirerTid, String tenure, String vpa, short deploymentStatus, String applicationId, short status, Date createdOn, Date updatedOn, String createdBy, String updatedBy) {
        this.id = id;
        this.posId = posId;
        this.mongoPosId = mongoPosId;
        this.program = program;
        this.subProgram = subProgram;
        this.acquirerMid = acquirerMid;
        this.acquirerTid = acquirerTid;
        this.tenure = tenure;
        this.vpa = vpa;
        this.deploymentStatus = deploymentStatus;
        this.applicationId = applicationId;
        this.status = status;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPosId() {
        return posId;
    }

    public void setPosId(Long posId) {
        this.posId = posId;
    }

    public String getMongoPosId() {
        return mongoPosId;
    }

    public void setMongoPosId(String mongoPosId) {
        this.mongoPosId = mongoPosId;
    }

    public int getProgram() {
        return program;
    }

    public void setProgram(int program) {
        this.program = program;
    }

    public int getSubProgram() {
        return subProgram;
    }

    public void setSubProgram(int subProgram) {
        this.subProgram = subProgram;
    }

    public String getAcquirerMid() {
        return acquirerMid;
    }

    public void setAcquirerMid(String acquirerMid) {
        this.acquirerMid = acquirerMid;
    }

    public String getAcquirerTid() {
        return acquirerTid;
    }

    public void setAcquirerTid(String acquirerTid) {
        this.acquirerTid = acquirerTid;
    }

    public String getTenure() {
        return tenure;
    }

    public void setTenure(String tenure) {
        this.tenure = tenure;
    }

    public String getVpa() {
        return vpa;
    }

    public void setVpa(String vpa) {
        this.vpa = vpa;
    }

    public short getDeploymentStatus() {
        return deploymentStatus;
    }

    public void setDeploymentStatus(short deploymentStatus) {
        this.deploymentStatus = deploymentStatus;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String   toString() {
        return "VasDetails{" +
                "id=" + id +
                ", posId=" + posId +
                ", mongoPosId='" + mongoPosId + '\'' +
                ", program=" + program +
                ", subProgram=" + subProgram +
                ", acquirerMid='" + acquirerMid + '\'' +
                ", acquirerTid='" + acquirerTid + '\'' +
                ", tenure='" + tenure + '\'' +
                ", vpa='" + vpa + '\'' +
                ", deploymentStatus=" + deploymentStatus +
                ", applicationId='" + applicationId + '\'' +
                ", status=" + status +
                ", createdOn=" + createdOn +
                ", updatedOn=" + updatedOn +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}
