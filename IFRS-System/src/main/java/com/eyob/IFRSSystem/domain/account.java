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
@Table(name = "accountmodel")
public class account {
    @Id
    @Column
    private String account_id;
    @Column
    private Date created;
    @Column
    private Date updated;
    @Column
    private String code;
    @Column
    private String name;
    @Column
    private String role;
    @Column
    private String balance_type;
    @Column
    private boolean locked;
    @Column
    private boolean active;
    @Column
    private String parent_id;

    public account() {
    }

    public account(String account_id, Date created, Date updated, String code, String name, String role, String balance_type, boolean locked, boolean active, String parent_id) {
        this.account_id = account_id;
        this.created = created;
        this.updated = updated;
        this.code = code;
        this.name = name;
        this.role = role;
        this.balance_type = balance_type;
        this.locked = locked;
        this.active = active;
        this.parent_id = parent_id;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBalance_type() {
        return balance_type;
    }

    public void setBalance_type(String balance_type) {
        this.balance_type = balance_type;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }
}
