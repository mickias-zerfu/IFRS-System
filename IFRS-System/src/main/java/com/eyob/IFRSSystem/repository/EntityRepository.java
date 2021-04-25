/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 29 - 12 - 2020
 */

package com.eyob.IFRSSystem.repository;

import com.eyob.IFRSSystem.domain.entity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface EntityRepository extends CrudRepository<entity, String> {

}
