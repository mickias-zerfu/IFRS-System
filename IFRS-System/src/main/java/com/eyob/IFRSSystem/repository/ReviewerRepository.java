/*
 * Copyright (c) 2021
 * Created by Eyob Amare on 24 - 1 - 2021
 */

package com.eyob.IFRSSystem.repository;

import com.eyob.IFRSSystem.domain.account;
import com.eyob.IFRSSystem.domain.reviewer;
import org.springframework.data.repository.CrudRepository;

public interface ReviewerRepository extends CrudRepository<reviewer, String> {
    reviewer findByEmail(String email);
}
