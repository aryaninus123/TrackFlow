package com.issuetracker.controller;

import com.issuetracker.dto.AttachmentResponse;
import com.issuetracker.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Attachments", description = "File attachment management endpoints")
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PostMapping("/issues/{issueId}/attachments")
    @Operation(summary = "Upload attachment", description = "Uploads a file attachment to an issue")
    public ResponseEntity<AttachmentResponse> uploadAttachment(
            @Parameter(description = "Issue ID") @PathVariable Long issueId,
            @Parameter(description = "File to upload") @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(attachmentService.uploadFile(issueId, file));
    }

    @GetMapping("/issues/{issueId}/attachments")
    @Operation(summary = "Get attachments", description = "Retrieves all attachments for an issue")
    public ResponseEntity<List<AttachmentResponse>> getAttachments(
            @Parameter(description = "Issue ID") @PathVariable Long issueId) {
        return ResponseEntity.ok(attachmentService.getAttachmentsByIssue(issueId));
    }

    @GetMapping("/attachments/{attachmentId}/download")
    @Operation(summary = "Download attachment", description = "Downloads a file attachment")
    public ResponseEntity<Resource> downloadAttachment(
            @Parameter(description = "Attachment ID") @PathVariable Long attachmentId) throws IOException {
        Resource resource = attachmentService.downloadFile(attachmentId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/attachments/{attachmentId}")
    @Operation(summary = "Delete attachment", description = "Deletes a file attachment")
    public ResponseEntity<Void> deleteAttachment(
            @Parameter(description = "Attachment ID") @PathVariable Long attachmentId) throws IOException {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }
}
