import { useState, Fragment } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { createJWTAxios } from 'apis/createInstance';
import { Typography, Stack, Button, CircularProgress, Avatar } from '@mui/material';
import { StopOutlined, ApiOutlined } from '@ant-design/icons';
// import MonthlyBarChart from '../MonthlyBarChart';
import MainCard from 'components/MainCard';
import ConnectedDevices from '../devices-display/ConnectedDevices';
import services from 'apis/index';
import ESP32Image from 'assets/images/devices/esp32.jpg';

const formattedDateTime = new Intl.DateTimeFormat('en-US', {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
  hour: 'numeric',
  minute: 'numeric',
  second: 'numeric'
});
const ControlDashboardHeader = () => {
  const isStopConnecting = useSelector((state) => state.user.connectingDevice.isStopConnecting);
  const currentUsingDevice = useSelector((state) => state.user.connectingDevice.current);
  const loginUser = useSelector((state) => state.auth.login.currentUser);
  const dispatch = useDispatch();
  const jwtAxios = createJWTAxios(loginUser, dispatch);
  const [openPopup, setOpenPopup] = useState(false);
  const handleOpenPopup = () => {
    setOpenPopup(true);
  };
  const handleClosePopup = () => {
    setOpenPopup(false);
  };
  const handleStopUsingDevice = async () => {
    await services.stopConnectToDevice(jwtAxios, dispatch, currentUsingDevice.device.id, loginUser.user.id);
  };
  return (
    <MainCard sx={{ mt: 1.5 }} contentSX={{ p: 2.25 }}>
      <Fragment>
        <ConnectedDevices isAbleConnect="false" openPopup={openPopup} handleClosePopup={handleClosePopup} />
      </Fragment>
      <Stack direction="row" justifyContent="space-between">
        <Stack direction="column" spacing={1}>
          <Stack direction="row" alignItems="center" spacing={2}>
            <Avatar
              alt="ESP32"
              src={ESP32Image}
              sx={{
                border: '2px solid #c9c6c5' // Màu và độ dày của viền
              }}
            />
            <Typography variant="h3">{currentUsingDevice?.device.name}</Typography>
          </Stack>
          <Typography variant="h5">[{currentUsingDevice?.device.description}]</Typography>
          <Typography variant="h6">Using time: {formattedDateTime.format(new Date(currentUsingDevice?.connectDeviceTime))}</Typography>
        </Stack>
        <Stack direction="column" spacing={1}>
          <Button
            variant="contained"
            onClick={handleStopUsingDevice}
            disabled={isStopConnecting}
            color="warning"
            sx={{
              width: '140px',
              height: '40px',
              fontSize: '0.9rem',
              '& .MuiButton-label': {
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              },
              '& .anticon': {
                fontSize: '1.2rem',
                marginLeft: '10px'
              }
            }}
          >
            {isStopConnecting ? (
              <CircularProgress color="inherit" size={25} thickness={4} />
            ) : (
              <span>
                Stop using
                <StopOutlined />
              </span>
            )}
          </Button>
          <Button
            variant="contained"
            onClick={handleOpenPopup}
            color="info"
            sx={{
              width: '140px',
              height: '40px',
              display: 'flex',
              alignItems: 'center',
              fontSize: '1rem',
              '& .anticon': {
                fontSize: '1.5rem',
                marginLeft: '14px'
              }
            }}
          >
            Devices
            <ApiOutlined />
          </Button>
        </Stack>
      </Stack>
    </MainCard>
  );
};

export default ControlDashboardHeader;
