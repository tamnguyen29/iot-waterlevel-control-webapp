import PropTypes from 'prop-types';
import { Dialog, DialogActions, DialogContent, DialogTitle, Box, Button, CircularProgress, Typography, Stack } from '@mui/material';
import { useTheme } from '@mui/material/styles';
import ResetProcessImage from 'assets/images/icons/reset-process.png';
import NoControlParameterChosenImage from 'assets/images/control/no-control-chosen.png';
import StartProcessImage from 'assets/images/control/start-process.png';
import StopProcessImage from 'assets/images/control/stop-process.png';
import { RightOutlined, CheckOutlined, CloseOutlined } from '@ant-design/icons';
import { useSelector } from 'react-redux';
import { LoadingButton, Timeline, TimelineItem, TimelineSeparator, TimelineConnector, TimelineContent, TimelineDot } from '@mui/lab';

const ConfirmControlProcessAction = ({
  action,
  openConfirmControlProcessAction,
  handleCloseConfirmControlProcessAction,
  handleStartMeasurement,
  handleStopMeasurement,
  handleRestartProcess,
  controlUnitChosen,
  isNoiseChecked
}) => {
  const theme = useTheme();
  const colors = theme.palette;
  const restartProcessState = useSelector((state) => state.device.deviceAction.restartProcess);
  const startMeasurementState = useSelector((state) => state.device.deviceAction.startMeasurement);
  const stopMeasurementState = useSelector((state) => state.device.deviceAction.stopMeasurement);
  console.log('colors', colors);

  const StartMeasurementInfoElement = () => {
    return controlUnitChosen ? (
      <Stack flexDirection="row" spacing={2} alignItems="center">
        <Box
          component="img"
          sx={{
            height: 'auto',
            width: '100%',
            maxHeight: { xs: 190, md: 190 },
            maxWidth: { xs: 190, md: 190 },
            alignItems: 'center'
          }}
          src={StartProcessImage}
        />
        <Timeline>
          <TimelineItem>
            <TimelineSeparator>
              <TimelineDot color="primary" />
              <TimelineConnector />
            </TimelineSeparator>
            <TimelineContent sx={{ fontSize: '1.1rem' }}>SETPOINT {controlUnitChosen.setpoint} (cm)</TimelineContent>
          </TimelineItem>
          <TimelineItem>
            <TimelineSeparator>
              <TimelineDot color="secondary" />
              <TimelineConnector />
            </TimelineSeparator>
            <TimelineContent sx={{ fontSize: '1.1rem' }}>Kp: {controlUnitChosen.kp}</TimelineContent>
          </TimelineItem>
          <TimelineItem>
            <TimelineSeparator>
              <TimelineDot color="error" />
            </TimelineSeparator>
            <TimelineContent sx={{ fontSize: '1.1rem' }}>{isNoiseChecked ? 'NOISE IS SELECTED' : ' NO NOISE SELECTED'}</TimelineContent>
          </TimelineItem>
        </Timeline>
      </Stack>
    ) : (
      <Stack flexDirection="column" spacing={2} alignItems="center">
        <Box
          component="img"
          sx={{
            height: 'auto',
            width: '100%',
            maxHeight: { xs: 190, md: 190 },
            maxWidth: { xs: 190, md: 190 },
            alignItems: 'center'
          }}
          src={NoControlParameterChosenImage}
        />
        <Typography variant="h4">Sorry. No control parameters chosen!</Typography>
      </Stack>
    );
  };
  return (
    <>
      <Dialog open={openConfirmControlProcessAction} onClose={handleCloseConfirmControlProcessAction} fullWidth maxWidth="sm">
        {action === 'start' && (
          <>
            <DialogTitle
              sx={{
                textAlign: 'center',
                position: 'relative',
                textTransform: 'uppercase',
                color: colors.text.primary,
                fontSize: '2rem'
              }}
            >
              YOU WANT TO START THE PROCESS?
            </DialogTitle>
            <DialogContent
              sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                border: '1px solid #ccc'
              }}
            >
              <StartMeasurementInfoElement />
            </DialogContent>
            <DialogActions>
              <LoadingButton
                variant="contained"
                onClick={handleStartMeasurement}
                endIcon={<CheckOutlined />}
                loadingPosition="end"
                loading={startMeasurementState.isStarting}
                disabled={startMeasurementState.isDisable}
              >
                Confirm
              </LoadingButton>
              <Button variant="contained" color="error" onClick={handleCloseConfirmControlProcessAction} endIcon={<CloseOutlined />}>
                Close
              </Button>
            </DialogActions>
          </>
        )}
        {action === 'stop' && (
          <>
            <DialogTitle
              sx={{
                textAlign: 'center',
                position: 'relative',
                textTransform: 'uppercase',
                color: colors.text.primary,
                fontSize: '2rem'
              }}
            >
              YOU WANT TO STOP THE PROCESS?
            </DialogTitle>
            <DialogContent
              sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                border: '1px solid #ccc'
              }}
            >
              <Box
                component="img"
                sx={{
                  height: 'auto',
                  width: '100%',
                  maxHeight: { xs: 190, md: 190 },
                  maxWidth: { xs: 190, md: 190 },
                  alignItems: 'center'
                }}
                src={StopProcessImage}
              />
              <Typography
                variant="h6"
                sx={{
                  fontStyle: 'italic',
                  textAlign: 'center'
                }}
              >
                {<RightOutlined />} Click <strong>&apos;Confirm&apos;</strong> to stop the control process immediately!`
              </Typography>
            </DialogContent>
            <DialogActions>
              <LoadingButton
                variant="contained"
                onClick={handleStopMeasurement}
                endIcon={<CheckOutlined />}
                loadingPosition="end"
                loading={stopMeasurementState.isStarting}
                disabled={stopMeasurementState.isDisable}
              >
                Confirm
              </LoadingButton>
              <Button variant="contained" color="error" onClick={handleCloseConfirmControlProcessAction} endIcon={<CloseOutlined />}>
                Close
              </Button>
            </DialogActions>
          </>
        )}
        {action === 'restart' && (
          <>
            <DialogTitle
              sx={{
                textAlign: 'center',
                position: 'relative',
                textTransform: 'uppercase',
                color: colors.text.primary,
                fontSize: '2rem'
              }}
            >
              YOU WANT TO RESTART THE PROCESS?
            </DialogTitle>
            <DialogContent
              sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                border: '1px solid #ccc'
              }}
            >
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <Box sx={{ m: 2, position: 'relative' }}>
                  <Box
                    component="img"
                    sx={{
                      height: 'auto',
                      width: '100%',
                      maxHeight: { xs: 190, md: 190 },
                      maxWidth: { xs: 190, md: 190 },
                      alignItems: 'center'
                    }}
                    src={ResetProcessImage}
                  />
                  {restartProcessState.isStarting && (
                    <CircularProgress
                      size={210}
                      sx={{
                        color: colors.success.main,
                        position: 'absolute',
                        top: '-5%',
                        left: '-5%',
                        zIndex: 1
                      }}
                    />
                  )}
                </Box>
              </Box>
              <Typography
                variant="h6"
                sx={{
                  fontStyle: 'italic',
                  textAlign: 'center'
                }}
              >
                {<RightOutlined />} Notice: The control process will be reset, including resetting the water level to 0, and the control
                parameters from the previous process will be reconfigured.
              </Typography>
            </DialogContent>
            <DialogActions>
              <Button
                variant="contained"
                disabled={restartProcessState.isStarting}
                onClick={handleRestartProcess}
                endIcon={<CheckOutlined />}
              >
                Confirm
              </Button>
              <Button variant="contained" color="error" onClick={handleCloseConfirmControlProcessAction} endIcon={<CloseOutlined />}>
                Close
              </Button>
            </DialogActions>
          </>
        )}
      </Dialog>
    </>
  );
};
ConfirmControlProcessAction.propTypes = {
  action: PropTypes.oneOf(['start', 'stop', 'restart']).isRequired,
  openConfirmControlProcessAction: PropTypes.bool.isRequired,
  handleCloseConfirmControlProcessAction: PropTypes.func.isRequired,
  handleStartMeasurement: PropTypes.func.isRequired,
  handleStopMeasurement: PropTypes.func.isRequired,
  handleRestartProcess: PropTypes.func.isRequired,
  controlUnitChosen: PropTypes.object,
  isNoiseChecked: PropTypes.bool
};

export default ConfirmControlProcessAction;
