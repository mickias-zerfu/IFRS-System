/*
 * Copyright (c) 2021
 * Created by Eyob Amare on 25 - 4 - 2021
 */

package com.mikiyas.IFRSSystem.web.dto;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

public class OrderUpdateDto {
    @NotEmpty
    String customer_name;
    @NotEmpty
    Double totalAmount;
    @NotEmpty
    Double freight;
    @NotEmpty
    Date orderDate;
    @NotEmpty
    Date shipmentDate;

    public OrderUpdateDto() {
    }

    public OrderUpdateDto(String customer_name, Double totalAmount, Double freight, Date orderDate, Date shipmentDate) {
        this.customer_name = customer_name;
        this.totalAmount = totalAmount;
        this.freight = freight;
        this.orderDate = orderDate;
        this.shipmentDate = shipmentDate;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getFreight() {
        return freight;
    }

    public void setFreight(Double freight) {
        this.freight = freight;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(Date shipmentDate) {
        this.shipmentDate = shipmentDate;
    }
}
