package com.example.text_drive.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

// To return login result
@Getter
@AllArgsConstructor
public class LoginResponseDTO {
    private final String token;
    private final UserResponseDTO user;
}
