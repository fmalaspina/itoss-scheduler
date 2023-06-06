package com.frsi.itoss.mgr.security;

import com.frsi.itoss.model.repository.LdapRepo;
import com.frsi.itoss.model.user.UserAccount;
import com.frsi.itoss.model.user.UserDTO;
import com.frsi.itoss.shared.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.naming.directory.Attributes;

import static org.springframework.ldap.query.LdapQueryBuilder.query;


@RestController
@CrossOrigin(origins = "*")
public class JwtAuthenticationController {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    LdapRepo ldapRepo;
    @Autowired
    LdapTemplate ldapTemplate;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @RequestMapping(value = "/authenticate/ldaps", method = RequestMethod.GET)
    public ResponseEntity<?> getLdaps() throws Exception {
        return ResponseEntity.ok().body(ldapRepo.findByOrderByName());
    }


    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

//       try {
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final UserAccount userAccount = authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword(),
                (authenticationRequest.getLdapId() == null ? -1L : authenticationRequest.getLdapId()));


        //final UserAccount userAccount = userDetailsService.getUserAccount(authenticationRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userAccount.getId(),userDetails);

        final String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token, refreshToken, userAccount));

    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> saveUser(@RequestBody UserDTO user) throws Exception {
        return ResponseEntity.ok(userDetailsService.save(user));
    }

    @RequestMapping(value = "/userAccounts/{id}/password", method = RequestMethod.POST)
    public ResponseEntity<?> savePassword(@PathVariable("id") Long id, @RequestBody UserPasswordDTO user) throws Exception {
        return ResponseEntity.ok(userDetailsService.savePassword(id, user));
    }

    @RequestMapping(value = "/contacts/{id}/password", method = RequestMethod.POST)
    public ResponseEntity<?> savePassword(@PathVariable("id") Long id, @RequestBody ContactPasswordDTO user) throws Exception {
        return ResponseEntity.ok(userDetailsService.savePassword(id, user));
    }

    private UserAccount authenticate(String username, String password, Long ldapId) throws Exception {
        //try {

        final UserAccount userAccount = userDetailsService.getUserAccount(username);
        if (ldapId == null) {
            ldapId = -1L;
        }
        if ((ldapId == null || ldapId == 0) && userAccount.getLdapAuthentication() != null && userAccount.getLdapAuthentication()) {
            throw new BadCredentialsException("User is configured for ldap authentication.");
        }
        if (userAccount.getLdapAuthentication() != null && userAccount.getLdapAuthentication()) {
            //if (ldapAuthenticate(getDnForUser(username),password))
            //if (ldapTemplate.authenticate)
            if (ldapId == -1L) {

                if (ldapAuthenticate(username, password)) {
                    password = "itoss";

                } else {
                    throw new BadCredentialsException("Bad credentials.");
                }
            } else {
                if (ldapAuthenticate(username, password, ldapId)) {
                    password = "itoss";

                } else {
                    throw new BadCredentialsException("Bad credentials.");
                }
            }
        }


        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		/*} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}*/
        return userAccount;
    }

    private boolean ldapAuthenticate(String user, String password) throws Exception {

        LdapQuery query = query()
                //.base("dc=261consulting,dc=com")

                .attributes("sAMAccountName")
                .where("sAMAccountName").is(user);


        var result = ldapTemplate.search(query,
                new AttributesMapper<String>() {
                    public String mapFromAttributes(Attributes attrs)
                            throws javax.naming.NamingException {

                        return (String) attrs.get("sAMAccountName").get().toString();
                    }
                });
        if (!result.isEmpty()) {
            try {
                ldapTemplate.authenticate(query, password);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean ldapAuthenticate(String user, String password, Long ldapId) throws Exception {

        var optLdap = ldapRepo.findById(ldapId);
        if (!optLdap.isPresent()) {
            throw new BadCredentialsException("LDAP server not found.");
        }

        var ldap = optLdap.get();

        LdapContextSource contextSource = new LdapContextSource();
        //CN=Itoss Ldpa User,OU=ITOSS,OU=Service Users,OU=Users,OU=Brasil,OU=CenturyLink Datacenter,DC=smpdc,DC=gblx

        contextSource.setUrl(ldap.getLdapUrl());
        contextSource.setBase(ldap.getBase());
        contextSource.setPassword(CryptoService.decrypt(ldap.getPassword()));
        contextSource.setUserDn(ldap.getUsername());
        contextSource.afterPropertiesSet();
        LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
        ldapTemplate.afterPropertiesSet();
        LdapQuery query = query()
                //.base("dc=261consulting,dc=com")

                .attributes("sAMAccountName")
                .where("sAMAccountName").is(user);


        var result = ldapTemplate.search(query,
                new AttributesMapper<String>() {
                    public String mapFromAttributes(Attributes attrs)
                            throws javax.naming.NamingException {

                        return (String) attrs.get("sAMAccountName").get().toString();
                    }
                });
        if (!result.isEmpty()) {
            try {
                ldapTemplate.authenticate(query, password);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }
}
