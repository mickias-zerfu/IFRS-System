/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 14 - 12 - 2020
 */

package com.eyob.IFRSSystem.repository;

import com.eyob.IFRSSystem.domain.customer;
import com.eyob.IFRSSystem.domain.transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<transaction, String> {

}
