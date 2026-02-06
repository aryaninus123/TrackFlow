package com.issuetracker.repository;

import com.issuetracker.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByIssueIdOrderByUploadedAtDesc(Long issueId);
}
