/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 16 - 12 - 2020
 */

package com.eyob.IFRSSystem.web.dto;

import javax.validation.constraints.NotEmpty;

public class AccountUpdateDto {
    @NotEmpty
    private String account;

    @NotEmpty
    private int account_type;

    @NotEmpty
    private String account_description;

    @NotEmpty
    private Double current_amount;

    public AccountUpdateDto() {
    }

    public AccountUpdateDto(@NotEmpty String account, @NotEmpty int account_type, @NotEmpty String account_description, @NotEmpty Double current_amount) {
        this.account = account;
        this.account_type = account_type;
        this.account_description = account_description;
        this.current_amount = current_amount;
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

    public Double getCurrent_amount() {
        return current_amount;
    }

    public void setCurrent_amount(Double current_amount) {
        this.current_amount = current_amount;
    }
}
