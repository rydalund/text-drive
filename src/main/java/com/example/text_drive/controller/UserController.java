package com.example.text_drive.controller;

import com.example.text_drive.dto.CreateUserRequestDTO;
import com.example.text_drive.dto.LoginRequestDTO;
import com.example.text_drive.dto.LoginResponseDTO;
import com.example.text_drive.dto.UserResponseDTO;
import com.example.text_drive.model.User;
import com.example.text_drive.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody CreateUserRequestDTO request) {
        try {
            User user = userService.createUser(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(UserResponseDTO.fromUser(user));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        try {
            String token = userService.login(request.getUsername(), request.getPassword());
            User user = userService.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(new LoginResponseDTO(
                    token,
                    UserResponseDTO.fromUser(user)
            ));
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password",
                    e
            );
        }
    }
}