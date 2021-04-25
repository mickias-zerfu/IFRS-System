/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 30 - 12 - 2020  
 */

package com.eyob.IFRSSystem.web.dto;

import javax.validation.constraints.NotEmpty;
import java.sql.Date;

public class BillRegistrationDto {
    @NotEmpty
    private String vendor;

    @NotEmpty
    private String xrn;


    private Date bill_date;


    private Double amount_due;

    private Double amount_paid;

    @NotEmpty
    private String term;

    @NotEmpty
    private String cash_account;

    @NotEmpty
    private String receivable_account;

    @NotEmpty
    private String payable_account;

    @NotEmpty
    private String earning_account;

    public BillRegistrationDto() {
    }

    public BillRegistrationDto(@NotEmpty String vendor, @NotEmpty String xrn, Date bill_date, Double amount_due, Double amount_paid, @NotEmpty String term, @NotEmpty String cash_account, @NotEmpty String receivable_account, @NotEmpty String payable_account, @NotEmpty String earning_account) {
        this.vendor = vendor;
        this.xrn = xrn;
        this.bill_date = bill_date;
        this.amount_due = amount_due;
        this.amount_paid = amount_paid;
        this.term = term;
        this.cash_account = cash_account;
        this.receivable_account = receivable_account;
        this.payable_account = payable_account;
        this.earning_account = earning_account;
    }

    public Double getAmount_paid() {
        return amount_paid;
    }

    public void setAmount_paid(Double amount_paid) {
        this.amount_paid = amount_paid;
    }

    public String getCustomer() {
        return vendor;
    }

    public void setCustomer(String vendor) {
        this.vendor = vendor;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getXrn() {
        return xrn;
    }

    public void setXrn(String xrn) {
        this.xrn = xrn;
    }

    public Date getBill_date() {
        return bill_date;
    }

    public void setBill_date(Date bill_date) {
        this.bill_date = bill_date;
    }

    public Date getInvoice_date() {
        return bill_date;
    }

    public void setInvoice_date(Date bill_date) {
        this.bill_date = bill_date;
    }

    public Double getAmount_due() {
        return amount_due;
    }

    public void setAmount_due(Double amount_due) {
        this.amount_due = amount_due;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getCash_account() {
        return cash_account;
    }

    public void setCash_account(String cash_account) {
        this.cash_account = cash_account;
    }

    public String getReceivable_account() {
        return receivable_account;
    }

    public void setReceivable_account(String receivable_account) {
        this.receivable_account = receivable_account;
    }

    public String getPayable_account() {
        return payable_account;
    }

    public void setPayable_account(String payable_account) {
        this.payable_account = payable_account;
    }

    public String getEarning_account() {
        return earning_account;
    }

    public void setEarning_account(String earning_account) {
        this.earning_account = earning_account;
    }
}
