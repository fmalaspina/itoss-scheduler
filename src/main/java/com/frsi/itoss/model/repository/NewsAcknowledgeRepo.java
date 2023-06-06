package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.news.NewsAcknowledge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RepositoryRestResource
public interface NewsAcknowledgeRepo extends JpaRepository<NewsAcknowledge, Long> {
    Optional<NewsAcknowledge> findById(UUID id);

    Page<List<NewsAcknowledge>> findByUserIdOrderByNewsTimestampDesc(Long userId, Pageable page);

    Page<List<NewsAcknowledge>> findByUserIdAndNewsTypeOrderByNewsTimestampDesc(Long userId, String type, Pageable page);

}
