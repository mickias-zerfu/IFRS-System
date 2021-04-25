/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 30 - 12 - 2020
 */

package com.eyob.IFRSSystem.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "invoicemodel")
public class invoice {
    @Id
    @Column
    private String invoice_id;
    @Column
    private Date created;
    @Column
    private Date updated;
    @Column
    private String terms;
    @Column
    private Double amount_due;
    @Column
    private Double amount_paid;
    @Column
    private Double amount_receivable;
    @Column
    private Double amount_unearned;
    @Column
    private Double amount_earned;
    @Column
    private boolean paid;
    private Date paid_date;
    @Column
    private Date date;
    @Column
    private Date due_date;
    @Column
    private boolean Void;
    @Column
    private Date void_date;
    @Column
    private boolean progressible;
    @Column
    private Double progress;
    @Column
    private String invoice_number;
    @Column
    private String cash_account_id;
    @Column
    private String earnings_account_id;
    @Column
    private String ledger_id;
    @Column
    private String payable_account_id;
    @Column
    private String receivable_account_id;
    @Column
    private String customer_id;

    public invoice() {
    }

    public invoice(String invoice_id, Date created, Date updated, String terms, Double amount_due, Double amount_paid, Double amount_receivable, Double amount_unearned, Double amount_earned, boolean paid, Date paid_date, Date date, Date due_date, boolean aVoid, Date void_date, boolean progressible, Double progress, String invoice_number, String cash_account_id, String earnings_account_id, String ledger_id, String payable_account_id, String receivable_account_id, String customer_id) {
        this.invoice_id = invoice_id;
        this.created = created;
        this.updated = updated;
        this.terms = terms;
        this.amount_due = amount_due;
        this.amount_paid = amount_paid;
        this.amount_receivable = amount_receivable;
        this.amount_unearned = amount_unearned;
        this.amount_earned = amount_earned;
        this.paid = paid;
        this.paid_date = paid_date;
        this.date = date;
        this.due_date = due_date;
        Void = aVoid;
        this.void_date = void_date;
        this.progressible = progressible;
        this.progress = progress;
        this.invoice_number = invoice_number;
        this.cash_account_id = cash_account_id;
        this.earnings_account_id = earnings_account_id;
        this.ledger_id = ledger_id;
        this.payable_account_id = payable_account_id;
        this.receivable_account_id = receivable_account_id;
        this.customer_id = customer_id;
    }

    public String getInvoice_id() {
        return invoice_id;
    }

    public void setInvoice_id(String invoice_id) {
        this.invoice_id = invoice_id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public Double getAmount_due() {
        return amount_due;
    }

    public void setAmount_due(Double amount_due) {
        this.amount_due = amount_due;
    }

    public Double getAmount_paid() {
        return amount_paid;
    }

    public void setAmount_paid(Double amount_paid) {
        this.amount_paid = amount_paid;
    }

    public Double getAmount_receivable() {
        return amount_receivable;
    }

    public void setAmount_receivable(Double amount_receivable) {
        this.amount_receivable = amount_receivable;
    }

    public Double getAmount_unearned() {
        return amount_unearned;
    }

    public void setAmount_unearned(Double amount_unearned) {
        this.amount_unearned = amount_unearned;
    }

    public Double getAmount_earned() {
        return amount_earned;
    }

    public void setAmount_earned(Double amount_earned) {
        this.amount_earned = amount_earned;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public Date getPaid_date() {
        return paid_date;
    }

    public void setPaid_date(Date paid_date) {
        this.paid_date = paid_date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDue_date() {
        return due_date;
    }

    public void setDue_date(Date due_date) {
        this.due_date = due_date;
    }

    public boolean isVoid() {
        return Void;
    }

    public void setVoid(boolean aVoid) {
        Void = aVoid;
    }

    public Date getVoid_date() {
        return void_date;
    }

    public void setVoid_date(Date void_date) {
        this.void_date = void_date;
    }

    public boolean isProgressible() {
        return progressible;
    }

    public void setProgressible(boolean progressible) {
        this.progressible = progressible;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public String getInvoice_number() {
        return invoice_number;
    }

    public void setInvoice_number(String invoice_number) {
        this.invoice_number = invoice_number;
    }

    public String getCash_account_id() {
        return cash_account_id;
    }

    public void setCash_account_id(String cash_account_id) {
        this.cash_account_id = cash_account_id;
    }

    public String getEarnings_account_id() {
        return earnings_account_id;
    }

    public void setEarnings_account_id(String earnings_account_id) {
        this.earnings_account_id = earnings_account_id;
    }

    public String getLedger_id() {
        return ledger_id;
    }

    public void setLedger_id(String ledger_id) {
        this.ledger_id = ledger_id;
    }

    public String getPayable_account_id() {
        return payable_account_id;
    }

    public void setPayable_account_id(String payable_account_id) {
        this.payable_account_id = payable_account_id;
    }

    public String getReceivable_account_id() {
        return receivable_account_id;
    }

    public void setReceivable_account_id(String receivable_account_id) {
        this.receivable_account_id = receivable_account_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }
}
