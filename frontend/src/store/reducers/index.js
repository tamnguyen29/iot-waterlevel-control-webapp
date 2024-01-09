// third-party
import { combineReducers } from 'redux';
// project import
import menuReducer from './menuReducer';
import authReducer from './authReducer';
import socketReducer from './socketReducer';
import userReducer from './userReducer';
import deviceReducer from './deviceReducer';
import waterLevelReducer from './waterLevelReducer';
import controlUnitReducer from './controlUnitReducer';
import notificationReducer from './notificationReducer';
// ==============================|| COMBINE REDUCERS ||============================== //

const reducers = combineReducers({
  menu: menuReducer,
  auth: authReducer,
  socket: socketReducer,
  user: userReducer,
  device: deviceReducer,
  waterLevel: waterLevelReducer,
  controlUnit: controlUnitReducer,
  notification: notificationReducer
});

export default reducers;
