package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.dashboard.ContainerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ContainerTypeRepo extends JpaRepository<ContainerType, Long> {
    Page<ContainerType> findByName(String name, Pageable page);

    Page<Set<ContainerType>> findByParentId(Long id, Pageable page);

    Page<Set<ContainerType>> findByParentIsNull(Pageable page);

    Page<ContainerType> findByNameContainingIgnoringCase(String name, Pageable page);

}