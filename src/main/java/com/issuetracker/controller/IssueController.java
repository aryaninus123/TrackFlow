package com.issuetracker.controller;

import com.issuetracker.dto.IssueRequest;
import com.issuetracker.dto.IssueResponse;
import com.issuetracker.dto.PageResponse;
import com.issuetracker.model.Issue.IssueStatus;
import com.issuetracker.service.IssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
@Tag(name = "Issues", description = "Issue management endpoints")
public class IssueController {
    private final IssueService issueService;

    @PostMapping
    @Operation(summary = "Create a new issue", description = "Creates a new issue and returns the created issue details")
    public ResponseEntity<IssueResponse> createIssue(@Valid @RequestBody IssueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(issueService.createIssue(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an issue", description = "Updates an existing issue by ID")
    public ResponseEntity<IssueResponse> updateIssue(
            @Parameter(description = "Issue ID") @PathVariable Long id,
            @Valid @RequestBody IssueRequest request) {
        return ResponseEntity.ok(issueService.updateIssue(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get issue by ID", description = "Retrieves a single issue by its ID")
    public ResponseEntity<IssueResponse> getIssue(@Parameter(description = "Issue ID") @PathVariable Long id) {
        return ResponseEntity.ok(issueService.getIssue(id));
    }

    @GetMapping
    @Operation(summary = "Get all issues", description = "Retrieves all issues with optional status filter")
    public ResponseEntity<List<IssueResponse>> getAllIssues(
            @Parameter(description = "Filter by status") @RequestParam(required = false) IssueStatus status) {
        if (status != null) {
            return ResponseEntity.ok(issueService.getIssuesByStatus(status));
        }
        return ResponseEntity.ok(issueService.getAllIssues());
    }

    @GetMapping("/paginated")
    @Operation(summary = "Get paginated issues", description = "Retrieves issues with pagination and sorting")
    public ResponseEntity<PageResponse<IssueResponse>> getAllIssuesPaginated(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir) {
        return ResponseEntity.ok(issueService.getAllIssuesPaginated(page, size, sortBy, sortDir));
    }

    @GetMapping("/search")
    @Operation(summary = "Search issues", description = "Search issues by title or description")
    public ResponseEntity<PageResponse<IssueResponse>> searchIssues(
            @Parameter(description = "Search term") @RequestParam String q,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(issueService.searchIssues(q, page, size));
    }

    @GetMapping("/my-issues")
    @Operation(summary = "Get my issues", description = "Retrieves issues created by the current user")
    public ResponseEntity<List<IssueResponse>> getMyIssues() {
        return ResponseEntity.ok(issueService.getMyIssues());
    }

    @GetMapping("/assigned-to-me")
    @Operation(summary = "Get assigned issues", description = "Retrieves issues assigned to the current user")
    public ResponseEntity<List<IssueResponse>> getAssignedIssues() {
        return ResponseEntity.ok(issueService.getAssignedIssues());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an issue", description = "Deletes an issue by ID")
    public ResponseEntity<Void> deleteIssue(@Parameter(description = "Issue ID") @PathVariable Long id) {
        issueService.deleteIssue(id);
        return ResponseEntity.noContent().build();
    }
}
