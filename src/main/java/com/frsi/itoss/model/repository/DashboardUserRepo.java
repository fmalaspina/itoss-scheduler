package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.dashboard.DashboardUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface DashboardUserRepo extends JpaRepository<DashboardUser, Long>, JpaSpecificationExecutor<DashboardUser> {
    Set<DashboardUser> findByUserAccountId(Long id);

    Set<DashboardUser> findByDashboardTypeNameAndUserAccountId(String name, Long id);

    Set<DashboardUser> findByDashboardTypeNameAndUserAccountIdAndDefaultDashboardTrue(String name, Long id);


}