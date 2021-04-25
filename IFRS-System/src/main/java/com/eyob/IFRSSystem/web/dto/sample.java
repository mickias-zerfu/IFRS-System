/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 26 - 8 - 2020
 */

package com.eyob.IFRSSystem.web.dto;

import com.eyob.IFRSSystem.domain.orderProduct;
import com.eyob.IFRSSystem.domain.product;

import javax.validation.constraints.NotEmpty;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class sample {
    String name;
    Double quantity;

    public sample() {
    }

    public sample(String name, Double quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        sample sample = (sample) o;
        return Objects.equals(name, sample.name) &&
                Objects.equals(quantity, sample.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, quantity);
    }

    @Override
    public String toString() {
        return "sample{" +
                "name='" + name + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
