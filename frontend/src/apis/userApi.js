import { API_ROOT_URL } from 'utils/constants';
import { toast } from 'react-toastify';
import { uploadImage, deleteImage } from 'config/firebase/firebaseAction';
import { loginSuccess } from 'store/reducers/authReducer';

export const getAllUsersAPI = async (jwtAxios) => {
  try {
    const res = await jwtAxios.get(`${API_ROOT_URL}/api/user`);
    return res.data.data;
  } catch (error) {
    toast.error('Cannot get list all users');
  }
};

export const getUserAmount = async (jwtAxios) => {
  try {
    const res = await jwtAxios.get(`${API_ROOT_URL}/api/user/amount`);
    return res.data.data;
  } catch (error) {
    return 0;
  }
};

export const updateInfoUser = async (jwtAxios, dispatch, loginUser, newInfo) => {
  try {
    const imageUrl = await uploadImage(newInfo.imageFile, loginUser.user.avatar);
    if (imageUrl) {
      const updateUserRequest = {
        fullName: newInfo.fullName,
        email: newInfo.email,
        phoneNumber: newInfo.phoneNumber,
        avatar: imageUrl
      };
      const res = await jwtAxios.put(`${API_ROOT_URL}/api/user/update-info/${loginUser.user.id}`, updateUserRequest);
      if (loginUser.user.avatar !== 'DEFAULT') {
        await deleteImage(loginUser.user.avatar);
      }
      const updatedUser = {
        ...loginUser,
        user: res.data.data
      };
      dispatch(loginSuccess(updatedUser));
      toast.success('Update your information successfully!');
    } else {
      toast.error('Upload your image failed!');
    }
  } catch (error) {
    toast.error('Update information failed!');
  }
};
