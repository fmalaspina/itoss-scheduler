package com.frsi.itoss.mgr.security;

import com.frsi.itoss.model.repository.UserAccountRepo;
import com.frsi.itoss.model.user.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade implements IAuthenticationFacade {

    @Autowired
    UserAccountRepo userAccountRepo;
    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public UserAccount getUserAccount() {

        var auth = getAuthentication();
        if (auth == null) return null;
        var user = (User) auth.getPrincipal();
        return userAccountRepo.findByUsername(user.getUsername());
    }
}