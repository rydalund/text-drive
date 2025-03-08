package com.example.text_drive.dto;

import com.example.text_drive.hateoas.LinkBuilder;
import com.example.text_drive.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.security.core.Authentication;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing the response for user-related operations.
 * Extends RepresentationModel to support HATEOAS links.
 */
@Getter
@AllArgsConstructor
public class UserResponseDTO extends RepresentationModel<UserResponseDTO> {

    private UUID id;
    private final String username;

    /**
     * Creates a UserResponseDTO from a User entity and adds HATEOAS links.
     *
     * @param user The User entity to convert into a DTO.
     * @param linkBuilder The LinkBuilder instance used to generate HATEOAS links.
     * @param authentication The Authentication object used for link generation.
     * @return A UserResponseDTO with user details and HATEOAS links.
     */
    public static UserResponseDTO fromUser(User user, LinkBuilder linkBuilder, Authentication authentication) {
        UserResponseDTO dto = new UserResponseDTO(user.getId(), user.getUsername());

        // Add HATEOAS links using LinkBuilder
        dto.add(linkBuilder.getUserSelfLink(user.getId())); // Self-link for the user
        dto.add(linkBuilder.getRegisterLink()); // Link to register a new user
        dto.add(linkBuilder.getLoginLink()); // Link to login

        return dto;
    }
}
