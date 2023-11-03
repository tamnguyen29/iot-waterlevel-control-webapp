package com.nvt.iot.payload.response;

import com.nvt.iot.document.DeviceDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DevicesResponse {
    private List<DeviceDocument> deviceList;
    private int currentPage;
    private int totalItems;
    private int totalPages;
}
