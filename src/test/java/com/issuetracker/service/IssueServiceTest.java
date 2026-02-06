package com.issuetracker.service;

import com.issuetracker.dto.IssueRequest;
import com.issuetracker.dto.IssueResponse;
import com.issuetracker.model.Issue;
import com.issuetracker.model.User;
import com.issuetracker.repository.IssueRepository;
import com.issuetracker.repository.UserRepository;
import com.issuetracker.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssueServiceTest {

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private IssueService issueService;

    private User testUser;
    private Issue testIssue;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        testUser.setRoles(roles);

        testIssue = new Issue();
        testIssue.setId(1L);
        testIssue.setTitle("Test Issue");
        testIssue.setDescription("Test Description");
        testIssue.setStatus(Issue.IssueStatus.OPEN);
        testIssue.setPriority(Issue.IssuePriority.MEDIUM);
        testIssue.setReporter(testUser);

        // Mock security context
        UserDetailsImpl userDetails = UserDetailsImpl.build(testUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCreateIssue_Success() {
        IssueRequest request = new IssueRequest();
        request.setTitle("New Issue");
        request.setDescription("Description");
        request.setPriority(Issue.IssuePriority.HIGH);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(issueRepository.save(any(Issue.class))).thenReturn(testIssue);

        IssueResponse response = issueService.createIssue(request);

        assertNotNull(response);
        assertEquals(testIssue.getId(), response.getId());
        assertEquals(testIssue.getTitle(), response.getTitle());
        verify(issueRepository, times(1)).save(any(Issue.class));
    }

    @Test
    void testGetIssue_Success() {
        when(issueRepository.findById(1L)).thenReturn(Optional.of(testIssue));

        IssueResponse response = issueService.getIssue(1L);

        assertNotNull(response);
        assertEquals(testIssue.getId(), response.getId());
        assertEquals(testIssue.getTitle(), response.getTitle());
    }

    @Test
    void testGetIssue_NotFound() {
        when(issueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> issueService.getIssue(999L));
    }

    @Test
    void testUpdateIssue_Success() {
        IssueRequest request = new IssueRequest();
        request.setTitle("Updated Title");
        request.setStatus(Issue.IssueStatus.IN_PROGRESS);

        when(issueRepository.findById(1L)).thenReturn(Optional.of(testIssue));
        when(issueRepository.save(any(Issue.class))).thenReturn(testIssue);

        IssueResponse response = issueService.updateIssue(1L, request);

        assertNotNull(response);
        verify(issueRepository, times(1)).save(any(Issue.class));
    }

    @Test
    void testDeleteIssue_Success() {
        when(issueRepository.findById(1L)).thenReturn(Optional.of(testIssue));
        doNothing().when(issueRepository).delete(any(Issue.class));

        issueService.deleteIssue(1L);

        verify(issueRepository, times(1)).delete(testIssue);
    }
}
