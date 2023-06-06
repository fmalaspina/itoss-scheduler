package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.dynamicsearch.DynamicSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository

public interface DynamicSearchRepo extends JpaRepository<DynamicSearch, Long> {
    Page<Set<DynamicSearch>> findByNameContainingIgnoreCase(String name, Pageable page);

    Optional<DynamicSearch> findByEndpoint(String endpoint);

    Page<Set<DynamicSearch>> findByGroupNameContainingIgnoreCase(String groupName, Pageable page);

    Page<Set<DynamicSearch>> findByNameContainingIgnoreCaseAndGroupNameContainingIgnoreCase(String name, String groupName, Pageable page);

    @RestResource(path = "findByNameContainingIgnoreCaseNoPaged", rel = "findByNameContainingIgnoreCaseNoPaged")
    Set<DynamicSearch> findByNameContainingIgnoreCase(String name);

}