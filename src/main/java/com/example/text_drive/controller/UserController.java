package com.example.text_drive.controller;

import com.example.text_drive.dto.CreateUserRequestDTO;
import com.example.text_drive.dto.LoginRequestDTO;
import com.example.text_drive.dto.LoginResponseDTO;
import com.example.text_drive.dto.UserResponseDTO;
import com.example.text_drive.hateoas.LinkBuilder;
import com.example.text_drive.model.User;
import com.example.text_drive.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final LinkBuilder linkBuilder; // Inject LinkBuilder

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody CreateUserRequestDTO request) {
        try {
            User user = userService.createUser(request.getUsername(), request.getPassword());
            UserResponseDTO responseDTO = UserResponseDTO.fromUser(user, linkBuilder, null);
            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request, Authentication authentication) {
        try {
            String token = userService.login(request.getUsername(), request.getPassword());
            User user = userService.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserResponseDTO userResponseDTO = UserResponseDTO.fromUser(user, linkBuilder, authentication);
            LoginResponseDTO loginResponseDTO = new LoginResponseDTO(token, userResponseDTO, linkBuilder, authentication);
            return ResponseEntity.ok(loginResponseDTO);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password",
                    e
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable UUID id, Authentication authentication) {
        User user = userService.findUserById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserResponseDTO responseDTO = UserResponseDTO.fromUser(user, linkBuilder, authentication);
        return ResponseEntity.ok(responseDTO);
    }
}
