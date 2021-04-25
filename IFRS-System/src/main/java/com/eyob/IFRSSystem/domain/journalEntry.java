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
@Table(name = "journalentrymodel")
public class journalEntry {
    @Id
    @Column
    private String je_id;
    @Column
    private Date date;
    @Column
    private String description;
    @Column
    private String activity;
    @Column
    private String origin;
    @Column
    private boolean posted;
    @Column
    private boolean locked;
    @Column
    private Date created;
    @Column
    private Date updated;
    @Column
    private String ledger_id;
    @Column
    private String parent_id;

    public journalEntry() {
    }

    public journalEntry(String je_id, Date date, String description, String activity, String origin, boolean posted, boolean locked, Date created, Date updated, String ledger_id, String parent_id) {
        this.je_id = je_id;
        this.date = date;
        this.description = description;
        this.activity = activity;
        this.origin = origin;
        this.posted = posted;
        this.locked = locked;
        this.created = created;
        this.updated = updated;
        this.ledger_id = ledger_id;
        this.parent_id = parent_id;
    }

    public String getJe_id() {
        return je_id;
    }

    public void setJe_id(String je_id) {
        this.je_id = je_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public boolean isPosted() {
        return posted;
    }

    public void setPosted(boolean posted) {
        this.posted = posted;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
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

    public String getLedger_id() {
        return ledger_id;
    }

    public void setLedger_id(String ledger_id) {
        this.ledger_id = ledger_id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }
}
