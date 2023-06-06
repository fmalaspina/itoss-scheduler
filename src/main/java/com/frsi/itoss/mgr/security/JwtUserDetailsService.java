package com.frsi.itoss.mgr.security;

import com.frsi.itoss.model.company.Contact;
import com.frsi.itoss.model.repository.ContactRepo;
import com.frsi.itoss.model.repository.UserAccountRepo;
import com.frsi.itoss.model.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserAccountRepo userAccountRepo;
    @Autowired
    private ContactRepo contactRepo;

    @Autowired
    AuthenticationFacade auth;

    @Autowired
    private PasswordEncoder bcryptEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserAccount user = userAccountRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        //responsabilityService.getWorkgroupsIds(user.getId());
//		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
//				new ArrayList<>());


        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), (user.getStatus().equals(UserStatus.ACTIVE) ? true : false), true, true,
                true, getAuthorities(user.getRoles()));


    }

    private List<String> getPrivileges(Collection<Role> roles) {

        List<String> privileges = new ArrayList<>();
        List<Privilege> collection = new ArrayList<>();
        for (Role role : roles) {
            collection.addAll(role.getPrivileges());
        }
        for (Privilege item : collection) {
            privileges.add(item.getName());
        }
        return privileges;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(
            Collection<Role> roles) {

        return getGrantedAuthorities(getPrivileges(roles));
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

    public UserAccount save(UserDTO user) {
        UserAccount newUser = new UserAccount();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
        return userAccountRepo.save(newUser);
    }

    public UserAccount getUserAccount(String username) {
        UserAccount user = userAccountRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return user;
    }

    public Contact getContact(Long id) {
        Optional<Contact> contact = contactRepo.findById(id);
        if (!contact.isPresent()) {
            throw new UsernameNotFoundException("Contact not found with id: " + id);
        }
        return contact.get();
    }


    public UserAccount getUserAccount(Long id) {
        Optional<UserAccount> user = userAccountRepo.findById(id);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("User not found with id: " + id);
        }
        return user.get();
    }

    public UserAccount savePassword(Long id, UserPasswordDTO user) {
        UserAccount userFound = this.getUserAccount(id);
        userFound.setPassword(bcryptEncoder.encode(user.getPassword()));
        userFound.setChangeOnNextLogin(user.isChangeOnNextLogin());
        return userAccountRepo.save(userFound);

    }

    public Object savePassword(Long id, ContactPasswordDTO user) {
        Contact contcactFound = this.getContact(id);
        contcactFound.setPassword(bcryptEncoder.encode(user.getPassword()));
        contcactFound.setChangeOnNextLogin(user.isChangeOnNextLogin());
        return contactRepo.save(contcactFound);
    }
}