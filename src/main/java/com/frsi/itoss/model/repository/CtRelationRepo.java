package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.ct.CtRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CtRelationRepo extends JpaRepository<CtRelation, Long> {

    List<CtRelation> findByRelatedFromId(Long id);

    List<CtRelation> findByRelatedToId(Long id);

    List<CtRelation> findByRelatedToIdAndImpactPercent(Long id, int impactPercent);
}
