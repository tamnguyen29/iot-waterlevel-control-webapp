import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import { API_ROOT_URL } from 'utils/constants';
import { loginSuccess } from 'store/reducers/authReducer';

export const refreshTokenAPI = async (refreshToken) => {
  try {
    const res = await axios.post(`${API_ROOT_URL}/api/auth/refresh-token`, {}, { headers: { Authorization: `Bearer ${refreshToken}` } });
    return res.data;
  } catch (error) {
    console.log(error);
  }
};

export const createJWTAxios = (currentUser, dispatch) => {
  const instance = axios.create();
  instance.interceptors.request.use(
    async (config) => {
      let date = new Date();
      const decodeToken = jwtDecode(currentUser?.accessToken);
      if (decodeToken.exp < date.getTime() / 1000) {
        const data = await refreshTokenAPI(currentUser?.refreshToken);
        const refreshUser = {
          ...currentUser,
          accessToken: data.accessToken,
          refreshToken: data.refreshToken
        };
        dispatch(loginSuccess(refreshUser));
        config.headers['Authorization'] = `Bearer ${data.accessToken}`;
      } else {
        config.headers['Authorization'] = `Bearer ${currentUser?.accessToken}`;
      }
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );
  return instance;
};
