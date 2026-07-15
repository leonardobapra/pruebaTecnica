package com.prueba.backend.controller;

import com.prueba.backend.dto.LoginRequest;
import com.prueba.backend.dto.LoginResponse;
import com.prueba.backend.dto.UserDto;
import com.prueba.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            UserDto userDto = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
            String token = "mock-jwt-token-" + UUID.randomUUID().toString() + "-" + userDto.getRole();
            return ResponseEntity.ok(new LoginResponse(token, userDto));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}
