package org.sang.bean;

import java.math.BigDecimal;
import java.util.Date;

public class Salary {
    private Integer id;
    private BigDecimal bonus;
    private BigDecimal lunchSalary;
    private BigDecimal jobSalary;
    private BigDecimal knowledgeSalary;
    private BigDecimal trafficSalary;
    private BigDecimal basicSalary;
    private BigDecimal allSalary;
    private BigDecimal pensionBase;
    private BigDecimal pensionPer;
    private Date createDate;
    private BigDecimal medicalBase;
    private BigDecimal medicalPer;
    private BigDecimal accumulationFundBase;
    private BigDecimal accumulationFundPer;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getBonus() {
        return bonus;
    }

    public void setBonus(BigDecimal bonus) {
        this.bonus = bonus;
    }

    public BigDecimal getLunchSalary() {
        return lunchSalary;
    }

    public void setLunchSalary(BigDecimal lunchSalary) {
        this.lunchSalary = lunchSalary;
    }

    public BigDecimal getTrafficSalary() {
        return trafficSalary;
    }

    public void setTrafficSalary(BigDecimal trafficSalary) {
        this.trafficSalary = trafficSalary;
    }

    public BigDecimal getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(BigDecimal basicSalary) {
        this.basicSalary = basicSalary;
    }

    public BigDecimal getAllSalary() {
        return allSalary;
    }

    public void setAllSalary(BigDecimal allSalary) {
        this.allSalary = allSalary;
    }

    public BigDecimal getPensionBase() {
        return pensionBase;
    }

    public void setPensionBase(BigDecimal pensionBase) {
        this.pensionBase = pensionBase;
    }

    public BigDecimal getPensionPer() {
        return pensionPer;
    }

    public void setPensionPer(BigDecimal pensionPer) {
        this.pensionPer = pensionPer;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public BigDecimal getMedicalBase() {
        return medicalBase;
    }

    public void setMedicalBase(BigDecimal medicalBase) {
        this.medicalBase = medicalBase;
    }

    public BigDecimal getMedicalPer() {
        return medicalPer;
    }

    public void setMedicalPer(BigDecimal medicalPer) {
        this.medicalPer = medicalPer;
    }

    public BigDecimal getAccumulationFundBase() {
        return accumulationFundBase;
    }

    public void setAccumulationFundBase(BigDecimal accumulationFundBase) {
        this.accumulationFundBase = accumulationFundBase;
    }

    public BigDecimal getAccumulationFundPer() {
        return accumulationFundPer;
    }

    public void setAccumulationFundPer(BigDecimal accumulationFundPer) {
        this.accumulationFundPer = accumulationFundPer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getJobSalary() {
        return jobSalary;
    }

    public void setJobSalary(BigDecimal jobSalary) {
        this.jobSalary = jobSalary;
    }

    public BigDecimal getKnowledgeSalary() {
        return knowledgeSalary;
    }

    public void setKnowledgeSalary(BigDecimal knowledgeSalary) {
        this.knowledgeSalary = knowledgeSalary;
    }
}

