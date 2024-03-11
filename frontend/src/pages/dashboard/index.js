/* eslint-disable react-hooks/exhaustive-deps */
import { useEffect, useState } from 'react';
import { over } from 'stompjs';
import SockJS from 'sockjs-client/dist/sockjs';
import { useSelector, useDispatch } from 'react-redux';
import { API_ROOT_URL, PRIVATE_ROOM, COMMON_ROOM, ERROR_ROOM } from 'utils/constants';
import { setOnlineUsers, stopConnectDeviceSuccess } from 'store/reducers/userReducer';
import { setConnectedDevices, restartProcessSuccess } from 'store/reducers/deviceReducer';
import { setCurrentWaterLevelData } from 'store/reducers/waterLevelReducer';
// material-ui
import { Box, Grid, Typography, CircularProgress } from '@mui/material';
// project import
import DevicesDisplay from './devices-display';
import ControlDashboard from './control-dashboard';
import SystemAnalytic from 'components/cards/statistics/SystemAnalytic';
import OnlineUsersImage from 'assets/images/users/online_users.png';
import TotalUsersImage from 'assets/images/users/total_users.png';
import ConnectedDevicesImage from 'assets/images/devices/connected_devices.png';
import AvailableDevicesImage from 'assets/images/devices/available_devices.png';
import services from 'apis/index';
import { createJWTAxios } from 'apis/createInstance';
import { addNotificationItem } from 'store/reducers/notificationReducer';
import { toast } from 'react-toastify';
import { resetAllState } from 'store/reducers/deviceReducer';
import { startMeasurementSuccess, startMeasurementFailed } from 'store/reducers/deviceReducer';
// ==============================|| DASHBOARD - DEFAULT ||============================== //
var stompClientRef = {
  current: null
};
const DashboardDefault = () => {
  const loginUser = useSelector((state) => state.auth.login.currentUser);
  const logout = useSelector((state) => state.auth.logout.isLogout);
  const connectedDeviceList = useSelector((state) => state.device.connectedDevices);
  const onlineUserList = useSelector((state) => state.user.onlineUsers);
  // const notificationList = useSelector((state) => state.notification.notificationList);
  const [systemUserAmount, setSystemUserAmount] = useState(0);
  const currentUsingDevice = useSelector((state) => state.user.connectingDevice.current);
  const dispatch = useDispatch();
  const jwtAxios = createJWTAxios(loginUser, dispatch);
  useEffect(() => {
    if (!stompClientRef.current || !stompClientRef.current.connected) {
      connectWebsocket();
    }
    // eslint-disable-next-line
  }, []);

  const getSystemUserAmount = async () => {
    const fetchSysUserAmount = await services.getUserAmount(jwtAxios);
    setSystemUserAmount(fetchSysUserAmount);
  };
  useEffect(() => {
    getSystemUserAmount();
    if (logout) {
      if (stompClientRef.current && stompClientRef.current.connected) {
        stompClientRef.current.disconnect();
        stompClientRef.current = null;
      }
    }
  }, [logout]);
  const connectWebsocket = () => {
    let sock = new SockJS(`${API_ROOT_URL}/ws?clientId=${loginUser.user.id}&clientType=USER`);
    stompClientRef.current = over(sock);
    stompClientRef.current.connect({}, onConnected, onError);
  };
  const onConnected = () => {
    stompClientRef.current.subscribe(COMMON_ROOM, onMembersMessageReceived);
    stompClientRef.current.subscribe(PRIVATE_ROOM, onPrivateMessageReceived);
    stompClientRef.current.subscribe(ERROR_ROOM, onErrorMessageReceived);
    stompClientRef.current.send('/app/member-connect', {}, null);
  };
  const onMembersMessageReceived = (payload) => {
    try {
      const receivedMessage = JSON.parse(payload.body);
      switch (receivedMessage.action) {
        case 'SEND_LIST_CONNECTED_USER':
          dispatch(setOnlineUsers(receivedMessage.content));
          break;
        case 'SEND_LIST_CONNECTED_DEVICE':
          dispatch(setConnectedDevices(receivedMessage.content));
          checkExistCurrentUsingDevice(receivedMessage.content);
          break;
        case 'NOTIFICATION':
          dispatch(addNotificationItem({ ...receivedMessage.content, time: receivedMessage.time }));
          break;
        default:
          break;
      }
    } catch (error) {
      console.log('Please reload this page!');
    }
  };

  const onPrivateMessageReceived = (payload) => {
    try {
      const receivedMessage = JSON.parse(payload.body);
      switch (receivedMessage.action) {
        case 'SEND_LIST_CONNECTED_DEVICE':
          dispatch(setConnectedDevices(receivedMessage.content));
          checkExistCurrentUsingDevice(receivedMessage.content);
          break;
        case 'SEND_WATER_LEVEL_DATA':
          console.log('SEND_WATER_LEVEL_DATA', receivedMessage.content);
          dispatch(setCurrentWaterLevelData(receivedMessage.content));
          break;
        case 'DEVICE_DISCONNECT_UNEXPECTED':
          dispatch(stopConnectDeviceSuccess());
          dispatch(resetAllState());
          break;
        case 'NOTIFICATION':
          dispatch(addNotificationItem({ ...receivedMessage.content, time: receivedMessage.time }));
          break;
        case 'RESTART_CONTROL_PROCESS':
          if (receivedMessage.content === 'FINISH_RESET') {
            dispatch(restartProcessSuccess());
            toast.success('Restart control process success!');
          }
          break;
        case 'START_MEASUREMENT':
          if (receivedMessage.content) {
            dispatch(startMeasurementSuccess());
            toast.success('Start the control process successfully!');
          } else {
            dispatch(startMeasurementFailed());
            toast.error('Start process failed! Setpoint must be greater than current water level!');
          }
          break;
        default:
          break;
      }
    } catch (error) {
      console.log('Cannot receive information from server');
    }
  };

  const checkExistCurrentUsingDevice = (listDevice) => {
    if (currentUsingDevice) {
      const device = listDevice.find((device) => device.usingStatus === 'UNAVAILABLE' && device.currentUsingUser.id === loginUser.user.id);
      if (!device) {
        dispatch(stopConnectDeviceSuccess());
      }
    }
  };

  const onErrorMessageReceived = (payload) => {
    const receivedMessage = JSON.parse(payload.body);
    console.log('Error message: ', receivedMessage);
  };

  const onError = () => {
    console.log('stompClient', stompClientRef.current);
    console.log('Error connect websocket');
    if (currentUsingDevice) {
      dispatch(stopConnectDeviceSuccess());
    }
  };

  return (
    <Grid container rowSpacing={4.5} columnSpacing={2.75}>
      {/* row 1 */}
      <Grid item xs={12} sx={{ mb: -2.25 }}>
        <Typography variant="h5">Analytics</Typography>
      </Grid>
      <Grid item xs={6} sm={3} md={3} lg={3}>
        <SystemAnalytic title="Online Users" count={onlineUserList.length} iconUrl={OnlineUsersImage} />
      </Grid>
      <Grid item xs={6} sm={3} md={3} lg={3}>
        <SystemAnalytic title="System Users" count={systemUserAmount} iconUrl={TotalUsersImage} />
      </Grid>
      <Grid item xs={6} sm={3} md={3} lg={3}>
        <SystemAnalytic title="Connected Devices" count={connectedDeviceList.length} iconUrl={ConnectedDevicesImage} />
      </Grid>
      <Grid item xs={6} sm={3} md={3} lg={3}>
        <SystemAnalytic
          title="Free Devices"
          count={connectedDeviceList.filter((item) => item.usingStatus === 'AVAILABLE').length}
          iconUrl={AvailableDevicesImage}
        />
      </Grid>

      <Grid item md={8} sx={{ display: { sm: 'none', md: 'block', lg: 'none' } }} />

      {/* row 2 */}
      <Grid item xs={12} md={12} lg={12}>
        {!(stompClientRef.current && stompClientRef.current.connected) ? (
          <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', width: '100%', height: '80vh' }}>
            <CircularProgress size={100} color="inherit" />
          </Box>
        ) : currentUsingDevice ? (
          <ControlDashboard />
        ) : (
          <DevicesDisplay />
        )}
      </Grid>
    </Grid>
  );
};

export default DashboardDefault;
