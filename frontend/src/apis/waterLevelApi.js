import { API_ROOT_URL } from 'utils/constants';
import { toast } from 'react-toastify';

export const getWaterLevelData = async (jwtAxios, userId, controlUnitId, deviceId) => {
  try {
    const res = await jwtAxios.get(`${API_ROOT_URL}/api/water-level`, {
      params: {
        userId: userId,
        deviceId: deviceId,
        controlUnitId: controlUnitId
      }
    });
    return res.data.data;
  } catch (error) {
    if (error.code === 'ERR_NETWORK') {
      toast.error(error.message);
    } else {
      toast.error('Get water level data failed!');
    }
  }
};
