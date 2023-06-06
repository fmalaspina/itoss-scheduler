package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.workgroup.WorkgroupType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface WorkgroupTypeRepo extends JpaRepository<WorkgroupType, Long> {
    Page<WorkgroupType> findByName(String name, Pageable page);

    Page<Set<WorkgroupType>> findByParentId(Long id, Pageable page);

    Page<Set<WorkgroupType>> findByParentIsNull(Pageable page);

    Page<WorkgroupType> findByNameContainingIgnoringCase(String name, Pageable page);

}
