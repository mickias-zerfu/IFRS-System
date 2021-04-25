/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 11 - 8 - 2020
 */

package com.eyob.IFRSSystem.web.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.sql.Date;

public class CustomerRegistrationDto {


    private String customer_id;
    @NotEmpty
    private String address_1;
    private String address_2;
    @NotEmpty
    private String email;
    private String website;
    @NotEmpty
    private String phone;
    @NotEmpty
    private String customer_name;
    @NotEmpty
    private String city;
    @NotEmpty
    private String country;
    @NotEmpty
    private String state;
    @NotEmpty
    private String zip_code;

    public CustomerRegistrationDto() {
    }

    public CustomerRegistrationDto(String customer_id, @NotEmpty String address_1, String address_2, @NotEmpty String email, String website, @NotEmpty String phone, @NotEmpty String customer_name, @NotEmpty String city, @NotEmpty String country, @NotEmpty String state, @NotEmpty String zip_code) {
        this.customer_id = customer_id;
        this.address_1 = address_1;
        this.address_2 = address_2;
        this.email = email;
        this.website = website;
        this.phone = phone;
        this.customer_name = customer_name;
        this.city = city;
        this.country = country;
        this.state = state;
        this.zip_code = zip_code;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getAddress_1() {
        return address_1;
    }

    public void setAddress_1(String address_1) {
        this.address_1 = address_1;
    }

    public String getAddress_2() {
        return address_2;
    }

    public void setAddress_2(String address_2) {
        this.address_2 = address_2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }
}
