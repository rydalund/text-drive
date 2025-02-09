package com.example.text_drive.dto;

//Basic UserDTO for Entity Relations
import com.example.text_drive.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;

    public UserDTO(User user) {
        if (user != null) {
            this.id = user.getId();
            this.username = user.getUsername();
        }
    }
}