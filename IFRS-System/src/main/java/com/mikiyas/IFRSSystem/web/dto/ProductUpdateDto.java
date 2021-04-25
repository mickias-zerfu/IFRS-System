/*
 * Copyright (c) 2021
 * Created by Eyob Amare on 25 - 4 - 2021
 */

package com.mikiyas.IFRSSystem.web.dto;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;

public class ProductUpdateDto {
    @NotEmpty
    private String name;

    @NotEmpty
    private Double unit_price;

    @NotEmpty
    private Double tax;

    @NotEmpty
    private Double quantity;

    public ProductUpdateDto(@NotEmpty String name, @NotEmpty Double unit_price, @NotEmpty Double tax, @NotEmpty Double quantity) {
        this.name = name;
        this.unit_price = unit_price;
        this.tax = tax;
        this.quantity = quantity;
    }

    public ProductUpdateDto() {
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
}
