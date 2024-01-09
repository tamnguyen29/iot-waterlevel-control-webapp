import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  notificationList: []
};

const notificationSlice = createSlice({
  name: 'notification',
  initialState,
  reducers: {
    setNotificationList: (state, action) => {
      state.notificationList = action.payload;
    },
    addNotificationItem: (state, action) => {
      const notificationItem = action.payload;
      const newNotificationList = state.notificationList;
      newNotificationList.unshift(notificationItem);
      if (newNotificationList.length > 5) {
        newNotificationList.pop();
      }
      state.notificationList = newNotificationList;
    }
  }
});

export default notificationSlice.reducer;

export const { setNotificationList, addNotificationItem } = notificationSlice.actions;
