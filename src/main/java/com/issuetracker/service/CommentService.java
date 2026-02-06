package com.issuetracker.service;

import com.issuetracker.dto.CommentRequest;
import com.issuetracker.dto.CommentResponse;
import com.issuetracker.dto.PageResponse;
import com.issuetracker.model.Comment;
import com.issuetracker.model.Issue;
import com.issuetracker.model.User;
import com.issuetracker.repository.CommentRepository;
import com.issuetracker.repository.IssueRepository;
import com.issuetracker.repository.UserRepository;
import com.issuetracker.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse createComment(Long issueId, CommentRequest request) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        
        User author = getCurrentUser();

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setIssue(issue);
        comment.setAuthor(author);

        Comment savedComment = commentRepository.save(comment);
        return mapToResponse(savedComment);
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, CommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        User currentUser = getCurrentUser();
        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only edit your own comments");
        }

        comment.setContent(request.getContent());
        Comment updatedComment = commentRepository.save(comment);
        return mapToResponse(updatedComment);
    }

    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> getCommentsByIssue(Long issueId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findByIssueIdOrderByCreatedAtDesc(issueId, pageable);

        return new PageResponse<>(
                commentPage.getContent().stream().map(this::mapToResponse).toList(),
                commentPage.getNumber(),
                commentPage.getSize(),
                commentPage.getTotalElements(),
                commentPage.getTotalPages(),
                commentPage.isFirst(),
                commentPage.isLast()
        );
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        User currentUser = getCurrentUser();
        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private CommentResponse mapToResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setIssueId(comment.getIssue().getId());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());

        User author = comment.getAuthor();
        response.setAuthor(new CommentResponse.AuthorSummary(
                author.getId(), author.getUsername(), author.getFullName()));

        return response;
    }
}
