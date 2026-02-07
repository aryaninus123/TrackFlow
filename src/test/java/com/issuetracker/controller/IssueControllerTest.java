package com.issuetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.issuetracker.dto.IssueRequest;
import com.issuetracker.model.Issue;
import com.issuetracker.model.User;
import com.issuetracker.repository.IssueRepository;
import com.issuetracker.repository.UserRepository;
import com.issuetracker.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IssueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private String authToken;
    private User testUser;

    @BeforeEach
    void setUp() {
        issueRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setFullName("Test User");
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        testUser.setRoles(roles);
        testUser = userRepository.save(testUser);

        // Generate token with UserDetailsImpl
        com.issuetracker.security.UserDetailsImpl userDetails = 
            com.issuetracker.security.UserDetailsImpl.build(testUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        authToken = jwtUtils.generateJwtToken(auth);
    }

    @Test
    void testCreateIssue_Success() throws Exception {
        IssueRequest request = new IssueRequest();
        request.setTitle("Test Issue");
        request.setDescription("Test Description");
        request.setPriority(Issue.IssuePriority.HIGH);

        mockMvc.perform(post("/api/issues")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Issue"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    void testGetAllIssues_Success() throws Exception {
        // Create test issue
        Issue issue = new Issue();
        issue.setTitle("Test Issue");
        issue.setDescription("Description");
        issue.setStatus(Issue.IssueStatus.OPEN);
        issue.setPriority(Issue.IssuePriority.MEDIUM);
        issue.setReporter(testUser);
        issueRepository.save(issue);

        mockMvc.perform(get("/api/issues")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Issue"));
    }

    @Test
    void testUpdateIssue_Success() throws Exception {
        // Create test issue
        Issue issue = new Issue();
        issue.setTitle("Original Title");
        issue.setDescription("Description");
        issue.setStatus(Issue.IssueStatus.OPEN);
        issue.setPriority(Issue.IssuePriority.MEDIUM);
        issue.setReporter(testUser);
        issue = issueRepository.save(issue);

        IssueRequest updateRequest = new IssueRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setStatus(Issue.IssueStatus.IN_PROGRESS);

        mockMvc.perform(put("/api/issues/" + issue.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void testDeleteIssue_Success() throws Exception {
        // Create test issue
        Issue issue = new Issue();
        issue.setTitle("Test Issue");
        issue.setDescription("Description");
        issue.setStatus(Issue.IssueStatus.OPEN);
        issue.setPriority(Issue.IssuePriority.MEDIUM);
        issue.setReporter(testUser);
        issue = issueRepository.save(issue);

        mockMvc.perform(delete("/api/issues/" + issue.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/issues"))
                .andExpect(status().isUnauthorized());
    }
}
