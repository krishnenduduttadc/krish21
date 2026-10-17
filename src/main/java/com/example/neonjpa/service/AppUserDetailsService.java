package com.example.neonjpa.service;

import com.example.neonjpa.entity.AppUser;
import com.example.neonjpa.repository.AppUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository userRepository;

    public AppUserDetailsService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        AppUser user = userRepository.findByEmailIgnoreCase(email.trim())
            .orElseThrow(() ->
                new UsernameNotFoundException("Invalid email or password")
            );

        return User.withUsername(user.getEmail())
            .password(user.getPasswordHash())
            .roles("USER")
            .build();
    }
}
