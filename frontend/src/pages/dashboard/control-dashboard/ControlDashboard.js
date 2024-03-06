import { useState } from 'react';
import { Grid, Typography, Stack, Button, Box } from '@mui/material';
// import MonthlyBarChart from '../MonthlyBarChart';
import MainCard from 'components/MainCard';
import WaterLevelChart from './WaterLevelChart';
import ControlParameter from '../control-parameter';
import ControlDashboardHeader from './ControlDashboardHeader';

const ControlDashboard = () => {
  const [slot, setSlot] = useState(20);

  return (
    <Grid container rowSpacing={4.5} columnSpacing={2.75}>
      <Grid item xs={12}>
        <ControlDashboardHeader />
      </Grid>
      <Grid item xs={12} md={7} lg={8}>
        <Grid container alignItems="center" justifyContent="space-between">
          <Grid item>
            <Typography variant="h5">Water level chart</Typography>
          </Grid>
          <Grid item>
            <Stack direction="row" alignItems="center" spacing={0}>
              <Button
                size="small"
                onClick={() => setSlot(20)}
                color={slot === 20 ? 'primary' : 'secondary'}
                variant={slot === 20 ? 'outlined' : 'text'}
              >
                20 data
              </Button>
              <Button
                size="small"
                onClick={() => setSlot(40)}
                color={slot === 40 ? 'primary' : 'secondary'}
                variant={slot === 40 ? 'outlined' : 'text'}
              >
                40 data
              </Button>
            </Stack>
          </Grid>
        </Grid>
        <MainCard content={false} sx={{ mt: 1.5 }}>
          <Box sx={{ pt: 1, pr: 1.5 }}>
            <WaterLevelChart slot={slot} />
          </Box>
        </MainCard>
      </Grid>
      <Grid item xs={12} md={5} lg={4}>
        <Grid alignItems="center" justifyContent="space-between">
          <Grid item>
            <Typography variant="h5">Control parameter</Typography>
          </Grid>
          <Grid item />
        </Grid>
        <MainCard sx={{ mt: 2 }} content={false}>
          <ControlParameter />
        </MainCard>
      </Grid>
    </Grid>
  );
};

export default ControlDashboard;
