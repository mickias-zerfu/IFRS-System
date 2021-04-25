/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 15 - 12 - 2020
 */

package com.eyob.IFRSSystem.web.dto;

import javax.validation.constraints.NotEmpty;

public class BankAccountRegistrationDto {

    @NotEmpty
    private String account;

    @NotEmpty
    private int account_type;

    @NotEmpty
    private String account_description;

    @NotEmpty
    private Double begining_balance;

    public BankAccountRegistrationDto() {
    }

    public BankAccountRegistrationDto(@NotEmpty String account, @NotEmpty int account_type, @NotEmpty String account_description, @NotEmpty Double begining_balance) {
        this.account = account;
        this.account_type = account_type;
        this.account_description = account_description;
        this.begining_balance = begining_balance;
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

    public Double getBegining_balance() {
        return begining_balance;
    }

    public void setBegining_balance(Double begining_balance) {
        this.begining_balance = begining_balance;
    }
}
