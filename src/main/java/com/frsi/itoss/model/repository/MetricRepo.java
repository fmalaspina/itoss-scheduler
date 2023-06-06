package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.profile.Metric;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MetricRepo extends JpaRepository<Metric, Long> {
    Page<Set<Metric>> findByNameContainingIgnoreCase(String name, Pageable page);

    Page<List<Metric>> findByCtTypeId(Long ctTypeId, Pageable page);

    Optional<Metric> findByName(String name);
    Optional<Metric> findById(Long id);

    @Query(
            value = """
                    SELECT m.* 
                      FROM Metric m 
                     WHERE (:name = '' OR m.name ILIKE CONCAT('%',:name,'%'))
                     	   AND (:metricCategory = '' OR m.metric_category = :metricCategory) 
                    	   AND (:ctTypeId = 0 OR m.ct_type_id = :ctTypeId)
                    """,
            nativeQuery = true
    )
    List<Metric> findByNameAndMetricCategoryAndCtTypeId(@Param("name") String name, @Param("metricCategory") String metricCategory, @Param("ctTypeId") Long ctTypeId);
    @Query(
            value = """
				SELECT m.* 
				  FROM Metric m 
				 WHERE (m.metric_category = 'Availability') 
					   AND (m.ct_type_id = :ctTypeId)
				""",
            nativeQuery = true
    )
    Optional<Metric> findAvailabilityMetricByCtTypeId(@Param("ctTypeId") Long ctTypeId);

}
