import { API_ROOT_URL } from 'utils/constants';
import { connectDeviceStart, connectDeviceSuccess, connectDeviceFailed } from 'store/reducers/userReducer';
import { stopConnectDeviceStart, stopConnectDeviceFailed, stopConnectDeviceSuccess } from 'store/reducers/userReducer';
import { sendPumpOutSignalBegin, sendPumpOutSignalSuccess, sendPumpOutSignalFailed } from 'store/reducers/deviceReducer';
import { toast } from 'react-toastify';
import { resetAllState } from 'store/reducers/deviceReducer';

export const connectToDevice = async (jwtAxios, dispatch, deviceId, userId) => {
  try {
    dispatch(connectDeviceStart());
    const res = await jwtAxios.get(`${API_ROOT_URL}/api/device/connect/${deviceId}`, {
      params: {
        userId: userId
      }
    });
    const data = res.data.data;
    console.log('Data connect device: ', data);
    dispatch(connectDeviceSuccess(data));
    dispatch(resetAllState());
    toast.success(`Using ${data.device.name} successfully!`);
  } catch (error) {
    if (error.code === 'ERR_NETWORK') {
      toast.error(error.message);
    } else {
      toast.error(error.response.data.message);
    }
    dispatch(connectDeviceFailed());
  }
};

export const stopConnectToDevice = async (jwtAxios, dispatch, deviceId, userId) => {
  try {
    dispatch(stopConnectDeviceStart());
    await jwtAxios.get(`${API_ROOT_URL}/api/device/stop-connect/${deviceId}`, {
      params: {
        userId: userId
      }
    });
    dispatch(stopConnectDeviceSuccess());
  } catch (error) {
    dispatch(stopConnectDeviceFailed());
    toast.error('Stop connect device failed!');
  }
};

export const sendPumpOutSignal = async (jwtAxios, dispatch, userId, deviceId, percentage) => {
  dispatch(sendPumpOutSignalBegin());
  try {
    await jwtAxios.post(`${API_ROOT_URL}/api/device/pump-out`, {
      userId: userId,
      deviceId: deviceId,
      percentage: percentage
    });
    dispatch(sendPumpOutSignalSuccess());
  } catch (error) {
    dispatch(sendPumpOutSignalFailed());
    toast.error('Send pump out signal failed!');
  }
};
