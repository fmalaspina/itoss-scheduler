package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.manager.Manager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ManagerRepo extends JpaRepository<Manager, Long> {
    Page<Set<Manager>> findByNameContainingIgnoreCase(String name, Pageable page);
}
