/* eslint-disable react-hooks/exhaustive-deps */
import { useEffect, useState } from 'react';
import { over } from 'stompjs';
import SockJS from 'sockjs-client/dist/sockjs';
import { useSelector, useDispatch } from 'react-redux';
import { API_ROOT_URL, PRIVATE_ROOM, COMMON_ROOM, ERROR_ROOM } from 'utils/constants';
import { setOnlineUsers, stopConnectDeviceSuccess } from 'store/reducers/userReducer';
import { setConnectedDevices } from 'store/reducers/deviceReducer';
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
          break;
        case 'NOTIFICATION':
          dispatch(addNotificationItem({ ...receivedMessage.content, time: receivedMessage.time }));
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

      {/* row 3 */}
      {/* <Grid item xs={12} md={7} lg={8}>
        <Grid container alignItems="center" justifyContent="space-between">
          <Grid item>
            <Typography variant="h5">Recent Orders</Typography>
          </Grid>
          <Grid item />
        </Grid>
        <MainCard sx={{ mt: 2 }} content={false}>
          <OrdersTable />
        </MainCard>
      </Grid> */}
      {/* <Grid item xs={12} md={5} lg={4}>
        <Grid container alignItems="center" justifyContent="space-between">
          <Grid item>
            <Typography variant="h5">Analytics Report</Typography>
          </Grid>
          <Grid item />
        </Grid>
        <MainCard sx={{ mt: 2 }} content={false}>
          <List sx={{ p: 0, '& .MuiListItemButton-root': { py: 2 } }}>
            <ListItemButton divider>
              <ListItemText primary="Company Finance Growth" />
              <Typography variant="h5">+45.14%</Typography>
            </ListItemButton>
            <ListItemButton divider>
              <ListItemText primary="Company Expenses Ratio" />
              <Typography variant="h5">0.58%</Typography>
            </ListItemButton>
            <ListItemButton>
              <ListItemText primary="Business Risk Cases" />
              <Typography variant="h5">Low</Typography>
            </ListItemButton>
          </List>
          <ReportAreaChart />
        </MainCard>
      </Grid> */}

      {/* row 4 */}
      {/* <Grid item xs={12} md={7} lg={8}>
        <Grid container alignItems="center" justifyContent="space-between">
          <Grid item>
            <Typography variant="h5">Sales Report</Typography>
          </Grid>
          <Grid item>
            <TextField
              id="standard-select-currency"
              size="small"
              select
              value={value}
              onChange={(e) => setValue(e.target.value)}
              sx={{ '& .MuiInputBase-input': { py: 0.5, fontSize: '0.875rem' } }}
            >
              {status.map((option) => (
                <MenuItem key={option.value} value={option.value}>
                  {option.label}
                </MenuItem>
              ))}
            </TextField>
          </Grid>
        </Grid>
        <MainCard sx={{ mt: 1.75 }}>
          <Stack spacing={1.5} sx={{ mb: -12 }}>
            <Typography variant="h6" color="secondary">
              Net Profit
            </Typography>
            <Typography variant="h4">$1560</Typography>
          </Stack>
          <SalesColumnChart />
        </MainCard>
      </Grid> */}
      {/* <Grid item xs={12} md={5} lg={4}>
        <Grid container alignItems="center" justifyContent="space-between">
          <Grid item>
            <Typography variant="h5">Transaction History</Typography>
          </Grid>
          <Grid item />
        </Grid>
        <MainCard sx={{ mt: 2 }} content={false}>
          <List
            component="nav"
            sx={{
              px: 0,
              py: 0,
              '& .MuiListItemButton-root': {
                py: 1.5,
                '& .MuiAvatar-root': avatarSX,
                '& .MuiListItemSecondaryAction-root': { ...actionSX, position: 'relative' }
              }
            }}
          >
            <ListItemButton divider>
              <ListItemAvatar>
                <Avatar
                  sx={{
                    color: 'success.main',
                    bgcolor: 'success.lighter'
                  }}
                >
                  <GiftOutlined />
                </Avatar>
              </ListItemAvatar>
              <ListItemText primary={<Typography variant="subtitle1">Order #002434</Typography>} secondary="Today, 2:00 AM" />
              <ListItemSecondaryAction>
                <Stack alignItems="flex-end">
                  <Typography variant="subtitle1" noWrap>
                    + $1,430
                  </Typography>
                  <Typography variant="h6" color="secondary" noWrap>
                    78%
                  </Typography>
                </Stack>
              </ListItemSecondaryAction>
            </ListItemButton>
            <ListItemButton divider>
              <ListItemAvatar>
                <Avatar
                  sx={{
                    color: 'primary.main',
                    bgcolor: 'primary.lighter'
                  }}
                >
                  <MessageOutlined />
                </Avatar>
              </ListItemAvatar>
              <ListItemText primary={<Typography variant="subtitle1">Order #984947</Typography>} secondary="5 August, 1:45 PM" />
              <ListItemSecondaryAction>
                <Stack alignItems="flex-end">
                  <Typography variant="subtitle1" noWrap>
                    + $302
                  </Typography>
                  <Typography variant="h6" color="secondary" noWrap>
                    8%
                  </Typography>
                </Stack>
              </ListItemSecondaryAction>
            </ListItemButton>
            <ListItemButton>
              <ListItemAvatar>
                <Avatar
                  sx={{
                    color: 'error.main',
                    bgcolor: 'error.lighter'
                  }}
                >
                  <SettingOutlined />
                </Avatar>
              </ListItemAvatar>
              <ListItemText primary={<Typography variant="subtitle1">Order #988784</Typography>} secondary="7 hours ago" />
              <ListItemSecondaryAction>
                <Stack alignItems="flex-end">
                  <Typography variant="subtitle1" noWrap>
                    + $682
                  </Typography>
                  <Typography variant="h6" color="secondary" noWrap>
                    16%
                  </Typography>
                </Stack>
              </ListItemSecondaryAction>
            </ListItemButton>
          </List>
        </MainCard>
        <MainCard sx={{ mt: 2 }}>
          <Stack spacing={3}>
            <Grid container justifyContent="space-between" alignItems="center">
              <Grid item>
                <Stack>
                  <Typography variant="h5" noWrap>
                    Help & Support Chat
                  </Typography>
                  <Typography variant="caption" color="secondary" noWrap>
                    Typical replay within 5 min
                  </Typography>
                </Stack>
              </Grid>
              <Grid item>
                <AvatarGroup sx={{ '& .MuiAvatar-root': { width: 32, height: 32 } }}>
                  <Avatar alt="Remy Sharp" src={avatar1} />
                  <Avatar alt="Travis Howard" src={avatar2} />
                  <Avatar alt="Cindy Baker" src={avatar3} />
                  <Avatar alt="Agnes Walker" src={avatar4} />
                </AvatarGroup>
              </Grid>
            </Grid>
            <Button size="small" variant="contained" sx={{ textTransform: 'capitalize' }}>
              Need Help?
            </Button>
          </Stack>
        </MainCard>
      </Grid> */}
    </Grid>
  );
};

export default DashboardDefault;
