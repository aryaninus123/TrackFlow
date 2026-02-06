package com.issuetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentResponse {
    private Long id;
    private String filename;
    private String originalFilename;
    private String contentType;
    private Long fileSize;
    private String downloadUrl;
    private LocalDateTime uploadedAt;
    private UserSummary uploadedBy;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private Long id;
        private String username;
        private String fullName;
    }
}
