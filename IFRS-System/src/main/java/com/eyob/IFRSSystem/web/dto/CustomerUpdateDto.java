/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 22 - 8 - 2020
 */

package com.eyob.IFRSSystem.web.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class CustomerUpdateDto {

    public CustomerUpdateDto() {

    }

    public CustomerUpdateDto(Long customer_id, @NotEmpty String name, @NotEmpty String phone, @Email @NotEmpty String email, @NotEmpty String fax, @NotEmpty String account_no, @NotEmpty String address, @NotEmpty String credit) {
        this.customer_id = customer_id;
        this.name = name;
        Phone = phone;
        this.email = email;
        this.fax = fax;
        this.account_no = account_no;
        this.address = address;
        this.credit = credit;
    }

    private Long customer_id;

    public Long getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Long customer_id) {
        this.customer_id = customer_id;
    }

    @NotEmpty
    private String name;
    @NotEmpty
    private String Phone;
    @Email
    @NotEmpty
    private String email;
    @NotEmpty
    private String fax;
    @NotEmpty
    private String account_no;

    @NotEmpty
    private String address;

    @NotEmpty
    private String credit;

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getAccount_no() {
        return account_no;
    }

    public void setAccount_no(String account_no) {
        this.account_no = account_no;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
