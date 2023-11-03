package com.nvt.iot.payload.request;

import com.nvt.iot.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    @NotNull(message = "User name can not be null!")
    @NotEmpty(message = "User name can not be empty!")
    private String fullName;

    @NotNull(message = "Email can not be null!")
    @NotEmpty(message = "Email can not be empty!")
    @Email(
        message = "Email must be in right format!",
        regexp = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$"
    )
    private String email;

    @NotNull(message = "Password can not be null!")
    @NotEmpty(message = "Password can not be empty!")
    @Length(min = 6, message = "Password must have at least 6 characters!")
    private String password;

    @Pattern(regexp = "ROLE_USER|ROLE_ADMIN", message = "Invalid role name!")
    private String role;
}
