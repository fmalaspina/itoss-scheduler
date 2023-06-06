package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.dashboard.DashboardUserView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface DashboardUserViewRepo extends JpaRepository<DashboardUserView, Long>, JpaSpecificationExecutor<DashboardUserView> {


    Set<DashboardUserView> findByDashboardIdAndUserAccountId(Long dashboardId, Long userAccountId);


}