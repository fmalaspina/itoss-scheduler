package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.profile.MonitoringProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface MonitoringProfileRepo extends JpaRepository<MonitoringProfile, Long> {
    Page<Set<MonitoringProfile>> findByNameContainingIgnoreCase(String name, Pageable page);
    Page<Set<MonitoringProfile>> findByNameContainingIgnoreCaseAndCtTypeId(String name, Long ctTypeId, Pageable page);
    Page<Set<MonitoringProfile>> findByCtTypeId(Long ctTypeId, Pageable page);
}
