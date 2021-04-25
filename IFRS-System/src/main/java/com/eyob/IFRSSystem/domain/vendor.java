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
@Table(name = "vendormodel")
public class vendor {
    @Id
    @Column
    private String vendor_id;
    @Column
    private Date created;
    @Column
    private Date updated;
    @Column
    private String address_1;
    @Column
    private String address_2;
    @Column
    private String email;
    @Column
    private String website;
    @Column
    private String phone;
    @Column
    private String vendor_name;
    @Column
    private String description;
    @Column
    private boolean active;
    @Column
    private boolean hidden;
    @Column
    private String additional_info;
    @Column
    private String entity_id;
    @Column
    private String city;
    @Column
    private String country;
    @Column
    private String state;
    @Column
    private String zip_code;

    public vendor() {
    }

    public vendor(String vendor_id, Date created, Date updated, String address_1, String address_2, String email, String website, String phone, String vendor_name, String description, boolean active, boolean hidden, String additional_info, String entity_id, String city, String country, String state, String zip_code) {
        this.vendor_id = vendor_id;
        this.created = created;
        this.updated = updated;
        this.address_1 = address_1;
        this.address_2 = address_2;
        this.email = email;
        this.website = website;
        this.phone = phone;
        this.vendor_name = vendor_name;
        this.description = description;
        this.active = active;
        this.hidden = hidden;
        this.additional_info = additional_info;
        this.entity_id = entity_id;
        this.city = city;
        this.country = country;
        this.state = state;
        this.zip_code = zip_code;
    }

    public String getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(String vendor_id) {
        this.vendor_id = vendor_id;
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

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getAdditional_info() {
        return additional_info;
    }

    public void setAdditional_info(String additional_info) {
        this.additional_info = additional_info;
    }

    public String getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(String entity_id) {
        this.entity_id = entity_id;
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
