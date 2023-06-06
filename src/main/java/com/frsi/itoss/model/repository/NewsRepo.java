package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.news.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RepositoryRestResource

public interface NewsRepo extends JpaRepository<News, Long> {


    Optional<News> findById(UUID id);

    @Modifying
    @Transactional
    @Query(value = """
            			
            begin \\; 
            delete from news_acknowledge where news_id in (select id from news  WHERE timestamp <= (NOW() - interval '4 hours'))\\; 
            delete from news where  timestamp <= (NOW() - interval '4 hours')\\; 
            			
            commit \\;
            end \\;
            """, nativeQuery = true)
    public void deleteOldNews();

    @Query(value = """
            WITH tennant AS (SELECT cts_id  FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id  
             WHERE tu.users_id = ?1 )	select n.*, a.status, a.timestamp as status_timestamp from news as n	
            		left join news_acknowledge as a on n.id = a.news_id and a.user_id =	?1 where (ct_id is null or ct_id in 
            				(select cts_id from tennant))	and (a.status is null or a.status != 'deleted') ORDER BY n.timestamp DESC \\;				
            """, nativeQuery = true)
    List<News> findByUserId(Long userId);


}
