import React, { useState, Fragment } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { toast } from 'react-toastify';
import { Container, Typography, Button } from '@mui/material';
import services from 'apis/index';
import { createJWTAxios } from 'apis/createInstance';
import ConnectedDevices from './ConnectedDevices';

const DevicesDisplay = () => {
  const [openPopup, setOpenPopup] = useState(false);
  const connectedDeviceList = useSelector((state) => state.device.connectedDevices);
  const loginUser = useSelector((state) => state.auth.login.currentUser);
  const dispatch = useDispatch();
  const jwtAxios = createJWTAxios(loginUser, dispatch);
  const handleOpenPopup = () => {
    setOpenPopup(true);
  };
  const handleClosePopup = () => {
    setOpenPopup(false);
  };

  const handleConnectToDevice = async (ids) => {
    if (ids.length === 0) {
      toast.warning('You should choose at least one!');
    } else if (ids.length === 1) {
      const device = connectedDeviceList.find((device) => device.id === ids[0]);

      if (device.usingStatus === 'AVAILABLE') {
        await services.connectToDevice(jwtAxios, dispatch, device.id, loginUser.user.id);
      } else if (device.usingStatus === 'UNAVAILABLE' && loginUser.user.id === device.currentUsingUser.id) {
        toast.info('You are already used this device!');
      } else if (device.usingStatus === 'BUSY') {
        toast.warning(`${device.name} is BUSY right now!`);
      } else {
        toast.warning(`${device.name} is currently used by ${device.currentUsingUser.name}. Try others!`);
      }
    } else {
      toast.warning('You can only choose one device!');
    }
  };

  return (
    <Container
      id="wrapper"
      sx={{
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        height: '80vh',
        width: '100%'
      }}
    >
      <Typography variant="h2" sx={{ textTransform: 'uppercase', textAlign: 'center' }}>
        You have not connected to any device yet
      </Typography>
      <Typography variant="body1" sx={{ color: 'text.secondary', mt: 3, mb: 3 }}>
        Click here to connect device
      </Typography>
      <Button onClick={handleOpenPopup} variant="contained" color="info" sx={{ borderRadius: '50px', mb: 4 }}>
        Connect
      </Button>
      <Fragment>
        <ConnectedDevices
          isAbleConnect="true"
          openPopup={openPopup}
          handleClosePopup={handleClosePopup}
          handleConnectToDevice={handleConnectToDevice}
        />
      </Fragment>
    </Container>
  );
};

export default DevicesDisplay;
