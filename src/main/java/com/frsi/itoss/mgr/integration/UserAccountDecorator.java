package com.frsi.itoss.mgr.integration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.frsi.itoss.model.company.Company;
import com.frsi.itoss.model.user.UserAccount;
import com.frsi.itoss.model.user.UserStatus;
import com.frsi.itoss.shared.Attribute;

import java.io.Serializable;
import java.util.List;

public class UserAccountDecorator implements Serializable {

    @JsonIgnore
    UserAccount userAccount;

    public UserAccountDecorator(UserAccount userAccount) {
        this.userAccount = userAccount;

    }

    public List<Attribute> getAttributes() {
        return userAccount.getAttributes();
    }

    public String getKey() {
        return userAccount.getKey();
    }

    public UserAccount getManager() {
        return userAccount.getManager();
    }

    public String getUsername() {
        return userAccount.getUsername();
    }

    public String getType() {
        return userAccount.getType().getName();
    }

    public Long getId() {
        return userAccount.getId();
    }

    public String getName() {
        return userAccount.getName();
    }

    public Company getCompany() {
        return userAccount.getCompany();
    }

    public Boolean getLdapAuthentication() {
        return userAccount.getLdapAuthentication();
    }

    public String getMobile() {
        return userAccount.getMobile();
    }

    public boolean isChangeOnNextLogin() {
        return userAccount.isChangeOnNextLogin();
    }

    public String getEmail() {
        return userAccount.getEmail();
    }

    public UserStatus getStatus() {
        return userAccount.getStatus();
    }
}
