package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.dashboard.Dashboard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface DashboardRepo extends JpaRepository<Dashboard, Long>, JpaSpecificationExecutor<Dashboard> {
    Page<Set<Dashboard>> findByNameContainingIgnoreCase(String name, Pageable page);

    Page<Set<Dashboard>> findByUserAccountId(Long id, Pageable page);

    Page<Set<Dashboard>> findByUserAccountIsNull(Pageable page);

    Page<Set<Dashboard>> findByTypeNameAndUserAccountIsNull(String name, Pageable page);

    Page<Set<Dashboard>> findByTypeNameAndUserAccountId(String name, Long id, Pageable page);

    Page<Set<Dashboard>> findByTypeNameAndParentIdAndUserAccountIsNull(String name, Long parentId, Pageable page);

    Page<Set<Dashboard>> findByTypeNameAndParentIdAndUserAccountId(String name, Long parentId, Long id, Pageable page);

    Page<Set<Dashboard>> findByTypeNameAndParentId(String name, Long parentId, Pageable page);

    Set<Dashboard> findByTypeNameAndUserAccountIdAndDefaultDashboardTrue(String name, Long id);
}