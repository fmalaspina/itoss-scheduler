package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.tennant.Tennant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TennantRepo extends JpaRepository<Tennant, Long> {
    Page<Set<Tennant>> findByNameContainingIgnoreCase(String name, Pageable page);

    Tennant findByName(String name);

    Set<Tennant> findByCtsId(Long id);

    Set<Tennant> findByUsersId(Long id);

    @Query(value = "select tu.users_id from tennant_users tu inner join (select * from tennant_cts t where t.cts_id = ?1) tc on tu.tennant_id = tc.tennant_id ", nativeQuery = true)
    Set<Long> findUserIdsByCtId(Long id);


}