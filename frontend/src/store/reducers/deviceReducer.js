import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  connectedDevices: [],
  deviceAction: {
    startMeasurement: {
      isDisable: false,
      isStarting: false,
      isSuccess: false,
      isFailed: false
    },
    stopMeasurement: {
      isDisable: true,
      isStarting: false,
      isSuccess: false,
      isFailed: false
    },
    pumpOut: {
      isStarting: false,
      isSuccess: false,
      isFailed: false
    },
    restartProcess: {
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
      state.deviceAction.startMeasurement.isDisable = true;
      state.deviceAction.stopMeasurement.isDisable = false;
    },
    startMeasurementFailed: (state) => {
      state.deviceAction.startMeasurement.isStarting = false;
      state.deviceAction.startMeasurement.isSuccess = false;
      state.deviceAction.startMeasurement.isFailed = true;
      state.deviceAction.startMeasurement.isDisable = false;
      state.deviceAction.stopMeasurement.isDisable = true;
    },
    stopMeasurementBegin: (state) => {
      state.deviceAction.stopMeasurement.isStarting = true;
    },
    stopMeasurementSuccess: (state) => {
      state.deviceAction.stopMeasurement.isStarting = false;
      state.deviceAction.stopMeasurement.isSuccess = true;
      state.deviceAction.stopMeasurement.isFailed = false;
      state.deviceAction.stopMeasurement.isDisable = true;
    },
    stopMeasurementFailed: (state) => {
      state.deviceAction.stopMeasurement.isStarting = false;
      state.deviceAction.stopMeasurement.isSuccess = false;
      state.deviceAction.stopMeasurement.isFailed = true;
      state.deviceAction.stopMeasurement.isDisable = false;
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
    },
    restartProcessBegin: (state) => {
      state.deviceAction.restartProcess.isStarting = true;
      state.deviceAction.startMeasurement.isDisable = true;
      state.deviceAction.stopMeasurement.isDisable = true;
      state.deviceAction.restartProcess.isSuccess = false;
      state.deviceAction.restartProcess.isFailed = false;
    },
    restartProcessSuccess: (state) => {
      state.deviceAction.restartProcess.isStarting = false;
      state.deviceAction.restartProcess.isSuccess = true;
      state.deviceAction.restartProcess.isFailed = false;
      state.deviceAction.startMeasurement.isDisable = false;
      state.deviceAction.stopMeasurement.isDisable = true;
    },
    restartProcessFailed: (state) => {
      state.deviceAction.restartProcess.isStarting = false;
      state.deviceAction.restartProcess.isSuccess = false;
      state.deviceAction.restartProcess.isFailed = true;
      state.deviceAction.startMeasurement.isDisable = true;
    },
    resetAllState: (state) => {
      state.deviceAction.startMeasurement.isStarting = false;
      state.deviceAction.startMeasurement.isSuccess = false;
      state.deviceAction.startMeasurement.isFailed = false;
      state.deviceAction.startMeasurement.isDisable = false;
      state.deviceAction.stopMeasurement.isStarting = false;
      state.deviceAction.stopMeasurement.isSuccess = false;
      state.deviceAction.stopMeasurement.isFailed = false;
      state.deviceAction.stopMeasurement.isDisable = true;
      state.deviceAction.pumpOut.isStarting = false;
      state.deviceAction.pumpOut.isSuccess = false;
      state.deviceAction.pumpOut.isFailed = false;
      state.deviceAction.restartProcess.isStarting = false;
      state.deviceAction.restartProcess.isSuccess = false;
      state.deviceAction.restartProcess.isFailed = false;
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
  sendPumpOutSignalSuccess,
  restartProcessBegin,
  restartProcessFailed,
  restartProcessSuccess,
  resetAllState
} = deviceSlice.actions;
