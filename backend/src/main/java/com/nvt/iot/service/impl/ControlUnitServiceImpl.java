package com.nvt.iot.service.impl;

import com.nvt.iot.document.ControlUnitDocument;
import com.nvt.iot.document.UserDocument;
import com.nvt.iot.exception.NotFoundCustomException;
import com.nvt.iot.exception.ValidationCustomException;
import com.nvt.iot.model.Creator;
import com.nvt.iot.payload.request.ControlUnitRequest;
import com.nvt.iot.payload.response.ControlUnitsResponse;
import com.nvt.iot.repository.ControlUnitRepository;
import com.nvt.iot.repository.UserRepository;
import com.nvt.iot.service.ControlUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
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

        UserDocument userDocument = userRepository.findById(controlUnitRequest.getCreatorId())
            .orElseThrow(
                () -> new NotFoundCustomException("User not found with id " + controlUnitRequest.getCreatorId())
            );
        var creator = Creator.builder()
            .id(userDocument.getId())
            .name(userDocument.getFullName())
            .build();

        var controlUnitDoc = ControlUnitDocument.builder()
            .kp(controlUnitRequest.getKp())
            .setpoint(controlUnitRequest.getSetpoint())
            .name(controlUnitRequest.getName())
            .kalmanFilter(controlUnitRequest.getKalmanFilter())
            .creator(creator)
            .createdAt(new Date(System.currentTimeMillis()))
            .updatedAt(new Date(System.currentTimeMillis()))
            .build();
        controlUnitRepository.save(controlUnitDoc);
    }

    @Override
    public ControlUnitsResponse getAllUnitControls(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<ControlUnitDocument> pagedResult = controlUnitRepository.findAll(pageable);

        List<ControlUnitDocument> controlUnitDocumentList = new ArrayList<>();

        if (pagedResult.hasContent()) {
            controlUnitDocumentList = pagedResult.getContent();
        }

        return ControlUnitsResponse.builder()
            .controlUnitDocumentList(controlUnitDocumentList)
            .currentPage(pagedResult.getNumber())
            .totalItems(pagedResult.getNumberOfElements())
            .totalPages(pagedResult.getTotalPages())
            .build();
    }

    @Override
    public ControlUnitDocument getUnitControlById(String id) {
        validateUnitControlIdFormat(id);
        return controlUnitRepository.findById(id)
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

        if (!controlUnitDoc.getCreator().getId().equals(controlUnitRequest.getCreatorId())) {
            var creator = userRepository.findById(controlUnitRequest.getCreatorId())
                .orElseThrow(
                    () -> new NotFoundCustomException("Creator not found with id " + controlUnitRequest.getCreatorId())
                );
            controlUnitDoc.setCreator(new Creator(creator.getId(), creator.getFullName()));
        }

        controlUnitDoc.setKp(controlUnitRequest.getKp());
        controlUnitDoc.setName(controlUnitRequest.getName());
        controlUnitDoc.setSetpoint(controlUnitRequest.getSetpoint());
        controlUnitDoc.setUpdatedAt(new Date(System.currentTimeMillis()));
        controlUnitDoc.setKalmanFilter(controlUnitRequest.getKalmanFilter());

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
