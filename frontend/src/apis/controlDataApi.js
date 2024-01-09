import { API_ROOT_URL } from 'utils/constants';
import { toast } from 'react-toastify';

export const getAllControlData = async (jwtAxios, userId) => {
  try {
    const res = await jwtAxios.get(`${API_ROOT_URL}/api/data-control`, {
      params: {
        userId: userId
      }
    });
    return res.data.data;
  } catch (error) {
    if (error.code === 'ERR_NETWORK') {
      toast.error(error.message);
    } else {
      toast.error('Get control data failed!');
    }
  }
};
