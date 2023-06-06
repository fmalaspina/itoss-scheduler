package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.dashboard.Container;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ContainerRepo extends JpaRepository<Container, Long>, JpaSpecificationExecutor<Container> {
    Optional<Container> findById(Long id);

    Page<Set<Container>> findByNameContainingIgnoreCase(String name, Pageable page);
    //List<Container> findByMetricId(Long id);

}
