package com.frsi.itoss.mgr.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ContactPasswordDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String password;
    private boolean changeOnNextLogin;
}
