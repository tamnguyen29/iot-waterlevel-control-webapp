package com.nvt.iot.payload.request;

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
public class UserUpdateInformationRequest {
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

    @NotNull(message = "Phone number can not be null!")
    @NotEmpty(message = "Phone number can not be empty!")
    @Length(min = 6, message = "Password must have at least 6 characters!")
    @Length(min = 6, message = "Password must have at least 6 characters!")
    @Pattern(regexp = "(\\\\+61|0)[0-9]{9}", message = "Phone number must in right format")
    private String phoneNumber;

    @NotNull(message = "Avatar url can not null!")
    @NotEmpty(message = "Avatar url can not be empty!")
    private String avatar;
}
