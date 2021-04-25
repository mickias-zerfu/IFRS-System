/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 14 - 12 - 2020
 */

package com.eyob.IFRSSystem.domain;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "transactionmodel")
public class transaction {

    @Id
    @Column
    private String transaction_id;
    @Column
    private Date created;
    @Column
    private Date updated;
    @Column
    private String tx_type;
    @Column
    private Double amount;
    @Column
    private String description;
    @Column
    private String journal_entry_id;
    @Column
    private String account_id;

    public transaction() {
    }

    public transaction(String transaction_id, Date created, Date updated, String tx_type, Double amount, String description, String journal_entry_id, String account_id) {
        this.transaction_id = transaction_id;
        this.created = created;
        this.updated = updated;
        this.tx_type = tx_type;
        this.amount = amount;
        this.description = description;
        this.journal_entry_id = journal_entry_id;
        this.account_id = account_id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
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

    public String getTx_type() {
        return tx_type;
    }

    public void setTx_type(String tx_type) {
        this.tx_type = tx_type;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJournal_entry_id() {
        return journal_entry_id;
    }

    public void setJournal_entry_id(String journal_entry_id) {
        this.journal_entry_id = journal_entry_id;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }
}
