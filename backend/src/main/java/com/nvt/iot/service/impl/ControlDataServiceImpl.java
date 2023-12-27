package com.nvt.iot.service.impl;

import com.nvt.iot.model.ControlData;
import com.nvt.iot.model.ControlUnit;
import com.nvt.iot.repository.DeviceControllerUserRepository;
import com.nvt.iot.repository.DeviceRepository;
import com.nvt.iot.service.ControlDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ControlDataServiceImpl implements ControlDataService {
    private final DeviceControllerUserRepository deviceControllerUserRepository;
    private final DeviceRepository deviceRepository;

    @Override
    public List<ControlData> getAllControlDataByUserId(String userId) {
        List<ControlData> controlDataList = new ArrayList<>();
        deviceControllerUserRepository.findByUserId(userId)
            .ifPresent((controlDataDoc) -> {
                for (Map.Entry<String, List<ControlUnit>> entry : controlDataDoc.getDeviceIdControlUnit().entrySet()) {
                    deviceRepository.findById(entry.getKey())
                        .ifPresent((deviceDocument) -> {
                            var controlData = ControlData.builder()
                                .deviceId(deviceDocument.getId())
                                .deviceName(deviceDocument.getName())
                                .deviceDescription(deviceDocument.getDescription())
                                .controlUnitList(entry.getValue())
                                .build();
                            controlDataList.add(controlData);
                        });

                }
            });
        return controlDataList;
    }
}
