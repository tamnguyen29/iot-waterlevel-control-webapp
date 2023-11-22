package com.nvt.iot.service;

import com.nvt.iot.model.WaterLevelStore;

import java.util.List;

public interface WaterLevelStoreService {
    List<WaterLevelStore> getAllWaterLevel(String userId, String controllerId);
}
