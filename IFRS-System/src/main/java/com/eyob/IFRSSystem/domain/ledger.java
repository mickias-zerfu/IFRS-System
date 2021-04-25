/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 30 - 12 - 2020
 */

package com.eyob.IFRSSystem.domain;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "ledgermodel")
public class ledger {
    @Id
    @Column
    private String ledger_id;
    @Column
    private Date created;
    @Column
    private Date updated;
    @Column
    private String name;
    @Column
    private boolean posted;
    @Column
    private boolean locked;
    @Column
    private String entity_id;

    public ledger() {
    }

    public ledger(String ledger_id, Date created, Date updated, String name, boolean posted, boolean locked, String entity_id) {
        this.ledger_id = ledger_id;
        this.created = created;
        this.updated = updated;
        this.name = name;
        this.posted = posted;
        this.locked = locked;
        this.entity_id = entity_id;
    }

    public String getLedger_id() {
        return ledger_id;
    }

    public void setLedger_id(String ledger_id) {
        this.ledger_id = ledger_id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(String entity_id) {
        this.entity_id = entity_id;
    }
}
