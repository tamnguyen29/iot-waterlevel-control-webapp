package com.nvt.iot.payload.response;

import com.nvt.iot.document.ControlUnitDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ControlUnitsResponse {
    private List<ControlUnitDocument> controlUnitDocumentList;
    private int currentPage;
    private int totalItems;
    private int totalPages;
}
