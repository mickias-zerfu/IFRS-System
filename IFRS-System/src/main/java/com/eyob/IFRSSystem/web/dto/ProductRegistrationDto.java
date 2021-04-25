/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 11 - 8 - 2020
 */

package com.eyob.IFRSSystem.web.dto;

import javax.validation.constraints.NotEmpty;

public class ProductRegistrationDto {

    @NotEmpty
    private String name;

    @NotEmpty
    private String unit_price;

    @NotEmpty
    private String tax;

    @NotEmpty
    private String quantity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(String unit_price) {
        this.unit_price = unit_price;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
