import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  connectedDevices: [],
  deviceAction: {
    startMeasurement: {
      isStarting: false,
      isSuccess: false,
      isFailed: false
    },
    stopMeasurement: {
      isStarting: false,
      isSuccess: false,
      isFailed: false
    },
    pumpOut: {
      isStarting: false,
      isSuccess: false,
      isFailed: false
    }
  }
};

const deviceSlice = createSlice({
  name: 'device',
  initialState,
  reducers: {
    setConnectedDevices: (state, action) => {
      state.connectedDevices = action.payload;
    },
    startMeasurementBegin: (state) => {
      state.deviceAction.startMeasurement.isStarting = true;
    },
    startMeasurementSuccess: (state) => {
      state.deviceAction.startMeasurement.isStarting = false;
      state.deviceAction.startMeasurement.isSuccess = true;
      state.deviceAction.startMeasurement.isFailed = false;
    },
    startMeasurementFailed: (state) => {
      state.deviceAction.startMeasurement.isStarting = false;
      state.deviceAction.startMeasurement.isSuccess = false;
      state.deviceAction.startMeasurement.isFailed = true;
    },
    stopMeasurementBegin: (state) => {
      state.deviceAction.stopMeasurement.isStarting = true;
    },
    stopMeasurementSuccess: (state) => {
      state.deviceAction.stopMeasurement.isStarting = false;
      state.deviceAction.stopMeasurement.isSuccess = true;
      state.deviceAction.stopMeasurement.isFailed = false;
    },
    stopMeasurementFailed: (state) => {
      state.deviceAction.stopMeasurement.isStarting = false;
      state.deviceAction.stopMeasurement.isSuccess = false;
      state.deviceAction.stopMeasurement.isFailed = true;
    },
    sendPumpOutSignalBegin: (state) => {
      state.deviceAction.pumpOut.isStarting = true;
    },
    sendPumpOutSignalFailed: (state) => {
      state.deviceAction.pumpOut.isStarting = false;
      state.deviceAction.pumpOut.isFailed = true;
    },
    sendPumpOutSignalSuccess: (state) => {
      state.deviceAction.pumpOut.isStarting = false;
      state.deviceAction.pumpOut.isSuccess = true;
    }
  }
});

export default deviceSlice.reducer;
export const {
  setConnectedDevices,
  startMeasurementBegin,
  startMeasurementSuccess,
  startMeasurementFailed,
  stopMeasurementBegin,
  stopMeasurementSuccess,
  stopMeasurementFailed,
  sendPumpOutSignalBegin,
  sendPumpOutSignalFailed,
  sendPumpOutSignalSuccess
} = deviceSlice.actions;
