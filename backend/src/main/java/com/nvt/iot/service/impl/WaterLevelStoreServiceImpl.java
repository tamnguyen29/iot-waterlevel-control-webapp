package com.nvt.iot.service.impl;

import com.nvt.iot.model.WaterLevelStore;
import com.nvt.iot.repository.WaterLevelStoreRepository;
import com.nvt.iot.service.WaterLevelStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WaterLevelStoreServiceImpl implements WaterLevelStoreService {
    private final WaterLevelStoreRepository waterLevelStoreRepository;

    @Override
    public List<WaterLevelStore> getAllWaterLevel(String userId, String controllerId) {
        return waterLevelStoreRepository.findAllWaterLevelsByCreatorIdAndControllerId(
            userId,
            controllerId
        );
    }
}
