package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.task.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {
    Page<List<Task>> findByNameContainingIgnoreCase(String name, Pageable page);

    Optional<Task> findById(Long id);

}
