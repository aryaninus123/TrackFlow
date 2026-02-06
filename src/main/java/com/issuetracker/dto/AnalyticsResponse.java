package com.issuetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {
    private Long totalIssues;
    private Map<String, Long> statusDistribution;
    private Map<String, Long> priorityDistribution;
    private Double averageResolutionTimeHours;
    private Long openIssues;
    private Long closedIssues;
    private Long inProgressIssues;
}
