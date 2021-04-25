/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 30 - 12 - 2020
 */

package com.eyob.IFRSSystem.web.dto;

import javax.validation.constraints.NotEmpty;
import java.sql.Date;

public class InvoiceRegistrationDto {
    @NotEmpty
    private String customer;


    private Date invoice_date;


    private Double amount_due;

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

    public InvoiceRegistrationDto() {
    }

    public InvoiceRegistrationDto(@NotEmpty String customer, @NotEmpty Date invoice_date, @NotEmpty Double amount_due, @NotEmpty String term, @NotEmpty String cash_account, @NotEmpty String receivable_account, @NotEmpty String payable_account, @NotEmpty String earning_account) {
        this.customer = customer;
        this.invoice_date = invoice_date;
        this.amount_due = amount_due;
        this.term = term;
        this.cash_account = cash_account;
        this.receivable_account = receivable_account;
        this.payable_account = payable_account;
        this.earning_account = earning_account;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Date getInvoice_date() {
        return invoice_date;
    }

    public void setInvoice_date(Date invoice_date) {
        this.invoice_date = invoice_date;
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
