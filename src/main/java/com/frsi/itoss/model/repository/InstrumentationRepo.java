package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.profile.Instrumentation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface InstrumentationRepo extends JpaRepository<Instrumentation, String> {
    Page<Set<Instrumentation>> findByNameContainingIgnoreCase(String name, Pageable page);
}
