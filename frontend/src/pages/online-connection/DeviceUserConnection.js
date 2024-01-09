import React from 'react';
import { Grid } from '@mui/material';
import { UserView } from './user/view/index';

const DeviceUserConnection = () => {
  return (
    <Grid container>
      <Grid item xs={12}>
        <UserView />
      </Grid>
    </Grid>
  );
};

export default DeviceUserConnection;
