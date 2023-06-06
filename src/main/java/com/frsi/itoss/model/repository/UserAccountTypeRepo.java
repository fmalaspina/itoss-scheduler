package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.user.UserAccountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserAccountTypeRepo extends JpaRepository<UserAccountType, Long> {
    UserAccountType findByName(String name);

    Page<Set<UserAccountType>> findByParentId(Long id, Pageable page);

    Page<Set<UserAccountType>> findByParentIsNull(Pageable page);

    Page<UserAccountType> findByNameContainingIgnoringCase(String name, Pageable page);

}
