package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.ct.CtType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CtTypeRepo extends JpaRepository<CtType, Long> {
    Page<CtType> findByName(String name, Pageable page);

    Page<Set<CtType>> findByParentId(Long id, Pageable page);

    Page<Set<CtType>> findByParentIsNull(Pageable page);

    Page<CtType> findByNameContainingIgnoringCase(String name, Pageable page);

}
