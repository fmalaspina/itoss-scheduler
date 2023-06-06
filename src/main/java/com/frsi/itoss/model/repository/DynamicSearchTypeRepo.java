package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.dynamicsearch.DynamicSearchType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface DynamicSearchTypeRepo extends JpaRepository<DynamicSearchType, Long> {
    Page<DynamicSearchType> findByName(String name, Pageable page);

    Page<DynamicSearchType> findByNameContainingIgnoringCase(String name, Pageable page);

    Page<Set<DynamicSearchType>> findByParentId(Long id, Pageable page);

    Page<Set<DynamicSearchType>> findByParentIsNull(Pageable page);


}
