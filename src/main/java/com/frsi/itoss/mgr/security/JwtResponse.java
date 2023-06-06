package com.frsi.itoss.mgr.security;

import com.frsi.itoss.model.user.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String token;
    private final String refreshToken;
    private final UserAccount userAccount;


}