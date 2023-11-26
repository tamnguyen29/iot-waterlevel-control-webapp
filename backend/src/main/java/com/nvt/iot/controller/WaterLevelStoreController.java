package com.nvt.iot.controller;

import com.nvt.iot.service.WaterLevelStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class WaterLevelStoreController {
    private final WaterLevelStoreService waterLevelStoreService;


}
