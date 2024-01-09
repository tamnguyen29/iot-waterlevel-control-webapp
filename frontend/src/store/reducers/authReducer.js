import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  login: {
    currentUser: null,
    isFetching: false,
    error: false
  },
  register: {
    registerData: null,
    isFetching: false,
    error: false
  },
  logout: {
    isLogout: false
  }
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    loginStart: (state) => {
      state.login.isFetching = true;
    },
    loginSuccess: (state, action) => {
      state.login.isFetching = false;
      state.login.error = false;
      state.login.currentUser = action.payload;
      state.logout.isLogout = false;
    },
    loginFailed: (state) => {
      state.login.isFetching = false;
      state.login.error = true;
    },
    registerStart: (state) => {
      state.register.isFetching = true;
    },
    registerSuccess: (state, action) => {
      state.register.isFetching = false;
      state.register.error = false;
      state.register.registerData = action.payload;
    },
    registerFailed: (state) => {
      state.register.isFetching = false;
      state.register.error = true;
    },

    logoutStart: (state) => {
      state.logout.isLogout = true;
    },
    logoutSuccess: (state) => {
      state.logout.isLogout = false;
      state.login.currentUser = null;
    },
    logoutFailed: (state) => {
      state.logout.isLogout = false;
    }
  }
});

export default authSlice.reducer;
export const {
  loginStart,
  loginSuccess,
  loginFailed,
  registerStart,
  registerFailed,
  registerSuccess,
  logoutSuccess,
  logoutStart,
  logoutFailed
} = authSlice.actions;
