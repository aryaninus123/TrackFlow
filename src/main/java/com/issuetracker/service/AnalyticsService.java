package com.issuetracker.service;

import com.issuetracker.dto.AnalyticsResponse;
import com.issuetracker.model.Issue.IssuePriority;
import com.issuetracker.model.Issue.IssueStatus;
import com.issuetracker.repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final IssueRepository issueRepository;

    @Transactional(readOnly = true)
    public AnalyticsResponse getAnalytics() {
        AnalyticsResponse response = new AnalyticsResponse();

        // Total issues
        response.setTotalIssues(issueRepository.count());

        // Status distribution
        Map<String, Long> statusDistribution = new HashMap<>();
        for (IssueStatus status : IssueStatus.values()) {
            Long count = issueRepository.countByStatus(status);
            statusDistribution.put(status.name(), count);
        }
        response.setStatusDistribution(statusDistribution);

        // Priority distribution
        Map<String, Long> priorityDistribution = new HashMap<>();
        for (IssuePriority priority : IssuePriority.values()) {
            Long count = issueRepository.countByPriority(priority);
            priorityDistribution.put(priority.name(), count);
        }
        response.setPriorityDistribution(priorityDistribution);

        // Average resolution time
        Double avgResolutionTime = issueRepository.getAverageResolutionTimeInHours();
        response.setAverageResolutionTimeHours(avgResolutionTime != null ? avgResolutionTime : 0.0);

        // Quick stats
        response.setOpenIssues(issueRepository.countByStatus(IssueStatus.OPEN));
        response.setClosedIssues(issueRepository.countByStatus(IssueStatus.CLOSED));
        response.setInProgressIssues(issueRepository.countByStatus(IssueStatus.IN_PROGRESS));

        return response;
    }
}
