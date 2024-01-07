package com.nvt.iot.dto;

import com.nvt.iot.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatar;
    private Date createdAt;
    private Date updatedAt;
    private Role role;
}
