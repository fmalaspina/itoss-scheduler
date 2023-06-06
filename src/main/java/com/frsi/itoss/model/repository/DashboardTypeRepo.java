package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.dashboard.DashboardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface DashboardTypeRepo extends JpaRepository<DashboardType, Long> {
    Page<DashboardType> findByName(String name, Pageable page);

    Page<DashboardType> findByNameContainingIgnoringCase(String name, Pageable page);

    Page<Set<DashboardType>> findByParentId(Long id, Pageable page);

    Page<Set<DashboardType>> findByParentIsNull(Pageable page);
}