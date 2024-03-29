package com.nvt.iot.service.impl;

import com.nvt.iot.document.UpdateWaterLevelDocument;
import com.nvt.iot.document.WaterLevelStoreDocument;
import com.nvt.iot.document.XControlDocument;
import com.nvt.iot.model.ControlParameter;
import com.nvt.iot.model.WaterLevelData;
import com.nvt.iot.repository.UpdateWaterLevelRepository;
import com.nvt.iot.repository.WaterLevelStoreRepository;
import com.nvt.iot.repository.XControlRepository;
import com.nvt.iot.service.WaterLevelMeasurementHelperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WaterLevelMeasurementHelperServiceImp implements WaterLevelMeasurementHelperService {
    private final XControlRepository xControlRepository;
    private final UpdateWaterLevelRepository updateWaterLevelRepository;
    private final WaterLevelStoreRepository waterLevelStoreRepository;

    @Override
    public void createFirstWaterLevelStore(
        String userId,
        String deviceId,
        String controllerId
    ) {
        if (!waterLevelStoreRepository.existsByUserIdAndDeviceIdAndControllerId(userId, deviceId, controllerId)) {
            var waterLevelStoreDoc = WaterLevelStoreDocument.builder()
                    .userId(userId)
                    .deviceId(deviceId)
                    .controllerId(controllerId)
                    .waterLevelDataList(new ArrayList<>())
                    .build();
            waterLevelStoreRepository.save(waterLevelStoreDoc);
        }
    }

    @Override
    public void createFirstWaterLevelUpdate(double value, Date time, String deviceId, ControlParameter controlParameter) {
        if (!updateWaterLevelRepository.existsByDeviceId(deviceId)) {
            var updateWaterLevelDoc = UpdateWaterLevelDocument.builder()
                .value(value)
                .time(time)
                .deviceId(deviceId)
                .controlParameter(controlParameter)
                .build();
            updateWaterLevelRepository.save(updateWaterLevelDoc);
        }
    }

    @Override
    public void createFirstXControl(String deviceId) {
        if (!xControlRepository.existsByDeviceId(deviceId)) {
            var xControlDoc = XControlDocument.builder()
                .deviceId(deviceId)
                .value(null)
                .build();
            xControlRepository.save(xControlDoc);
        }
    }
}
