package com.nvt.iot.service.impl;

import com.nvt.iot.model.WaterLevelData;
import com.nvt.iot.repository.WaterLevelStoreRepository;
import com.nvt.iot.service.WaterLevelStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WaterLevelStoreServiceImpl implements WaterLevelStoreService {
    private final WaterLevelStoreRepository waterLevelStoreRepository;

    @Override
    public List<WaterLevelData> getAllData(String userId, String controlUnitId, String deviceId) {
        List<WaterLevelData> waterLevelDataList = new ArrayList<>();
        waterLevelStoreRepository.findByUserIdAndDeviceIdAndControllerId(
            userId,
            deviceId,
            controlUnitId
        ).ifPresent((waterLevelStoreDocument) ->
            waterLevelDataList.addAll(waterLevelStoreDocument.getWaterLevelDataList())
        );
        return waterLevelDataList;
    }
}
