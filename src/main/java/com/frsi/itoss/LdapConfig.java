package com.frsi.itoss;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import java.util.List;

@Configuration
@ConditionalOnProperty(name = "spring.ldap.urls")

public class LdapConfig {


    @Value("${spring.ldap.urls:ldap://localhost:389}")
    List<String> ldapUrls;
    @Value("${spring.ldap.base:dc=}")
    String ldapBase;

    @Value("${spring.ldap.username}")
    String ldapUser;

    @Value("${spring.ldap.password}")
    String ldapPassword;


    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();

        if (ldapUrls != null) {
            String[] ldapUrlsStr = ldapUrls.toArray(new String[0]);
            contextSource.setUrls(ldapUrlsStr);
        }

        contextSource.setBase(ldapBase);
        contextSource.setUserDn(ldapUser);
        contextSource.setPassword(ldapPassword);


        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
        LdapTemplate ldapTemplate = new LdapTemplate(contextSource());
        ldapTemplate.setDefaultTimeLimit(3000);

        return new LdapTemplate(contextSource());
    }
}
