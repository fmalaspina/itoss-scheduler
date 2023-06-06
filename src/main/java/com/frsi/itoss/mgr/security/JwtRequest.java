package com.frsi.itoss.mgr.security;

import java.io.Serializable;

public class JwtRequest implements Serializable {

    private static final long serialVersionUID = 5926468583005150707L;

    private String username;
    private String password;

    private Long ldapId;

    public JwtRequest(String username, String password, Long ldapId) {
        this.setUsername(username);
        this.setPassword(password);
        this.ldapId = ldapId;
    }

    public Long getLdapId() {
        return ldapId;
    }

    //need default constructor for JSON Parsing
    public JwtRequest() {

    }

    public void setLdapId(Long ldapId) {
        this.ldapId = ldapId;
    }

    public JwtRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}