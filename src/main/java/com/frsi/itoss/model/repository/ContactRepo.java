package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.company.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ContactRepo extends JpaRepository<Contact, Long> {
    Page<Set<Contact>> findByNameContainingIgnoreCase(String name, Pageable page);

    List<Contact> findByCompanyId(Long id);

    Page<Set<Contact>> findByKeyContainingIgnoreCase(String key, Pageable page);

    Optional<Contact> findByEmail(String email);
}