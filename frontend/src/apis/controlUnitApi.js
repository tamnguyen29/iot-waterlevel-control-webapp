import { API_ROOT_URL } from 'utils/constants';
import { getAllControlUnitStart, getAllControlUnitFailed, getAllControlUnitSuccess } from 'store/reducers/controlUnitReducer';
import { toast } from 'react-toastify';

export const getAllControlUnit = async (userId, jwtAxios, dispatch) => {
  dispatch(getAllControlUnitStart());
  try {
    const res = await jwtAxios.get(`${API_ROOT_URL}/api/unit-control`, {
      params: {
        userId: userId
      }
    });
    dispatch(getAllControlUnitSuccess(res.data.data));
  } catch (error) {
    dispatch(getAllControlUnitFailed());
  }
};

export const addControlUnit = async (userId, jwtAxios, controlUnitData, dispatch) => {
  try {
    await jwtAxios.post(`${API_ROOT_URL}/api/unit-control/add`, controlUnitData);
    await getAllControlUnit(userId, jwtAxios, dispatch);
    toast.success('Add control parameter successfully!');
  } catch (error) {
    if (error.code === 'ERR_NETWORK') {
      toast.error(error.message);
    } else {
      toast.error(error.response.data.message);
    }
  }
};

export const deleteControlUnit = async (userId, jwtAxios, dispatch, controlUnitId) => {
  try {
    await jwtAxios.delete(`${API_ROOT_URL}/api/unit-control/delete/${controlUnitId}`);
    await getAllControlUnit(userId, jwtAxios, dispatch);
    toast.success('Delete control parameter successfully!');
  } catch (error) {
    if (error.code === 'ERR_NETWORK') {
      toast.error(error.message);
    } else {
      toast.error(error.response.data.message);
    }
  }
};

export const updateControlUnit = async (userId, jwtAxios, dispatch, controlUnitId, data) => {
  try {
    await jwtAxios.put(`${API_ROOT_URL}/api/unit-control/update/${controlUnitId}`, data);
    await getAllControlUnit(userId, jwtAxios, dispatch);
    toast.success('Update control parameter successfully!');
  } catch (error) {
    if (error.code === 'ERR_NETWORK') {
      toast.error(error.message);
    } else {
      toast.error(error.response.data.message);
    }
  }
};
