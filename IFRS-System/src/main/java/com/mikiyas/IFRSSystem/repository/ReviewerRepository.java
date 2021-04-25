/*
 * Copyright (c) 2021
 * Created by Eyob Amare on 25 - 4 - 2021
 */

package com.mikiyas.IFRSSystem.repository;

import com.mikiyas.IFRSSystem.domain.reviewer;
import org.springframework.data.repository.CrudRepository;

public interface ReviewerRepository extends CrudRepository<reviewer, String> {
    reviewer findByEmail(String email);
}
