import { useSelector, useDispatch } from 'react-redux';
import { Box, Stack, Button, CircularProgress } from '@mui/material';
import { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import services from 'apis/index';
import { createJWTAxios } from 'apis/createInstance';
import ControlParameterSelection from './ControlParameterSelection';
import ControlParameterAction from './ControlParameterAction';
import RadialBarWaterLevelChart from './RadialBarWaterLevelChart';
import PumpOut from './PumpOut';

const ControlParameter = () => {
  const [controlUnit, setControlUnit] = useState(null);
  const loginUser = useSelector((state) => state.auth.login.currentUser);
  const currentUsingDevice = useSelector((state) => state.user.connectingDevice.current);
  const operationAction = useSelector((state) => state.device.deviceAction);
  const dispatch = useDispatch();
  const jwtAxios = createJWTAxios(loginUser, dispatch);

  useEffect(() => {
    services.getAllControlUnit(loginUser.user.id, jwtAxios, dispatch);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [loginUser.user.id, jwtAxios, dispatch]);
  const handleStopMeasurement = async () => {
    await services.stopMeasurement(jwtAxios, dispatch, currentUsingDevice?.device.id, loginUser.user.id);
  };
  const handleStartMeasurement = async () => {
    if (controlUnit) {
      await services.startMeasurement(jwtAxios, dispatch, currentUsingDevice?.device.id, loginUser.user.id, controlUnit.id);
    } else {
      toast.warning('You should choose at least 1 control parameter!');
    }
  };

  const handleControlUnitChange = (value) => {
    setControlUnit(value);
  };
  return (
    <Box sx={{ p: 3 }}>
      <Stack spacing={2} direction="column">
        <ControlParameterAction controlUnit={controlUnit} handleControlUnitChange={handleControlUnitChange} />
        <ControlParameterSelection controlUnit={controlUnit} handleControlUnitChange={handleControlUnitChange} />
        <RadialBarWaterLevelChart />
        <PumpOut />
        <Button
          variant="contained"
          color="success"
          onClick={handleStartMeasurement}
          fullWidth
          disabled={operationAction.startMeasurement.isStarting}
        >
          {operationAction.startMeasurement.isStarting ? <CircularProgress color="inherit" size={25} thickness={4} /> : 'Start measurement'}
        </Button>
        <Button
          variant="contained"
          color="error"
          onClick={handleStopMeasurement}
          fullWidth
          disabled={operationAction.stopMeasurement.isStarting}
        >
          {operationAction.stopMeasurement.isStarting ? <CircularProgress color="inherit" size={25} thickness={4} /> : 'Stop measurement'}
        </Button>
      </Stack>
    </Box>
  );
};

export default ControlParameter;
