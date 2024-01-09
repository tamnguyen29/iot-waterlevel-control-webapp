import axios from 'axios';
import { API_ROOT_URL } from 'utils/constants';
import {
  loginStart,
  loginFailed,
  loginSuccess,
  registerStart,
  registerSuccess,
  registerFailed,
  logoutFailed,
  logoutStart,
  logoutSuccess
} from 'store/reducers/authReducer';
import { stopConnectDeviceSuccess } from 'store/reducers/userReducer';
import { toast } from 'react-toastify';

export const authenticateAPI = async (user, dispatch, navigate) => {
  try {
    dispatch(loginStart());
    const res = await axios.post(`${API_ROOT_URL}/api/auth/authenticate`, user);
    dispatch(loginSuccess(res.data.data));
    navigate('/');
    toast.success('Login successfully!');
  } catch (error) {
    if (error.code === 'ERR_NETWORK') {
      toast.error(error.message);
    } else {
      toast.warning('Your email or password is incorrect, try again!');
      dispatch(loginFailed());
    }
  }
};

export const registerAPI = async (registerInfo, dispatch, navigate) => {
  try {
    dispatch(registerStart());
    const res = await axios.post(`${API_ROOT_URL}/api/auth/register`, registerInfo);
    dispatch(registerSuccess(res.data.data));
    toast.success('Register account successfully!');
    navigate('/login');
  } catch (error) {
    if (error.code === 'ERR_NETWORK') {
      toast.error(error.message);
    } else if (error.code === 'ERR_BAD_REQUEST') {
      toast.warning(error.response.data.message);
      dispatch(registerFailed());
    }
  }
};

export const logoutAPI = async (jwtAxios, dispatch, userId) => {
  try {
    dispatch(logoutStart());
    await jwtAxios.post(`${API_ROOT_URL}/api/auth/logout/${userId}`);
    dispatch(logoutSuccess());
    dispatch(stopConnectDeviceSuccess());
  } catch (error) {
    dispatch(logoutFailed());
    if (error.code === 'ERR_NETWORK') {
      toast.error(error.message);
    } else {
      toast.warning('Logout failed!');
    }
  }
};
