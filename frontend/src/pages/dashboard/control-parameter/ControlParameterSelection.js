import { useSelector } from 'react-redux';
import PropTypes from 'prop-types';
import { Stack, FormControl, InputLabel, Select, MenuItem, FormHelperText, Typography } from '@mui/material';

const ControlParameterSelection = ({ controlUnit, setControlUnit }) => {
  const controlUnitListStore = useSelector((state) => state.controlUnit.controlUnitList.current);
  console.log('renderrrrrrrrrrrrrrrrrr');
  const SelectControlParameter = () => {
    return (
      <FormControl required sx={{ width: '100%' }}>
        <InputLabel id="control-params-label">Control parameters</InputLabel>
        <Select
          labelId="control-params-label"
          id="control-params"
          name="controlUnit"
          value={controlUnit && controlUnitListStore.find((item) => item.id === controlUnit.id) ? controlUnit.id : ''}
          label="Control parameter *"
          onChange={(e) => setControlUnit(controlUnitListStore.find((item) => item.id === e.target.value))}
        >
          {controlUnitListStore.map((controlUnitItem) => (
            <MenuItem key={controlUnitItem.id} value={controlUnitItem.id}>
              {controlUnitItem && controlUnitItem.name}
            </MenuItem>
          ))}
        </Select>
        <FormHelperText>Required</FormHelperText>
      </FormControl>
    );
  };
  const DisplayControlParameter = () => {
    if (controlUnit) {
      const controlUnitDisplay = controlUnitListStore.find((item) => item.id === controlUnit.id);
      if (controlUnitDisplay) {
        const createdTime = new Date(controlUnitDisplay.createdAt);
        const updatedTime = new Date(controlUnitDisplay.updatedAt);
        const formattedDateTime = new Intl.DateTimeFormat('en-US', {
          year: 'numeric',
          month: 'long',
          day: 'numeric',
          hour: 'numeric',
          minute: 'numeric',
          second: 'numeric'
        });
        return (
          <>
            <Stack direction="row" justifyContent="space-between">
              <Typography variant="h3">Kp: {controlUnitDisplay.kp}</Typography>
              <Typography variant="h3">Setpoint: {controlUnitDisplay.setpoint} (cm)</Typography>
            </Stack>
            <Stack direction="row" justifyContent="space-between">
              <Typography variant="h6" sx={{ fontStyle: 'italic', color: 'red' }}>
                Created time
              </Typography>
              <Typography variant="h6" sx={{ fontStyle: 'italic', color: 'red' }}>
                {formattedDateTime.format(createdTime)}
              </Typography>
            </Stack>
            <Stack direction="row" justifyContent="space-between">
              <Typography variant="h6" sx={{ fontStyle: 'italic', color: 'red' }}>
                Updated time
              </Typography>
              <Typography variant="h6" sx={{ fontStyle: 'italic', color: 'red' }}>
                {formattedDateTime.format(updatedTime)}
              </Typography>
            </Stack>
          </>
        );
      }
    }
  };
  return (
    <Stack direction="column">
      <SelectControlParameter />
      <DisplayControlParameter />
    </Stack>
  );
};
ControlParameterSelection.propTypes = {
  controlUnit: PropTypes.any,
  setControlUnit: PropTypes.func,
  controlUnitListStore: PropTypes.any
};
export default ControlParameterSelection;
