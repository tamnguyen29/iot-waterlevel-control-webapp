package com.nvt.iot.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceStatusRequest {
    @NotNull(message = "Status is required!")
    @NotEmpty(message = "Status is not empty!")
    @Pattern(regexp = "^(AVAILABLE|BUSY|UNAVAILABLE)$", message = "Status should be 'AVAILABLE', 'BUSY', 'UNAVAILABLE'")
    private String status;
    @NotNull(message = "Device id is required!")
    @NotEmpty(message = "Device id is not empty!")
    private String deviceId;
}
