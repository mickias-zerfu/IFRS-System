/*
 * Copyright (c) 2021
 * Created by Eyob Amare on 25 - 4 - 2021
 */

package com.mikiyas.IFRSSystem.repository;

import com.mikiyas.IFRSSystem.domain.ProductIdentity;
import com.mikiyas.IFRSSystem.domain.orderProduct;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductRepository extends CrudRepository<orderProduct, ProductIdentity> {
    List<orderProduct> findByProductIdentity_id(Long id);
}
