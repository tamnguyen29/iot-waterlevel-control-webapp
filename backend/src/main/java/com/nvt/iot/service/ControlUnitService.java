package com.nvt.iot.service;

import com.nvt.iot.document.ControlUnitDocument;
import com.nvt.iot.payload.request.ControlUnitRequest;
import com.nvt.iot.payload.response.ControlUnitsResponse;
import org.springframework.validation.BindingResult;


public interface ControlUnitService {
    void addUnitControl(ControlUnitRequest controlUnitRequest, BindingResult bindingResult);
    ControlUnitsResponse getAllUnitControls(Integer pageNo, Integer pageSize);

    ControlUnitDocument getUnitControlById(String id);
    void update(String id, ControlUnitRequest controlUnitRequest, BindingResult bindingResult);
    void delete(String id);
}
