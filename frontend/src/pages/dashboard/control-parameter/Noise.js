import PropTypes from 'prop-types';
import { Button, Switch, Box, Tooltip } from '@mui/material';
import LoadingButton from '@mui/lab/LoadingButton';
import { SendOutlined, ThunderboltOutlined, ThunderboltFilled } from '@ant-design/icons';
import { useSelector } from 'react-redux';

// const PrettoSlider = styled(Slider)({
//   color: '#52af77',
//   height: 8,
//   '& .MuiSlider-track': {
//     border: 'none'
//   },
//   '& .MuiSlider-thumb': {
//     height: 24,
//     width: 24,
//     backgroundColor: '#fff',
//     border: '2px solid currentColor',
//     '&:focus, &:hover, &.Mui-active, &.Mui-focusVisible': {
//       boxShadow: 'inherit'
//     },
//     '&::before': {
//       display: 'none'
//     }
//   },
//   '& .MuiSlider-valueLabel': {
//     lineHeight: 1.2,
//     fontSize: 12,
//     background: 'unset',
//     padding: 0,
//     width: 32,
//     height: 32,
//     borderRadius: '50% 50% 50% 0',
//     backgroundColor: '#52af77',
//     transformOrigin: 'bottom left',
//     transform: 'translate(50%, -100%) rotate(-45deg) scale(0)',
//     '&::before': { display: 'none' },
//     '&.MuiSlider-valueLabelOpen': {
//       transform: 'translate(50%, -100%) rotate(-45deg) scale(1)'
//     },
//     '& > *': {
//       transform: 'rotate(45deg)'
//     }
//   }
// });

const Noise = ({ isNoiseChecked, setIsNoiseChecked, handleSendNoise }) => {
  const sendPumOutAction = useSelector((state) => state.device.deviceAction.pumpOut);

  const handleToggle = () => {
    setIsNoiseChecked((prev) => !prev);
  };

  return (
    <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
      <Button
        variant="text"
        sx={{ fontWeight: 'bold', color: !isNoiseChecked ? '#6b6a68' : '#f2110a' }}
        startIcon={!isNoiseChecked ? <ThunderboltOutlined /> : <ThunderboltFilled />}
      >
        {!isNoiseChecked ? 'NOISE' : 'NOISE SELECTED'}
      </Button>
      <Tooltip title={!isNoiseChecked ? 'Turn on noise' : 'Turn off noise'}>
        <Switch
          edge="end"
          onChange={handleToggle}
          inputProps={{
            'aria-labelledby': 'switch-list-label-wifi'
          }}
          color="error"
        />
      </Tooltip>
      <Tooltip title="Send noise signal">
        <LoadingButton
          variant="contained"
          endIcon={<SendOutlined />}
          onClick={handleSendNoise}
          sx={{
            backgroundColor: '#52af77'
          }}
          loading={sendPumOutAction.isStarting}
          loadingPosition="end"
        >
          SEND
        </LoadingButton>
      </Tooltip>
    </Box>
  );
};

Noise.propTypes = {
  isNoiseChecked: PropTypes.bool.isRequired,
  setIsNoiseChecked: PropTypes.func.isRequired,
  handleSendNoise: PropTypes.func.isRequired
};

export default Noise;
