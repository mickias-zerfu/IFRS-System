/*
 * Copyright (c) 2021
 * Created by Eyob Amare on 24 - 1 - 2021
 */

package com.eyob.IFRSSystem.domain;

import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
@Entity
@Table(name = "reviewer")
public class reviewer {

    @Id
    @Column
    private String email;
    @Column
    private Date from_date;
    @Column
    private Date to_date;

    @Column
    private String entity_id;

    @Column
    private Boolean approve;

    public reviewer() {
    }

    public reviewer (String email, Date from_date, Date to_date, String entity_id, Boolean approve){
        this.approve = approve;
        this.email = email;
        this.from_date = from_date;
        this.to_date = to_date;
        this.entity_id = entity_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getFrom_date() {
        return from_date;
    }

    public void setFrom_date(Date from_date) {
        this.from_date = from_date;
    }

    public Date getTo_date() {
        return to_date;
    }

    public void setTo_date(Date to_date) {
        this.to_date = to_date;
    }

    public String getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(String entity_id) {
        this.entity_id = entity_id;
    }

    public Boolean getApprove() {
        return approve;
    }

    public void setApprove(Boolean approve) {
        this.approve = approve;
    }
}
