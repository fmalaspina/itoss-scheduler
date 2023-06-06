package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.continuousqueries.ContinuousQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContinuousQueryRepo extends JpaRepository<ContinuousQuery, Long> {


    List<ContinuousQuery> findByMetricId(Long id);

    @Query(value = "select * from continuous_query where metric_id = :metricId", nativeQuery = true)
    List<ContinuousQuery> findByMetricIdCustom(@Param("metricId") Long Id);
}
