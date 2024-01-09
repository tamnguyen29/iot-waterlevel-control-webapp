import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  data: {
    value: 0,
    time: 0
  }
};

const waterLevelSlice = createSlice({
  name: 'waterLevel',
  initialState,
  reducers: {
    setCurrentWaterLevelData: (state, action) => {
      state.data = action.payload;
    }
  }
});

export default waterLevelSlice.reducer;
export const { setCurrentWaterLevelData } = waterLevelSlice.actions;
