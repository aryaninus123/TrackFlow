package com.issuetracker.controller;

import com.issuetracker.dto.UserResponse;
import com.issuetracker.model.User;
import com.issuetracker.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves a list of all users (for assignee selection)")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll().stream()
                .map(user -> new UserResponse(user.getId(), user.getUsername(), user.getEmail()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }
}
