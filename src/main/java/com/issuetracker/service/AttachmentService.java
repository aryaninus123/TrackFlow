package com.issuetracker.service;

import com.issuetracker.dto.AttachmentResponse;
import com.issuetracker.model.Attachment;
import com.issuetracker.model.Issue;
import com.issuetracker.model.User;
import com.issuetracker.repository.AttachmentRepository;
import com.issuetracker.repository.IssueRepository;
import com.issuetracker.repository.UserRepository;
import com.issuetracker.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;

    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    @Value("${file.upload.max-size:10485760}") // 10MB default
    private long maxFileSize;

    @Transactional
    public AttachmentResponse uploadFile(Long issueId, MultipartFile file) throws IOException {
        // Validate issue exists
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        // Validate file
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("File size exceeds maximum allowed size");
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        String filename = UUID.randomUUID().toString() + "." + extension;

        // Create upload directory if not exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Save metadata to database
        User uploader = getCurrentUser();
        Attachment attachment = new Attachment();
        attachment.setFilename(filename);
        attachment.setOriginalFilename(originalFilename);
        attachment.setContentType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setFilePath(filePath.toString());
        attachment.setIssue(issue);
        attachment.setUploadedBy(uploader);

        Attachment savedAttachment = attachmentRepository.save(attachment);
        return mapToResponse(savedAttachment);
    }

    @Transactional(readOnly = true)
    public List<AttachmentResponse> getAttachmentsByIssue(Long issueId) {
        return attachmentRepository.findByIssueIdOrderByUploadedAtDesc(issueId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Resource downloadFile(Long attachmentId) throws IOException {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        Path filePath = Paths.get(attachment.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("File not found or not readable");
        }
    }

    @Transactional
    public void deleteAttachment(Long attachmentId) throws IOException {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        // Delete physical file
        Path filePath = Paths.get(attachment.getFilePath());
        Files.deleteIfExists(filePath);

        // Delete database record
        attachmentRepository.delete(attachment);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private AttachmentResponse mapToResponse(Attachment attachment) {
        AttachmentResponse response = new AttachmentResponse();
        response.setId(attachment.getId());
        response.setFilename(attachment.getFilename());
        response.setOriginalFilename(attachment.getOriginalFilename());
        response.setContentType(attachment.getContentType());
        response.setFileSize(attachment.getFileSize());
        response.setDownloadUrl("/api/attachments/" + attachment.getId() + "/download");
        response.setUploadedAt(attachment.getUploadedAt());

        User uploader = attachment.getUploadedBy();
        response.setUploadedBy(new AttachmentResponse.UserSummary(
                uploader.getId(), uploader.getUsername(), uploader.getFullName()));

        return response;
    }
}
