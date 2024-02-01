import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  data: {
    value: 0,
    time: 0
  },
  deleteData: {
    isFetching: false,
    isDeleteSuccess: false,
    isDeleteFailed: false
  }
};

const waterLevelSlice = createSlice({
  name: 'waterLevel',
  initialState,
  reducers: {
    setCurrentWaterLevelData: (state, action) => {
      state.data = action.payload;
    },
    deleteWaterLevelDataStart: (state) => {
      state.deleteData.isFetching = true;
      state.deleteData.isDeleteSuccess = false;
      state.deleteData.isDeleteFailed = false;
    },
    deleteWaterLevelDataSuccess: (state) => {
      state.deleteData.isFetching = false;
      state.deleteData.isDeleteSuccess = true;
      state.deleteData.isDeleteFailed = false;
    },
    deleteWaterLevelDataFailed: (state) => {
      state.deleteData.isFetching = false;
      state.deleteData.isDeleteSuccess = false;
      state.deleteData.isDeleteFailed = true;
    }
  }
});

export default waterLevelSlice.reducer;
export const { setCurrentWaterLevelData, deleteWaterLevelDataFailed, deleteWaterLevelDataStart, deleteWaterLevelDataSuccess } =
  waterLevelSlice.actions;
