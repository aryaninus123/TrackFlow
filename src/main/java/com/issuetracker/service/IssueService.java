package com.issuetracker.service;

import com.issuetracker.dto.IssueRequest;
import com.issuetracker.dto.IssueResponse;
import com.issuetracker.dto.PageResponse;
import com.issuetracker.model.Issue;
import com.issuetracker.model.Issue.IssueStatus;
import com.issuetracker.model.User;
import com.issuetracker.repository.IssueRepository;
import com.issuetracker.repository.UserRepository;
import com.issuetracker.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssueService {
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;

    @Transactional
    public IssueResponse createIssue(IssueRequest request) {
        User reporter = getCurrentUser();

        Issue issue = new Issue();
        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setStatus(request.getStatus() != null ? request.getStatus() : IssueStatus.OPEN);
        issue.setPriority(request.getPriority() != null ? request.getPriority() : Issue.IssuePriority.MEDIUM);
        issue.setReporter(reporter);

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee with ID " + request.getAssigneeId() + " not found. Please select a valid user."));
            issue.setAssignee(assignee);
        }

        Issue savedIssue = issueRepository.save(issue);
        return mapToResponse(savedIssue);
    }

    @Transactional
    public IssueResponse updateIssue(Long id, IssueRequest request) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        if (request.getTitle() != null) {
            issue.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            issue.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            issue.setStatus(request.getStatus());
            if (request.getStatus() == IssueStatus.RESOLVED || request.getStatus() == IssueStatus.CLOSED) {
                issue.setResolvedAt(LocalDateTime.now());
            }
        }
        if (request.getPriority() != null) {
            issue.setPriority(request.getPriority());
        }
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee with ID " + request.getAssigneeId() + " not found. Please select a valid user."));
            issue.setAssignee(assignee);
        } else {
            issue.setAssignee(null);
        }

        Issue updatedIssue = issueRepository.save(issue);
        return mapToResponse(updatedIssue);
    }

    @Transactional(readOnly = true)
    public IssueResponse getIssue(Long id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        return mapToResponse(issue);
    }

    @Transactional(readOnly = true)
    public List<IssueResponse> getAllIssues() {
        return issueRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PageResponse<IssueResponse> getAllIssuesPaginated(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Issue> issuePage = issueRepository.findAll(pageable);

        return mapToPageResponse(issuePage);
    }

    @Transactional(readOnly = true)
    public PageResponse<IssueResponse> searchIssues(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Issue> issuePage = issueRepository.searchIssues(searchTerm, pageable);

        return mapToPageResponse(issuePage);
    }

    @Transactional(readOnly = true)
    public List<IssueResponse> getIssuesByStatus(IssueStatus status) {
        return issueRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IssueResponse> getMyIssues() {
        User user = getCurrentUser();
        return issueRepository.findByReporterId(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IssueResponse> getAssignedIssues() {
        User user = getCurrentUser();
        return issueRepository.findByAssigneeId(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteIssue(Long id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        issueRepository.delete(issue);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private IssueResponse mapToResponse(Issue issue) {
        IssueResponse response = new IssueResponse();
        response.setId(issue.getId());
        response.setTitle(issue.getTitle());
        response.setDescription(issue.getDescription());
        response.setStatus(issue.getStatus());
        response.setPriority(issue.getPriority());
        response.setResolvedAt(issue.getResolvedAt());
        response.setCreatedAt(issue.getCreatedAt());
        response.setUpdatedAt(issue.getUpdatedAt());

        User reporter = issue.getReporter();
        response.setReporter(new IssueResponse.UserSummary(
                reporter.getId(), reporter.getUsername(), reporter.getFullName()));

        if (issue.getAssignee() != null) {
            User assignee = issue.getAssignee();
            response.setAssignee(new IssueResponse.UserSummary(
                    assignee.getId(), assignee.getUsername(), assignee.getFullName()));
        }

        return response;
    }

    private PageResponse<IssueResponse> mapToPageResponse(Page<Issue> issuePage) {
        List<IssueResponse> content = issuePage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                issuePage.getNumber(),
                issuePage.getSize(),
                issuePage.getTotalElements(),
                issuePage.getTotalPages(),
                issuePage.isFirst(),
                issuePage.isLast()
        );
    }
}
