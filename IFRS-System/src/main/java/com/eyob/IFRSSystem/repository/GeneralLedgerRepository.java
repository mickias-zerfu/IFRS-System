/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 14 - 12 - 2020
 */

package com.eyob.IFRSSystem.repository;

import com.eyob.IFRSSystem.domain.GeneralLedger;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeneralLedgerRepository extends CrudRepository<GeneralLedger, Long> {
    Optional<GeneralLedger> findByAccount(String account);
    Optional<GeneralLedger> findById (Long id);
}
