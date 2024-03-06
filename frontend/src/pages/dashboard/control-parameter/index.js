import { useSelector, useDispatch } from 'react-redux';
import { Box, Stack, Button, CircularProgress, Divider } from '@mui/material';
import { useState, useEffect, Fragment } from 'react';
import { toast } from 'react-toastify';
import services from 'apis/index';
import { createJWTAxios } from 'apis/createInstance';
import ControlParameterSelection from './ControlParameterSelection';
import ControlParameterAction from './ControlParameterAction';
import RadialBarWaterLevelChart from './RadialBarWaterLevelChart';
import Noise from './Noise';
import ConfirmControlProcessAction from './ConfirmControlProcessAction';

const ControlParameter = () => {
  const [controlUnit, setControlUnit] = useState(null);
  const loginUser = useSelector((state) => state.auth.login.currentUser);
  const currentUsingDevice = useSelector((state) => state.user.connectingDevice.current);
  const operationAction = useSelector((state) => state.device.deviceAction);
  const dispatch = useDispatch();
  const jwtAxios = createJWTAxios(loginUser, dispatch);
  const [confirmControlAction, setConfirmControlAction] = useState({
    action: '',
    openConfirmPopup: false
  });
  const [isNoiseChecked, setIsNoiseChecked] = useState(false);
  console.log('Device action', operationAction);
  useEffect(() => {
    services.getAllControlUnit(loginUser.user.id, jwtAxios, dispatch);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [loginUser.user.id]);
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

  const handleRestartProcess = async () => {
    await services.restartProcess(jwtAxios, dispatch, currentUsingDevice.device.id, loginUser.user.id);
  };

  const handleControlUnitChange = (value) => {
    setControlUnit(value);
  };

  const handleCloseConfirmControlActionPopup = () => {
    setConfirmControlAction((prev) => ({
      ...prev,
      openConfirmPopup: false
    }));
  };

  const handleOpenPopupConfirmStartMeasurement = () => {
    setConfirmControlAction((prev) => ({
      ...prev,
      action: 'start',
      openConfirmPopup: true
    }));
  };

  const handleOpenPopupConfirmStopMeasurement = () => {
    setConfirmControlAction((prev) => ({
      ...prev,
      action: 'stop',
      openConfirmPopup: true
    }));
  };

  const handleOpenPopupConfirmRestartProcess = () => {
    setConfirmControlAction((prev) => ({
      ...prev,
      action: 'restart',
      openConfirmPopup: true
    }));
  };

  const handleSendNoise = async () => {
    const value = isNoiseChecked ? 100 : 0;
    await services.sendPumpOutSignal(jwtAxios, dispatch, loginUser.user.id, currentUsingDevice.device.id, value);
  };

  return (
    <Box sx={{ p: 3 }}>
      <Fragment>
        <ConfirmControlProcessAction
          action={confirmControlAction.action}
          openConfirmControlProcessAction={confirmControlAction.openConfirmPopup}
          handleCloseConfirmControlProcessAction={handleCloseConfirmControlActionPopup}
          handleStartMeasurement={handleStartMeasurement}
          handleStopMeasurement={handleStopMeasurement}
          handleRestartProcess={handleRestartProcess}
          controlUnitChosen={controlUnit}
          isNoiseChecked={isNoiseChecked}
        />
      </Fragment>
      <Stack spacing={2} direction="column">
        <ControlParameterAction controlUnit={controlUnit} handleControlUnitChange={handleControlUnitChange} />
        <ControlParameterSelection controlUnit={controlUnit} handleControlUnitChange={handleControlUnitChange} />
        <RadialBarWaterLevelChart />
        <Divider />
        <Noise isNoiseChecked={isNoiseChecked} setIsNoiseChecked={setIsNoiseChecked} handleSendNoise={handleSendNoise} />
        <Button
          variant="contained"
          color="success"
          onClick={handleOpenPopupConfirmStartMeasurement}
          fullWidth
          disabled={operationAction.startMeasurement.isDisable}
        >
          START MEASUREMENT
        </Button>
        <Button
          variant="contained"
          color="error"
          onClick={handleOpenPopupConfirmStopMeasurement}
          fullWidth
          disabled={operationAction.stopMeasurement.isDisable}
        >
          STOP MEASUREMENT
        </Button>
        <Button variant="contained" color="warning" onClick={handleOpenPopupConfirmRestartProcess} fullWidth>
          RESTART PROCESS
        </Button>
      </Stack>
    </Box>
  );
};

export default ControlParameter;
