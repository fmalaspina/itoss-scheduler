package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.company.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository

public interface CompanyRepo extends JpaRepository<Company, Long> {

    Page<Set<Company>> findByNameContainingIgnoreCase(String name, Pageable page);

    Page<Set<Company>> findByKeyContainingIgnoreCase(String key, Pageable page);

    Company findByName(String name);

    Page<Set<Company>> findByParentId(Long id, Pageable page);

    Page<Set<Company>> findByParentIsNull(Pageable page);

    @Query(value = "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = ?1) SELECT DISTINCT co.* FROM ct c INNER JOIN company co ON c.company_id = co.id WHERE c.id IN (SELECT cts_id FROM tennant) AND (?2 = '' OR co.name ILIKE Concat('%',?2,'%')) ORDER BY co.name ASC", countQuery = "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = ?1) SELECT COUNT(DISTINCT co.*) FROM ct c INNER JOIN company co ON c.company_id = co.id WHERE c.id IN (SELECT cts_id FROM tennant) AND (?2 = '' OR co.name ILIKE Concat('%',?2,'%'))", nativeQuery = true)
    Page<Set<Company>> findByUserId(Long userId, String name, Pageable page);

    @Query(value = "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = ?1) SELECT DISTINCT co.* FROM ct c INNER JOIN company co ON c.company_id = co.id WHERE c.id IN (SELECT cts_id FROM tennant) AND (?2 = '' OR co.name ILIKE Concat('%',?2,'%')) AND (?3 = 0 OR co.location_id = ?3 ) ", countQuery = "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = ?1) SELECT COUNT(DISTINCT co.*) FROM ct c INNER JOIN company co ON c.company_id = co.id WHERE c.id IN (SELECT cts_id FROM tennant) AND (?2 = '' OR co.name ILIKE Concat('%',?2,'%') ) AND (?3 = 0 OR co.location_id = ?3 )", nativeQuery = true)
    Page<Set<Company>> findByUserIdAndLocationId(Long userId, String name, Long locationId, Pageable page);

    @Query(value = "SELECT * FROM company as company WHERE (?1 = '' OR name ILIKE Concat('%',?1,'%')) AND (?2 = 0 OR location_id = ?2 ) ", countQuery = "SELECT COUNT(*) FROM company co WHERE (?1 = '' OR name ILIKE Concat('%',?1,'%') ) AND (?2 = 0 OR location_id = ?2 )", nativeQuery = true)
    Page<Set<Company>> findByNameIgnoringCaseAndLocationId(String name, Long locationId, Pageable page);

    Page<Set<Company>> findByNameContainingIgnoringCaseOrLocationId(String name, Long locationId, Pageable page);

    Page<Set<Company>> findByIdIn(List<Long> ids, Pageable page);
}


