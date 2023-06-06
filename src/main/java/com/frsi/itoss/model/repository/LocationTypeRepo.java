package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.location.LocationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface LocationTypeRepo extends JpaRepository<LocationType, Long> {
    Page<LocationType> findByName(String name, Pageable page);

    Page<LocationType> findByNameContainingIgnoringCase(String name, Pageable page);

    Page<Set<LocationType>> findByParentId(Long id, Pageable page);

    Page<Set<LocationType>> findByParentIsNull(Pageable page);


}
