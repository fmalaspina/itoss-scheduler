package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.company.ContactType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ContactTypeRepo extends JpaRepository<ContactType, Long> {
    Page<ContactType> findByName(String name, Pageable page);

    Page<Set<ContactType>> findByParentId(Long id, Pageable page);

    Page<Set<ContactType>> findByParentIsNull(Pageable page);

    Page<ContactType> findByNameContainingIgnoringCase(String name, Pageable page);

}