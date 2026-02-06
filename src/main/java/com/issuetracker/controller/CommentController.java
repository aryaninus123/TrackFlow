package com.issuetracker.controller;

import com.issuetracker.dto.CommentRequest;
import com.issuetracker.dto.CommentResponse;
import com.issuetracker.dto.PageResponse;
import com.issuetracker.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/issues/{issueId}/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Comment management endpoints")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "Create a comment", description = "Adds a new comment to an issue")
    public ResponseEntity<CommentResponse> createComment(
            @Parameter(description = "Issue ID") @PathVariable Long issueId,
            @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(issueId, request));
    }

    @GetMapping
    @Operation(summary = "Get comments", description = "Retrieves all comments for an issue with pagination")
    public ResponseEntity<PageResponse<CommentResponse>> getComments(
            @Parameter(description = "Issue ID") @PathVariable Long issueId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(commentService.getCommentsByIssue(issueId, page, size));
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "Update a comment", description = "Updates an existing comment")
    public ResponseEntity<CommentResponse> updateComment(
            @Parameter(description = "Issue ID") @PathVariable Long issueId,
            @Parameter(description = "Comment ID") @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.updateComment(commentId, request));
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete a comment", description = "Deletes a comment by ID")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "Issue ID") @PathVariable Long issueId,
            @Parameter(description = "Comment ID") @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
