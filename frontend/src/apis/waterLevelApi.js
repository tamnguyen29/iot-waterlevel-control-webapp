import { API_ROOT_URL } from 'utils/constants';
import { toast } from 'react-toastify';
import { deleteWaterLevelDataStart, deleteWaterLevelDataFailed, deleteWaterLevelDataSuccess } from 'store/reducers/waterLevelReducer';

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

export const deleteWaterLevelData = async (dispatch, jwtAxios, userId, controlUnitId, deviceId) => {
  try {
    dispatch(deleteWaterLevelDataStart());
    const res = await jwtAxios.delete(`${API_ROOT_URL}/api/water-level/delete`, {
      params: {
        userId: userId,
        deviceId: deviceId,
        controlUnitId: controlUnitId
      }
    });
    dispatch(deleteWaterLevelDataSuccess());
    toast.success('Delete water level data successfully!');
    return res.data.data;
  } catch (error) {
    dispatch(deleteWaterLevelDataFailed());
    if (error.code === 'ERR_NETWORK') {
      toast.error(error.message);
    } else {
      toast.error('Delete water level data failed!');
    }
  }
};
