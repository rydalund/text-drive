package com.example.text_drive.dto;

import com.example.text_drive.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

// To return user information
@Getter
@AllArgsConstructor
public class UserResponseDTO {
    private final Long id;
    private final String username;

    public static UserResponseDTO fromUser(User user) {
        return new UserResponseDTO(user.getId(), user.getUsername());
    }
}