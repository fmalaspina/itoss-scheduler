package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.parameters.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ParametersRepo extends JpaRepository<Parameter, String> {
    Optional<Parameter> findById(String id);

    Set<Parameter> findByType(String type);

    List<Parameter> findAll();

    Set<Parameter> findByIdContainingIgnoringCaseAndType(String id, String type);
}