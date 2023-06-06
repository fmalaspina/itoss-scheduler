package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.company.CompanyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CompanyTypeRepo extends JpaRepository<CompanyType, Long> {
    Page<CompanyType> findByName(String name, Pageable page);

    Page<Set<CompanyType>> findByParentId(Long id, Pageable page);

    Page<Set<CompanyType>> findByParentIsNull(Pageable page);

    Page<CompanyType> findByNameContainingIgnoringCase(String name, Pageable page);

}
