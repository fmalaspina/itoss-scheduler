package com.frsi.itoss.model.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.frsi.itoss.model.ldap.Ldap;
import com.frsi.itoss.model.ldap.LdapWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface LdapRepo extends JpaRepository<Ldap, Long> {
    Optional<Ldap> findById(Long id);

    Page<Set<Ldap>> findByNameContainingIgnoreCase(String name, Pageable page);

    @JsonIgnore
    List<LdapWrapper> findByOrderByName();

}
