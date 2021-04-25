/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 11 - 8 - 2020
 */

package com.eyob.IFRSSystem.service;

import com.eyob.IFRSSystem.domain.User;

import com.eyob.IFRSSystem.web.dto.UserRegistrationDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

public interface UserService extends UserDetailsService {

    User findByEmail(String email);

    User save(UserRegistrationDto registration) throws IOException;
}
