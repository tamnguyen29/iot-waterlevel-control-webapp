package com.nvt.iot.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceResetProcessRequest {
    @NotNull(message = "Device id is required!")
    @NotEmpty(message = "Device id is not empty!")
    private String deviceId;

    @NotNull(message = "User id is not null!")
    @NotEmpty(message = "User id is not empty!")
    private String userId;

    @NotNull(message = "Status is required!")
    @NotEmpty(message = "Status is not empty!")
    @Pattern(regexp = "^(START_RESET|FINISH_RESET)$", message = "Status should be 'START_RESET', 'FINISH_RESET'")
    private String status;
}
