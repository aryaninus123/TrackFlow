package com.issuetracker.dto;

import com.issuetracker.model.Issue.IssuePriority;
import com.issuetracker.model.Issue.IssueStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IssueRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private IssueStatus status;

    private IssuePriority priority;

    private Long assigneeId;
}
