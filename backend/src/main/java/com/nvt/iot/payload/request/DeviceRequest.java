package com.nvt.iot.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceRequest {
    @NotNull(message = "Name is required!")
    @NotEmpty(message = "Name can not be empty!")
    private String name;

    @NotNull(message = "Description is required!")
    @NotEmpty(message = "Description can not be empty!")
    private String description;
}
