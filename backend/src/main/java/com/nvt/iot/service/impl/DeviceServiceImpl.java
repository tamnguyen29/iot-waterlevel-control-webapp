package com.nvt.iot.service.impl;

import com.nvt.iot.document.DeviceDocument;
import com.nvt.iot.exception.DeviceNameAlreadyExistException;
import com.nvt.iot.exception.NotFoundCustomException;
import com.nvt.iot.exception.ValidationCustomException;
import com.nvt.iot.payload.request.DeviceRequest;
import com.nvt.iot.payload.response.DevicesResponse;
import com.nvt.iot.repository.DeviceRepository;
import com.nvt.iot.service.DeviceService;
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
public class DeviceServiceImpl implements DeviceService {
    private final DeviceRepository deviceRepository;

    @Override
    public void add(DeviceRequest deviceRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationCustomException(bindingResult);
        }

        if (deviceRepository.existsByName(deviceRequest.getName())) {
            throw new DeviceNameAlreadyExistException("Device name " + deviceRequest.getName() + " already existed!");
        }

        var deviceDoc = DeviceDocument.builder()
            .name(deviceRequest.getName())
            .description(deviceRequest.getDescription())
            .createdAt(new Date(System.currentTimeMillis()))
            .updatedAt(new Date(System.currentTimeMillis()))
            .build();
        deviceRepository.save(deviceDoc);
    }

    @Override
    public DevicesResponse getAllDevices(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<DeviceDocument> pagedResult = deviceRepository.findAll(pageable);
        List<DeviceDocument> deviceDocumentList = new ArrayList<>();

        if (pagedResult.hasContent()) {
            deviceDocumentList = pagedResult.getContent();
        }

        return DevicesResponse.builder()
            .deviceList(deviceDocumentList)
            .currentPage(pagedResult.getNumber())
            .totalItems(pagedResult.getNumberOfElements())
            .totalPages(pagedResult.getTotalPages())
            .build();
    }

    @Override
    public void update(String id, DeviceRequest deviceRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationCustomException(bindingResult);
        }

        if (deviceRepository.existsByNameAndNotEqualId(deviceRequest.getName(), id)) {
            throw new DeviceNameAlreadyExistException("Duplicated name device " + deviceRequest.getName());
        }

        DeviceDocument deviceDocument = deviceRepository.findById(id)
            .orElseThrow(() -> new NotFoundCustomException("Not found device with id " + id));

        deviceDocument.setName(deviceRequest.getName());
        deviceDocument.setDescription(deviceRequest.getDescription());
        deviceDocument.setUpdatedAt(new Date(System.currentTimeMillis()));

        deviceRepository.save(deviceDocument);
    }

    @Override
    public void delete(String id) {
        if (deviceRepository.existsById(id)) {
            deviceRepository.existsById(id);
        } else {
            throw new NotFoundCustomException("Device not found with id " + id);
        }
    }
}
