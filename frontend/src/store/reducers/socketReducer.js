import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  stompClient: null,
  isConnecting: false
};

const socketSlice = createSlice({
  name: 'socket',
  initialState,
  reducers: {
    connectSocketStart: (state) => {
      state.isConnecting = true;
    },
    connectSocketSuccess: (state, action) => {
      state.isConnecting = false;
      state.stompClient = action.payload;
    },
    connectSocketFailed: (state) => {
      state.isConnecting = false;
      state.stompClient = null;
    }
  }
});

export default socketSlice.reducer;
export const { connectSocketStart, connectSocketSuccess, connectSocketFailed } = socketSlice.actions;
