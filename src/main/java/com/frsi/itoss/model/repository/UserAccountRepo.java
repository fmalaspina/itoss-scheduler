package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.user.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Repository
public interface UserAccountRepo extends JpaRepository<UserAccount, Long> {

    @Query(value="select * from user_account where ?1 = '' or name ilike Concat('%',?1,'%') or username ilike Concat('%',?1,'%')",
            countQuery = "select count(*) from user_account where ?1 = '' or name ilike Concat('%',?1,'%') or username ilike Concat('%',?1,'%')",nativeQuery = true)
    Page<Set<UserAccount>> findByNameContainingIgnoreCase(String name, Pageable page);

    UserAccount findByUsername(String username);

    Page<Set<UserAccount>> findByCompanyId(Long id, Pageable page);

    Page<Set<UserAccount>> findByManagerId(Long id, Pageable page);

    @Modifying
    @Transactional
    @Query(value = """
            begin\\;
            delete from tennant_users where users_id = :id\\;
            delete from role_users where users_id = :id\\;
            delete from user_account_roles where user_account_id = :id\\;
            delete from workgroup_user_accounts where user_accounts_id = :id\\;
            delete from dashboard_user where user_account_id = :id\\; 
            update ct set support_user_id = null where support_user_id = :id\\;
            commit\\;
            end\\;
            			
            """, nativeQuery = true)
    void deleteByUserId(@Param("id") Long id);


}