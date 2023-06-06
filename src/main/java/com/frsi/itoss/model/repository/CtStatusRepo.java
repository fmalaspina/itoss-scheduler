package com.frsi.itoss.model.repository;

import com.frsi.itoss.shared.CtStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CtStatusRepo extends JpaRepository<CtStatus, Long> {

    Optional<CtStatus> findById(Long id);

    @Query(value = "WITH RECURSIVE managertree AS (  SELECT  id, attributes, creation_date, last_modified_date, created_by, last_modified_by, parent_id, type_id, workgroup_manager_id   FROM workgroup   WHERE workgroup_manager_id = ?1 OR id in (SELECT workgroup_id FROM workgroup_user_accounts WHERE user_accounts_id = ?1 )    UNION ALL   SELECT e.id, e.attributes, e.creation_date, e.last_modified_date, e.created_by, e.last_modified_by, e.parent_id, e.type_id, e.workgroup_manager_id   FROM workgroup e         INNER JOIN managertree mtree ON mtree.id = e.parent_id ) SELECT * FROM ct_status WHERE id in (SELECT id FROM ct where workgroup_id in (select id from managertree))", nativeQuery = true)
    Set<CtStatus> findByUserWorkgroups(Long id);

    @Query(value = "WITH RECURSIVE managertree AS (  SELECT  id, attributes, creation_date, last_modified_date, created_by, last_modified_by, parent_id, type_id, workgroup_manager_id   FROM workgroup   WHERE workgroup_manager_id = ?1 OR id in (SELECT workgroup_id FROM workgroup_user_accounts WHERE user_accounts_id = ?1 )    UNION ALL   SELECT e.id, e.attributes, e.creation_date, e.last_modified_date, e.created_by, e.last_modified_by, e.parent_id, e.type_id, e.workgroup_manager_id   FROM workgroup e         INNER JOIN managertree mtree ON mtree.id = e.parent_id ) SELECT * FROM ct_status WHERE id in (SELECT id FROM ct where company_id = ?2 AND workgroup_id in (select id from managertree))", nativeQuery = true)
    Set<CtStatus> findByUserWorkgroupsAndCompany(Long id, Long companyId);

    @Modifying
    @Query(value = "delete from ct_status where id = ?1", nativeQuery = true)
    void deleteStatusById(Long id);

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Query(value = "select c from CtStatus c where c.id = ?1")
    Optional<CtStatus> findByIdPessimisticWrite(Long id);
}
