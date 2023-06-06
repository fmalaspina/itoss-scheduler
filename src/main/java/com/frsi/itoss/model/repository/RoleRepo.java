package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.user.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
    Page<Set<Role>> findByNameContainingIgnoreCase(String name, Pageable page);

    Role findByName(String name);

    @Query(value = "SELECT DISTINCT r.* FROM role r INNER JOIN role_users ru ON r.id = ru.role_id  WHERE ru.users_id = ?1", nativeQuery = true)
    List<Role> findByUsersId(Long userAccountId);

}