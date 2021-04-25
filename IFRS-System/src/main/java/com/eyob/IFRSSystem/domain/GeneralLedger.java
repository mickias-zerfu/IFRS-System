/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 14 - 12 - 2020
 */

package com.eyob.IFRSSystem.domain;

import javax.persistence.*;

@Entity
@Table(name = "general_ledger")
public class GeneralLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "account")
    private String account;

    @Column(name = "account_type")
    private int account_type;

    @Column(name = "account_description")
    private String account_description;

    @Column(name = "current_amount")
    private double current_amount;

    public GeneralLedger() {
    }

    public GeneralLedger(String account, int account_type, String account_description, double current_amount) {
        this.account = account;
        this.account_type = account_type;
        this.account_description = account_description;
        this.current_amount = current_amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getAccount_type() {
        return account_type;
    }

    public void setAccount_type(int account_type) {
        this.account_type = account_type;
    }

    public String getAccount_description() {
        return account_description;
    }

    public void setAccount_description(String account_description) {
        this.account_description = account_description;
    }

    public double getCurrent_amount() {
        return current_amount;
    }

    public void setCurrent_amount(double current_amount) {
        this.current_amount = current_amount;
    }
}
