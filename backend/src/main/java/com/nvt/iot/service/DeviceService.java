package com.nvt.iot.service;

import com.nvt.iot.document.DeviceDocument;
import com.nvt.iot.payload.request.DeviceRequest;
import com.nvt.iot.payload.response.DevicesResponse;
import org.springframework.validation.BindingResult;

public interface DeviceService {
    DeviceDocument add(DeviceRequest deviceRequest, BindingResult bindingResult);

    DevicesResponse getAllDevices(Integer pageNo, Integer pageSize);

    void update(String id, DeviceRequest deviceRequest, BindingResult bindingResult);

    void delete(String id);
}
