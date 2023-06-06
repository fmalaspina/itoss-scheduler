package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.location.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository

public interface LocationRepo extends JpaRepository<Location, Long> {
    Page<Set<Location>> findByNameContainingIgnoreCase(String name, Pageable page);

    Page<Set<Location>> findByIdIn(List<Long> ids, Pageable page);

    Page<Set<Location>> findByKeyContainingIgnoreCase(String key, Pageable page);

    @RestResource(path = "findByNameContainingIgnoreCaseNoPaged", rel = "findByNameContainingIgnoreCaseNoPaged")
    Set<Location> findByNameContainingIgnoreCase(String name);

    Page<Set<Location>> findByParentId(Long id, Pageable page);

    Page<Set<Location>> findByParentIsNull(Pageable page);
}