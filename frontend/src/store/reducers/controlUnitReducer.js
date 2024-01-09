import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  controlUnitList: {
    current: [],
    isFetching: false,
    isFailed: false
  },
  controlUnitAction: {
    isAdding: false,
    isDeleting: false,
    isEditing: false
  }
};
const controlUnitSlice = createSlice({
  name: 'controlUnit',
  initialState,
  reducers: {
    getAllControlUnitStart: (state) => {
      state.controlUnitList.isFetching = true;
    },
    getAllControlUnitSuccess: (state, action) => {
      state.controlUnitList.isFetching = false;
      state.controlUnitList.current = action.payload;
      state.controlUnitList.isFailed = false;
    },
    getAllControlUnitFailed: (state) => {
      state.controlUnitList.isFailed = true;
      state.controlUnitList.isFetching = false;
    },
    deleteControlUnitStart: (state) => {
      state.controlUnitAction.isDeleting = true;
    },
    deleteControlUnitSuccess: (state) => {
      state.controlUnitAction.isDeleting = false;
    },
    deleteControlUnitFailed: (state) => {
      state.controlUnitAction.isDeleting = false;
    }
  }
});
export const {
  getAllControlUnitFailed,
  getAllControlUnitStart,
  getAllControlUnitSuccess,
  deleteControlUnitStart,
  deleteControlUnitSuccess,
  deleteControlUnitFailed
} = controlUnitSlice.actions;
export default controlUnitSlice.reducer;
