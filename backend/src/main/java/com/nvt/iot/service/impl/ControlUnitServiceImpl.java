package com.nvt.iot.service.impl;

import com.nvt.iot.document.ControlUnitDocument;
import com.nvt.iot.exception.ControlUnitNameAlreadyExistException;
import com.nvt.iot.exception.NotFoundCustomException;
import com.nvt.iot.exception.ValidationCustomException;
import com.nvt.iot.payload.request.ControlUnitRequest;
import com.nvt.iot.repository.ControlUnitRepository;
import com.nvt.iot.repository.UserRepository;
import com.nvt.iot.service.ControlUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ControlUnitServiceImpl implements ControlUnitService {
    private final ControlUnitRepository controlUnitRepository;
    private final UserRepository userRepository;

    @Override
    public void addUnitControl(ControlUnitRequest controlUnitRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationCustomException(bindingResult);
        }

        if (!userRepository.existsById(controlUnitRequest.getUserId())) {
            throw new NotFoundCustomException("User not found with id " + controlUnitRequest.getUserId());
        }

        if (controlUnitRepository.existsByName(controlUnitRequest.getName())) {
            throw new ControlUnitNameAlreadyExistException(controlUnitRequest.getName() + " already exist!");
        }

        Date time = new Date(System.currentTimeMillis());
        var controlUnitDoc = ControlUnitDocument.builder()
            .kp(controlUnitRequest.getKp())
            .setpoint(controlUnitRequest.getSetpoint())
            .name(controlUnitRequest.getName())
            .userId(controlUnitRequest.getUserId())
            .createdAt(time)
            .updatedAt(time)
            .build();
        controlUnitRepository.save(controlUnitDoc);
    }

    @Override
    public List<ControlUnitDocument> getAllUnitControlsByUserId(String userId) {
        return controlUnitRepository.findAllByUserId(userId);
    }

    @Override
    public ControlUnitDocument getUnitControlById(String id) {
        validateUnitControlIdFormat(id);
        return controlUnitRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundCustomException("Not found unit control with id " + id));
    }

    @Override
    public void update(String id, ControlUnitRequest controlUnitRequest, BindingResult bindingResult) {
        validateUnitControlIdFormat(id);
        if (bindingResult.hasErrors()) {
            throw new ValidationCustomException(bindingResult);
        }
        var controlUnitDoc = controlUnitRepository.findById(id)
            .orElseThrow(() -> new NotFoundCustomException("Not found control unit with id " + id));
        controlUnitDoc.setKp(controlUnitRequest.getKp());
        controlUnitDoc.setName(controlUnitRequest.getName());
        controlUnitDoc.setSetpoint(controlUnitRequest.getSetpoint());
        controlUnitDoc.setUpdatedAt(new Date(System.currentTimeMillis()));
        controlUnitRepository.save(controlUnitDoc);
    }

    @Override
    public void delete(String id) {
        validateUnitControlIdFormat(id);

        if (controlUnitRepository.existsById(id)) {
            controlUnitRepository.deleteById(id);
        } else {
            throw new NotFoundCustomException("Not found control unit with id " + id);
        }
    }

    private void validateUnitControlIdFormat(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationCustomException("Invalid control unit id");
        }
    }
}
