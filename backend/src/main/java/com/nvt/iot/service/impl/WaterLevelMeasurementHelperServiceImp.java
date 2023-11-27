package com.nvt.iot.service.impl;

import com.nvt.iot.document.UpdateWaterLevelDocument;
import com.nvt.iot.document.WaterLevelStoreDocument;
import com.nvt.iot.document.XControlDocument;
import com.nvt.iot.model.WaterLevelData;
import com.nvt.iot.repository.UpdateWaterLevelRepository;
import com.nvt.iot.repository.WaterLevelStoreRepository;
import com.nvt.iot.repository.XControlRepository;
import com.nvt.iot.service.WaterLevelMeasurementHelperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WaterLevelMeasurementHelperServiceImp implements WaterLevelMeasurementHelperService {
    private final XControlRepository xControlRepository;
    private final UpdateWaterLevelRepository updateWaterLevelRepository;
    private final WaterLevelStoreRepository waterLevelStoreRepository;

    @Override
    public void createFirstXControl(String deviceId) {
        var XControlDoc = XControlDocument.builder()
            .deviceId(deviceId)
            .value(null)
            .build();
        xControlRepository.save(XControlDoc);
    }

    @Override
    public void createFirstWaterLevelStore(
        String userId,
        String deviceId,
        String controllerId,
        List<WaterLevelData> dataList
    ) {
        var waterLevelStoreDoc = WaterLevelStoreDocument.builder()
            .userId(userId)
            .deviceId(deviceId)
            .controllerId(controllerId)
            .waterLevelDataList(dataList)
            .build();
        waterLevelStoreRepository.save(waterLevelStoreDoc);
    }

    @Override
    public void createFirstWaterLevelUpdate(String userId, String deviceId, String controllerId) {
        var updateWaterLevelDoc = UpdateWaterLevelDocument.builder()
            .value(-1)
            .userId(userId)
            .createdAt(new Date(System.currentTimeMillis()))
            .controlUnitId(controllerId)
            .deviceId(deviceId)
            .build();
        updateWaterLevelRepository.save(updateWaterLevelDoc);
    }
}
