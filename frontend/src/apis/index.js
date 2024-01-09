import { authenticateAPI, registerAPI, logoutAPI } from './authApi';
import { getAllUsersAPI, getUserAmount, updateInfoUser } from './userApi';
import { getAllControlUnit, addControlUnit, deleteControlUnit, updateControlUnit } from './controlUnitApi';
import { connectToDevice, stopConnectToDevice, sendPumpOutSignal } from './deviceApi';
import { startMeasurement, stopMeasurement } from './operationApi';
import { getWaterLevelData } from './waterLevelApi';
import { getAllControlData } from './controlDataApi';

const services = {
  authenticateAPI,
  registerAPI,
  getAllUsersAPI,
  getAllControlUnit,
  addControlUnit,
  deleteControlUnit,
  updateControlUnit,
  connectToDevice,
  stopConnectToDevice,
  startMeasurement,
  stopMeasurement,
  getWaterLevelData,
  getAllControlData,
  logoutAPI,
  sendPumpOutSignal,
  getUserAmount,
  updateInfoUser
};

export default services;
