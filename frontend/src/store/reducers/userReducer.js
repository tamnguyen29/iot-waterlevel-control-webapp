import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  onlineUsers: [],
  connectingDevice: {
    current: null,
    isConnecting: false,
    isConnectFailed: false,
    isStopConnecting: false,
    isStopConnectingFailed: false
  }
};

const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    setOnlineUsers: (state, action) => {
      state.onlineUsers = action.payload;
    },
    connectDeviceStart: (state) => {
      state.connectingDevice.isConnecting = true;
    },
    connectDeviceSuccess: (state, action) => {
      state.connectingDevice.isConnecting = false;
      state.connectingDevice.isConnectFailed = false;
      state.connectingDevice.current = action.payload;
    },
    connectDeviceFailed: (state) => {
      state.connectingDevice.isConnecting = false;
      state.connectingDevice.isConnectFailed = true;
    },
    stopConnectDeviceStart: (state) => {
      state.connectingDevice.isStopConnecting = true;
    },
    stopConnectDeviceSuccess: (state) => {
      state.connectingDevice.isStopConnecting = false;
      state.connectingDevice.isStopConnectingFailed = false;
      state.connectingDevice.current = null;
    },
    stopConnectDeviceFailed: (state) => {
      state.connectingDevice.isStopConnecting = false;
      state.connectingDevice.isStopConnectingFailed = true;
    }
  }
});

export default userSlice.reducer;
export const {
  setOnlineUsers,
  connectDeviceStart,
  connectDeviceSuccess,
  connectDeviceFailed,
  stopConnectDeviceStart,
  stopConnectDeviceSuccess,
  stopConnectDeviceFailed
} = userSlice.actions;
