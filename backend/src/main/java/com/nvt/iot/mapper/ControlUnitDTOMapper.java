package com.nvt.iot.mapper;

import com.nvt.iot.document.ControlUnitDocument;
import com.nvt.iot.model.ControlUnit;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ControlUnitDTOMapper implements Function<ControlUnitDocument, ControlUnit> {
    @Override
    public ControlUnit apply(ControlUnitDocument controlUnitDocument) {
        return new ControlUnit(
            controlUnitDocument.getId(),
            controlUnitDocument.getName(),
            controlUnitDocument.getKp(),
            controlUnitDocument.getSetpoint()
        );
    }
}
