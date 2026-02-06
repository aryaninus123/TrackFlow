package com.issuetracker.dto;

import com.issuetracker.model.Issue.IssuePriority;
import com.issuetracker.model.Issue.IssueStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueResponse {
    private Long id;
    private String title;
    private String description;
    private IssueStatus status;
    private IssuePriority priority;
    private UserSummary reporter;
    private UserSummary assignee;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private Long id;
        private String username;
        private String fullName;
    }
}
