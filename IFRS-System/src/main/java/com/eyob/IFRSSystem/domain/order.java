/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 11 - 8 - 2020
 */

package com.eyob.IFRSSystem.domain;
import com.sun.java.browser.plugin2.DOM;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "orders")
public class order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long order_id;

    @Column(name = "customer_id")
    private Long customer_id;


    @Column(name = "freight")
    private Double freight;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "order_date")
    private Date order_date;

    @Column(name = "shipment_date")
    private Date shipment_date;

    public order() {
    }

    public order(Long customer_id, Double freight, Double amount, Date order_date, Date shipment_date) {
        this.customer_id = customer_id;
        this.freight = freight;
        this.amount = amount;
        this.order_date = order_date;
        this.shipment_date = shipment_date;
    }

    public Long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }

    public Long getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Long customer_id) {
        this.customer_id = customer_id;
    }

    public Double getFreight() {
        return freight;
    }

    public void setFreight(Double freight) {
        this.freight = freight;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getOrder_date() {
        return order_date;
    }

    public void setOrder_date(Date order_date) {
        this.order_date = order_date;
    }

    public Date getShipment_date() {
        return shipment_date;
    }

    public void setShipment_date(Date shipment_date) {
        this.shipment_date = shipment_date;
    }

}
