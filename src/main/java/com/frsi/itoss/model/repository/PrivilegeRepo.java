package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.user.Privilege;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PrivilegeRepo extends JpaRepository<Privilege, Long> {
    Page<Set<Privilege>> findByNameContainingIgnoreCase(String name, Pageable page);

    Privilege findByName(String name);

}