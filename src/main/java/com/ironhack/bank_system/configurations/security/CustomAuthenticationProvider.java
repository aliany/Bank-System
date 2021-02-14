package com.ironhack.bank_system.configurations.security;

import com.ironhack.bank_system.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class CustomAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    @Autowired
    UserService userService;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    }

    @Override
    protected UserDetails retrieveUser(
            String userName,
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
    ) throws AuthenticationException {
        Object hashedKey = usernamePasswordAuthenticationToken.getCredentials();
        return Optional
                .ofNullable(hashedKey)
                .map(String::valueOf)
                .flatMap(userService::findByHashedKey)
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find user with authentication token=" + hashedKey));
    }
}
