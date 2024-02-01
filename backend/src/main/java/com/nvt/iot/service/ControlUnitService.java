package com.nvt.iot.service;

import com.nvt.iot.document.ControlUnitDocument;
import com.nvt.iot.payload.request.ControlUnitRequest;
import org.springframework.validation.BindingResult;

import java.util.List;


public interface ControlUnitService {
    void addUnitControl(ControlUnitRequest controlUnitRequest, BindingResult bindingResult);

    List<ControlUnitDocument> getAllUnitControlsByUserId(String UserId);

    ControlUnitDocument getUnitControlById(String id);

    void update(String id, ControlUnitRequest controlUnitRequest, BindingResult bindingResult);

    void delete(String id, String userId);
}
