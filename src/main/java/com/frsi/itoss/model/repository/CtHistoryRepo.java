package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.ct.CtHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CtHistoryRepo extends JpaRepository<CtHistory, Long> {
    Optional<CtHistory> findById(Long id);

    @Query(value = "select * from ct_history where ct_id = ?1", nativeQuery = true)
    Page<List<CtHistory>> findByCtId(Long ctId, Pageable page);

}