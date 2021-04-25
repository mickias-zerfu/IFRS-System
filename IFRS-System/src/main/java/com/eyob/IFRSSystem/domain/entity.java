/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 29 - 12 - 2020
 */

package com.eyob.IFRSSystem.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "entitymodel")
public class entity {
    @Id
    @Column
    private String entity_id;
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
    private String name;
    @Column
    private Boolean hidden;
    @Column
    private Long admin_id;
    @Column
    private String city;
    @Column
    private String country;
    @Column
    private String state;
    @Column
    private String zip_code;
    @Column
    private String parent_id;

    public entity() {
    }

    public entity(String entity_id, Date created, Date updated, String address_1, String address_2, String email, String website, String phone, String name, Boolean hidden, Long admin_id, String city, String country, String state, String zip_code, String parent_id) {
        this.entity_id = entity_id;
        this.created = created;
        this.updated = updated;
        this.address_1 = address_1;
        this.address_2 = address_2;
        this.email = email;
        this.website = website;
        this.phone = phone;
        this.name = name;
        this.hidden = hidden;
        this.admin_id = admin_id;
        this.city = city;
        this.country = country;
        this.state = state;
        this.zip_code = zip_code;
        this.parent_id = parent_id;
    }

    public String getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(String entity_id) {
        this.entity_id = entity_id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Long getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(Long admin_id) {
        this.admin_id = admin_id;
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

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }
}
