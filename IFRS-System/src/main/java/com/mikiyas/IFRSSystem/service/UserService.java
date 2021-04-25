/*
 * Copyright (c) 2021
 * Created by Eyob Amare on 25 - 4 - 2021
 */

package com.mikiyas.IFRSSystem.service;

import com.mikiyas.IFRSSystem.domain.User;

import com.mikiyas.IFRSSystem.web.dto.UserRegistrationDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

public interface UserService extends UserDetailsService {

    User findByEmail(String email);

    User save(UserRegistrationDto registration) throws IOException;
}
