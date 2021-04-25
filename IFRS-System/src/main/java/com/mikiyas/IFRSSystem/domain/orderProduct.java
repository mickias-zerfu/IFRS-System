/*
 * Copyright (c) 2021
 * Created by Eyob Amare on 25 - 4 - 2021
 */

package com.mikiyas.IFRSSystem.domain;

import javax.persistence.*;
@Entity
@Table(name = "order_product")
public class orderProduct {

    @EmbeddedId
    private ProductIdentity productIdentity;
    @Column(name = "quantity")
    private Double quantity;

    private Double unit_price;
    private Double tax;
    private Double amount;

    public orderProduct() {
    }

    public orderProduct(ProductIdentity productIdentity, Double quantity) {
        this.productIdentity = productIdentity;
        this.quantity = quantity;
    }

    public ProductIdentity getProductIdentity() {
        return productIdentity;
    }

    public void setProductIdentity(ProductIdentity productIdentity) {
        this.productIdentity = productIdentity;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
