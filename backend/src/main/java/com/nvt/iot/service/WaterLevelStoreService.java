package com.nvt.iot.service;


import com.nvt.iot.model.WaterLevelData;

import java.util.List;

public interface WaterLevelStoreService {
    List<WaterLevelData> getAllData(String userId, String controlUnitId, String deviceId);
    void deleteWaterLevelData(String userId, String controlUnitId, String deviceId);
}
