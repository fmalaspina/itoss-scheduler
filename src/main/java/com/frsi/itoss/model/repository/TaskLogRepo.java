package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.task.TaskLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface TaskLogRepo extends JpaRepository<TaskLog, Long> {


    Optional<TaskLog> findById(Long id);

    Page<Set<TaskLog>> findByCtId(Long ctId, Pageable page);
}
