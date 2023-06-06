package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.contents.HelpContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HelpContentRepo extends JpaRepository<HelpContent, Long> {
    Boolean existsByKey(String key);

    Optional<HelpContent> findByKey(String key);

    Page<List<HelpContent>> findByNameContainingIgnoreCase(String name, Pageable page);

    Page<List<HelpContent>> findByRootTrue(Pageable page);

    Page<List<HelpContent>> findByRootFalse(Pageable page);

    Page<List<HelpContent>> findByRootNull(Pageable page);

}