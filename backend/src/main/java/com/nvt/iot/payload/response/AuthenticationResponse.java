package com.nvt.iot.payload.response;

import com.nvt.iot.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private UserDTO user;
    private String accessToken;
    private String refreshToken;
}
