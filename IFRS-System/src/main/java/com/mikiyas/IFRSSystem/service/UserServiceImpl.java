/*
 * Copyright (c) 2021
 * Created by Eyob Amare on 25 - 4 - 2021
 */

package com.mikiyas.IFRSSystem.service;


import com.mikiyas.IFRSSystem.domain.Role;
import com.mikiyas.IFRSSystem.domain.User;
import com.mikiyas.IFRSSystem.domain.reviewer;
import com.mikiyas.IFRSSystem.repository.ReviewerRepository;
import com.mikiyas.IFRSSystem.repository.UserRepository;
import com.mikiyas.IFRSSystem.web.dto.UserRegistrationDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User save(UserRegistrationDto registration) throws IOException {
        
        User user = new User();
        user.setFirstName(registration.getFirstName());
        user.setLastName(registration.getLastName());
        user.setEmail(registration.getEmail());
        user.setPassword(encoder.encode(registration.getPassword()));
        user.setFileName(registration.getImage().getOriginalFilename());
        user.setFileType(registration.getImage().getContentType());
        user.setData(registration.getImage().getBytes());
        user.setRoles(Arrays.asList(new Role(registration.getRole())));
        user.setApproved(false);
        user.setEnabled(false);
        user.setForget(false);
        user.setNotify(true);

        if(registration.getRole().contains("REVIEWER")){

            reviewer reviewer = new reviewer();
            reviewer.setEmail(registration.getEmail());
            reviewer.setApprove(false);
            reviewerRepository.save(reviewer);
        }

        return userRepository.save(user);
    }

    @Autowired
    ReviewerRepository reviewerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null){
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles){
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
