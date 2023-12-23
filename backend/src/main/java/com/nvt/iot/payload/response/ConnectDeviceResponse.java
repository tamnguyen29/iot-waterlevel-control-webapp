package com.nvt.iot.payload.response;

import com.nvt.iot.document.ConnectedDeviceDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectDeviceResponse {
    private ConnectedDeviceDocument device;
    private Date connectDeviceTime;
}
