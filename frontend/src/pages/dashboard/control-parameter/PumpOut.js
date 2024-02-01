import React from 'react';
import { Stack, Slider, Button, CircularProgress } from '@mui/material';
import { styled } from '@mui/material/styles';
import { SendOutlined } from '@ant-design/icons';
import services from 'apis/index';
import { useSelector, useDispatch } from 'react-redux';
import { createJWTAxios } from 'apis/createInstance';

const PrettoSlider = styled(Slider)({
  color: '#52af77',
  height: 8,
  '& .MuiSlider-track': {
    border: 'none'
  },
  '& .MuiSlider-thumb': {
    height: 24,
    width: 24,
    backgroundColor: '#fff',
    border: '2px solid currentColor',
    '&:focus, &:hover, &.Mui-active, &.Mui-focusVisible': {
      boxShadow: 'inherit'
    },
    '&::before': {
      display: 'none'
    }
  },
  '& .MuiSlider-valueLabel': {
    lineHeight: 1.2,
    fontSize: 12,
    background: 'unset',
    padding: 0,
    width: 32,
    height: 32,
    borderRadius: '50% 50% 50% 0',
    backgroundColor: '#52af77',
    transformOrigin: 'bottom left',
    transform: 'translate(50%, -100%) rotate(-45deg) scale(0)',
    '&::before': { display: 'none' },
    '&.MuiSlider-valueLabelOpen': {
      transform: 'translate(50%, -100%) rotate(-45deg) scale(1)'
    },
    '& > *': {
      transform: 'rotate(45deg)'
    }
  }
});

const PumpOut = () => {
  const [value, setValue] = React.useState(50);
  const loginUser = useSelector((state) => state.auth.login.currentUser);
  const dispatch = useDispatch();
  const jwtAxios = createJWTAxios(loginUser, dispatch);
  const connectingDevice = useSelector((state) => state.user.connectingDevice.current.device);
  const sendPumOutAction = useSelector((state) => state.device.deviceAction.pumpOut);
  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  const valueLabelFormat = (value) => {
    return `${value}%`;
  };

  const handlePumpOut = async () => {
    console.log('hello pumpout');
    await services.sendPumpOutSignal(jwtAxios, dispatch, loginUser.user.id, connectingDevice.id, value);
  };
  return (
    <Stack direction="row" spacing={1}>
      <PrettoSlider value={value} onChange={handleChange} valueLabelDisplay="on" valueLabelFormat={valueLabelFormat} min={0} max={100} />
      <Button
        variant="contained"
        endIcon={sendPumOutAction.isStarting ? '' : <SendOutlined />}
        onClick={handlePumpOut}
        disabled={sendPumOutAction.isStarting}
        sx={{
          width: '100%',
          maxWidth: '120px',
          backgroundColor: '#52af77',
          color: 'white',
          fontSize: '12px'
        }}
      >
        {sendPumOutAction.isStarting ? <CircularProgress size={20} /> : 'NOISE'}
      </Button>
    </Stack>
  );
};

export default PumpOut;
