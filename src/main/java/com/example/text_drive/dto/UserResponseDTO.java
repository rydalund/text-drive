package com.example.text_drive.dto;

import com.example.text_drive.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

// To return user information
@Getter
@AllArgsConstructor
public class UserResponseDTO {
    private UUID id;
    private final String username;

    public static UserResponseDTO fromUser(User user) {
        return new UserResponseDTO(user.getId(), user.getUsername());
    }
}