package com.issuetracker.repository;

import com.issuetracker.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByIssueIdOrderByCreatedAtDesc(Long issueId, Pageable pageable);
    long countByIssueId(Long issueId);
}
