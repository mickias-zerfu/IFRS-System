/*
 * Copyright (c) 2021
 * Created by Eyob Amare on 25 - 4 - 2021
 */

package com.mikiyas.IFRSSystem.web.dto;

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
