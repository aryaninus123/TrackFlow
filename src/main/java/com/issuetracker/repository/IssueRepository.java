package com.issuetracker.repository;

import com.issuetracker.model.Issue;
import com.issuetracker.model.Issue.IssueStatus;
import com.issuetracker.model.Issue.IssuePriority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    // Paginated queries
    Page<Issue> findAll(Pageable pageable);
    Page<Issue> findByStatus(IssueStatus status, Pageable pageable);
    Page<Issue> findByPriority(IssuePriority priority, Pageable pageable);
    Page<Issue> findByReporterId(Long reporterId, Pageable pageable);
    Page<Issue> findByAssigneeId(Long assigneeId, Pageable pageable);
    
    // Search functionality
    @Query("SELECT i FROM Issue i WHERE " +
           "LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Issue> searchIssues(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Non-paginated queries (kept for backward compatibility)
    List<Issue> findByStatus(IssueStatus status);
    List<Issue> findByPriority(IssuePriority priority);
    List<Issue> findByReporterId(Long reporterId);
    List<Issue> findByAssigneeId(Long assigneeId);
    
    @Query("SELECT COUNT(i) FROM Issue i WHERE i.status = ?1")
    Long countByStatus(IssueStatus status);
    
    @Query("SELECT COUNT(i) FROM Issue i WHERE i.priority = ?1")
    Long countByPriority(IssuePriority priority);
    
    @Query("SELECT i FROM Issue i WHERE i.status = 'RESOLVED' AND i.resolvedAt BETWEEN ?1 AND ?2")
    List<Issue> findResolvedIssuesBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT AVG(TIMESTAMPDIFF(HOUR, i.createdAt, i.resolvedAt)) FROM Issue i WHERE i.status = 'RESOLVED' AND i.resolvedAt IS NOT NULL")
    Double getAverageResolutionTimeInHours();
}
