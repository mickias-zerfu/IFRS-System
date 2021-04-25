/*
 * Copyright (c) 2021
 * Created by Eyob Amare on 25 - 4 - 2021
 */

package com.mikiyas.IFRSSystem.repository;

import com.mikiyas.IFRSSystem.domain.GeneralLedger;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeneralLedgerRepository extends CrudRepository<GeneralLedger, Long> {
    Optional<GeneralLedger> findByAccount(String account);
    Optional<GeneralLedger> findById (Long id);
}
