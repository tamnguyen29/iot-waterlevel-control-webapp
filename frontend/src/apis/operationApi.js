import { API_ROOT_URL } from 'utils/constants';
import { toast } from 'react-toastify';
import {
  startMeasurementBegin,
  startMeasurementFailed,
  startMeasurementSuccess,
  stopMeasurementBegin,
  stopMeasurementSuccess,
  stopMeasurementFailed,
  restartProcessBegin,
  restartProcessFailed
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
    toast.success('The control process has been started!');
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
    toast.info('The control process has been stopped!');
  } catch (error) {
    dispatch(stopMeasurementFailed());
    if (error.code === 'ERR_NETWORK') {
      toast.error(error.message);
    } else {
      toast.error(error.response.data.message);
    }
  }
};

export const restartProcess = async (jwtAxios, dispatch, deviceId, userId) => {
  try {
    dispatch(restartProcessBegin());
    await jwtAxios.get(`${API_ROOT_URL}/api/operation/reset-process/${deviceId}`, {
      params: {
        userId: userId
      }
    });
  } catch (error) {
    dispatch(restartProcessFailed());
    if (error.code === 'ERR_NETWORK') {
      toast.error(error.message);
    } else {
      toast.error('Restart control process failed!');
    }
  }
};
