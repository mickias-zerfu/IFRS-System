/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 24 - 8 - 2020
 */

package com.eyob.IFRSSystem.repository;

import com.eyob.IFRSSystem.domain.ProductIdentity;
import com.eyob.IFRSSystem.domain.orderProduct;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductRepository extends CrudRepository<orderProduct, ProductIdentity> {
    List<orderProduct> findByProductIdentity_id(Long id);
}
