package com.nvt.iot.service.impl;

import com.nvt.iot.repository.WaterLevelStoreRepository;
import com.nvt.iot.service.WaterLevelStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WaterLevelStoreServiceImpl implements WaterLevelStoreService {
    private final WaterLevelStoreRepository waterLevelStoreRepository;


}
