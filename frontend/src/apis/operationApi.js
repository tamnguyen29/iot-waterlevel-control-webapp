import { API_ROOT_URL } from 'utils/constants';
import { toast } from 'react-toastify';
import {
  startMeasurementBegin,
  startMeasurementFailed,
  startMeasurementSuccess,
  stopMeasurementBegin,
  stopMeasurementSuccess,
  stopMeasurementFailed
} from 'store/reducers/deviceReducer';

export const startMeasurement = async (jwtAxios, dispatch, deviceId, userId, controlUnitId) => {
  try {
    dispatch(startMeasurementBegin());
    await jwtAxios.get(`${API_ROOT_URL}/api/operation/start-measurement/${deviceId}`, {
      params: {
        controlUnitId: controlUnitId,
        userId: userId
      }
    });
    dispatch(startMeasurementSuccess());
  } catch (error) {
    dispatch(startMeasurementFailed());
    if (error.code === 'ERR_NETWORK') {
      toast.error(error.message);
    } else {
      toast.error(error.response.data.message);
    }
  }
};

export const stopMeasurement = async (jwtAxios, dispatch, deviceId, userId) => {
  try {
    dispatch(stopMeasurementBegin());
    await jwtAxios.get(`${API_ROOT_URL}/api/operation/stop-measurement/${deviceId}`, {
      params: {
        userId: userId
      }
    });
    dispatch(stopMeasurementSuccess());
  } catch (error) {
    dispatch(stopMeasurementFailed());
    if (error.code === 'ERR_NETWORK') {
      toast.error(error.message);
    } else {
      toast.error(error.response.data.message);
    }
  }
};
