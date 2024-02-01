package com.nvt.iot.service.impl;

import com.nvt.iot.exception.NotFoundCustomException;
import com.nvt.iot.model.ControlData;
import com.nvt.iot.model.ControlUnit;
import com.nvt.iot.model.WaterLevelData;
import com.nvt.iot.repository.DeviceControllerUserRepository;
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
    private final DeviceControllerUserRepository deviceControllerUserRepository;

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

    @Override
    public void deleteWaterLevelData(String userId, String controlUnitId, String deviceId) {
        if (!waterLevelStoreRepository.existsByUserIdAndDeviceIdAndControllerId(userId, deviceId, controlUnitId)) {
            throw new NotFoundCustomException("Water level data not found");
        }
        deviceControllerUserRepository.findByUserId(userId)
            .ifPresent((deviceControllerUserDocument -> {
                List<ControlData> controlDataList = deviceControllerUserDocument.getControlDataList();
                if (controlDataList != null && controlDataList.size() > 0) {
                    ControlData controlData = controlDataList.stream()
                        .filter((controlDataItem -> controlDataItem.getDeviceId().equals(deviceId)))
                        .findFirst()
                        .orElse(null);
                    if (controlData != null) {
                        List<ControlUnit> controlUnitList = controlData.getControlUnitList();
                        boolean isDeleteControlUnitSuccess = controlUnitList
                            .removeIf(controlUnit -> controlUnit.getId().equals(controlUnitId));
                        if (isDeleteControlUnitSuccess) {
                            int index = controlDataList.indexOf(controlData);
                            controlData.setControlUnitList(controlUnitList);
                            controlDataList.set(index, controlData);
                            deviceControllerUserDocument.setControlDataList(controlDataList);
                            deviceControllerUserRepository.save(deviceControllerUserDocument);
                        }
                    }
                }
            }));
        waterLevelStoreRepository.deleteByUserIdAndControllerIdAndDeviceId(userId, controlUnitId, deviceId);
    }
}
