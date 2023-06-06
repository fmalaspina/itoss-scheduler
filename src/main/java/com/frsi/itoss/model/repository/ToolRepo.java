package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.tool.Tool;
import com.frsi.itoss.shared.MetricCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolRepo extends JpaRepository<Tool, Long> {
    Page<List<Tool>> findByNameContainingIgnoreCase(String name, Pageable page);

    List<Tool> findByMetricMetricCategory(MetricCategory category);

    List<Tool> findByMetricMetricCategoryAndMetricCtTypeId(MetricCategory category, Long ctTypeId);

    List<Tool> findByMetricCtTypeId(Long ctTypeId);
}
