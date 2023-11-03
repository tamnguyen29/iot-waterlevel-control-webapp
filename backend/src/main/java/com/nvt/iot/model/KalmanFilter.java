package com.nvt.iot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KalmanFilter {
    @NotNull(message = "x is required!")
    private Double x;
    @NotNull(message = "sigma_w is required!")
    private Double sigma_w;
    @NotNull(message = "sigma_x is required!")
    private Double sigma_x;
    @NotNull(message = "sigma_v is required!")
    private Double sigma_v;
}
