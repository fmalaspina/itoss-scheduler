package com.frsi.itoss.model.ldap;

import com.frsi.itoss.model.baseclasses.CustomValidationException;
import com.frsi.itoss.shared.CryptoService;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler(Ldap.class)
public class LdapPasswordHandler {


    @HandleBeforeCreate
    public Ldap handleLdapCreate(Ldap ldap) {
        try {
            ldap.setPassword(CryptoService.encrypt((String) ldap.getPassword()));
            ldap.setOldPassword(ldap.getPassword());
        } catch (Exception e) {
            throw new CustomValidationException("Unable to encrypt password or null password.");
        }
        return ldap;
    }

    @HandleBeforeSave
    public Ldap handleLdapSave(Ldap ldap) {
        try {
            if (ldap.getOldPassword() != null && !ldap.getOldPassword().equals(ldap.getPassword())) {
                ldap.setPassword(CryptoService.encrypt(ldap.getPassword()));
            }
            ldap.setOldPassword(ldap.getPassword());
        } catch (Exception e) {
            throw new CustomValidationException("Unable to encrypt password or null password.");
        }
        return ldap;
    }
}


