package com.nvt.iot.service;

import com.nvt.iot.model.ControlData;

import java.util.List;

public interface ControlDataService {
    List<ControlData> getAllControlDataByUserId(String userId);
}
