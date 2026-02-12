package com.example.userservice.dto;

import com.example.userservice.models.Role;
import com.example.userservice.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDTO {
    private Long userId;
    private String email;
    private List<Role> roles;

    public static UserDTO from(User user) {
        if(user == null) return null;

        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setRoles(user.getRoles());
        return userDTO;
    }
}
