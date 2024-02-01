package com.nvt.iot.service.impl;

import com.nvt.iot.document.DeviceControllerUserDocument;
import com.nvt.iot.model.ControlData;
import com.nvt.iot.repository.DeviceControllerUserRepository;
import com.nvt.iot.service.ControlDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ControlDataServiceImpl implements ControlDataService {
    private final DeviceControllerUserRepository deviceControllerUserRepository;

    @Override
    public List<ControlData> getAllControlDataByUserId(String userId) {
        Optional<DeviceControllerUserDocument> deviceControllerUserOptional =
            deviceControllerUserRepository.findByUserId(userId);
        return deviceControllerUserOptional.isPresent() ?
                    deviceControllerUserOptional.get().getControlDataList()
                    : new ArrayList<>();
    }
}
