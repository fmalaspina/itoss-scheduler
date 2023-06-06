package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.workgroup.Workgroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface WorkgroupRepo extends JpaRepository<Workgroup, Long> {
    Page<Set<Workgroup>> findByNameContainingIgnoreCase(String name, Pageable page);

    Page<Set<Workgroup>> findByParentId(Long id, Pageable page);

    Page<Set<Workgroup>> findByParentIsNull(Pageable page);

    Page<Set<Workgroup>> findByUserAccountsId(Long id, Pageable page);

    //Set<Workgroup> findByUserAccountsId(Long id);
    Set<Workgroup> findByWorkgroupManagerId(Long id);

    Set<Workgroup> findByParentIdIsIn(Set<Long> ids);

    Page<Set<Workgroup>> findByIdIn(List<Long> ids, Pageable page);

    @Query(value = "(WITH RECURSIVE managertree AS (  SELECT  id, name, attributes, creation_date, last_modified_date, created_by, last_modified_by, parent_id, type_id, workgroup_manager_id   FROM workgroup   WHERE workgroup_manager_id = ?1 OR id in (SELECT workgroup_id FROM workgroup_user_accounts WHERE user_accounts_id = ?1 )    UNION ALL   SELECT e.id, e.name, e.attributes, e.creation_date, e.last_modified_date, e.created_by, e.last_modified_by, e.parent_id, e.type_id, e.workgroup_manager_id   FROM workgroup e  INNER JOIN managertree mtree ON mtree.id = e.parent_id ) SELECT * FROM managertree)", nativeQuery = true)
    Set<Workgroup> findByUserId(Long id);
}