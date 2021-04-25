/*
 * Copyright (c) 2021
 * Created by Eyob Amare on 25 - 4 - 2021
 */

package com.mikiyas.IFRSSystem.domain;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "product")
public class product {
    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "unit_price")
    private Double unit_price;

    @Column(name = "tax")
    private Double tax;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "total_sale")
    private Double total_sale;

    public product() {
    }

    public product(String name, Double unit_price, Double tax, Double quantity) {
        this.name = name;
        this.unit_price = unit_price;
        this.tax = tax;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(Double unit_price) {
        this.unit_price = unit_price;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getTotal_sale() {
        return total_sale;
    }

    public void setTotal_sale(Double total_sale) {
        this.total_sale = total_sale;
    }
}
