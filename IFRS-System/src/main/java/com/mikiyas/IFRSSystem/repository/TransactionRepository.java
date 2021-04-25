/*
 * Copyright (c) 2021
 * Created by Eyob Amare on 25 - 4 - 2021
 */

package com.mikiyas.IFRSSystem.repository;

import com.mikiyas.IFRSSystem.domain.transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends CrudRepository<transaction, String> {

}
