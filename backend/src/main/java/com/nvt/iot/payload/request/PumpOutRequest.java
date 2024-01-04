package com.nvt.iot.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PumpOutRequest {
    @NotNull(message = "User id is required!")
    @NotEmpty(message = "User id is not empty!")
    private String userId;
    @NotNull(message = "Device id is required!")
    @NotEmpty(message = "Device id is not empty!")
    private String deviceId;
    @NotNull(message = "Percentage not null")
    @Min(value = 0, message = "Percentage must greater than 0")
    @Max(value = 100, message = "Percentage must less than 100")
    private Integer percentage;
}
