/*
 * Copyright (c) 2021
 * Created by Eyob Amare on 25 - 4 - 2021
 */

package com.mikiyas.IFRSSystem.web.dto;

import com.mikiyas.IFRSSystem.domain.orderProduct;

import java.sql.Date;
import java.util.List;

public class OrderRegistrationDto {

    private Long customer1;


    private Date order_date1;


    private Date shipment_date1;


    private List<orderProduct> ordersList1;


    private Double freight1;

    public OrderRegistrationDto() {
    }

    public OrderRegistrationDto(Long customer1, Date order_date1, Date shipment_date1, List<orderProduct> ordersList1, Double freight1) {
        this.customer1 = customer1;
        this.order_date1 = order_date1;
        this.shipment_date1 = shipment_date1;
        this.ordersList1 = ordersList1;
        this.freight1 = freight1;
    }

    public Long getCustomer1() {
        return customer1;
    }

    public void setCustomer1(Long customer1) {
        this.customer1 = customer1;
    }

    public Date getOrder_date1() {
        return order_date1;
    }

    public void setOrder_date1(Date order_date1) {
        this.order_date1 = order_date1;
    }

    public Date getShipment_date1() {
        return shipment_date1;
    }

    public void setShipment_date1(Date shipment_date1) {
        this.shipment_date1 = shipment_date1;
    }

    public List<orderProduct> getOrdersList1() {
        return ordersList1;
    }

    public void setOrdersList1(List<orderProduct> ordersList1) {
        this.ordersList1 = ordersList1;
    }

    public Double getFreight1() {
        return freight1;
    }

    public void setFreight1(Double freight1) {
        this.freight1 = freight1;
    }
}
