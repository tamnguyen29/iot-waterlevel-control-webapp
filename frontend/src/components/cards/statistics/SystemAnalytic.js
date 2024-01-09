import PropTypes from 'prop-types';
import { Box, Grid, Stack, Typography } from '@mui/material';
import MainCard from 'components/MainCard';

// ==============================|| STATISTICS - CARD  ||============================== //

const SystemAnalytic = ({ title, count, iconUrl }) => (
  <MainCard contentSX={{ p: 2.25 }}>
    <Stack spacing={0.5}>
      <Typography variant="h6" color="textSecondary">
        {title}
      </Typography>
      <Stack container alignItems="center" flexDirection="row" justifyContent="space-between">
        <Grid item>
          <Typography variant="h2" color="inherit">
            {count}
          </Typography>
        </Grid>
        <Box component="img" sx={{ width: 64, height: 64 }} src={iconUrl} />
      </Stack>
    </Stack>
  </MainCard>
);

SystemAnalytic.propTypes = {
  title: PropTypes.string,
  count: PropTypes.number,
  iconUrl: PropTypes.any
};

export default SystemAnalytic;
