package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.collector.Collector;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CollectorRepo extends JpaRepository<Collector, Long> {
    Page<Set<Collector>> findByNameContainingIgnoreCase(String name, Pageable page);
}
