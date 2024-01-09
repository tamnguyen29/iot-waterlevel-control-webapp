import React, { useState } from 'react';
import PropTypes from 'prop-types';
import { useSelector } from 'react-redux';
import {
  Typography,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Box,
  useTheme,
  IconButton,
  Stack,
  CircularProgress
} from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import { CloseOutlined, ApiOutlined } from '@ant-design/icons';
import Dot from 'components/@extended/Dot';
import ConnectImage from 'assets/images/connect-img.png';

const ConnectedDevices = ({ openPopup, handleClosePopup, handleConnectToDevice, isAbleConnect }) => {
  const theme = useTheme();
  const colors = theme.palette;
  const isConnectingDevice = useSelector((state) => state.user.connectingDevice.isConnecting);
  const [currentDeviceIdsSelection, setCurrentDeviceIdsSelection] = useState([]);
  const connectedDeviceList = useSelector((state) => state.device.connectedDevices);
  const columns = [
    { field: 'id', headerName: 'ID' },
    {
      field: 'name',
      headerName: 'NAME',
      flex: 1
    },
    {
      field: 'description',
      headerName: 'DESCRIPTION',
      flex: 1
    },
    {
      field: 'connectedAt',
      headerName: 'CONNECTED TIME',
      align: 'left',
      flex: 1,
      cellClassName: 'name-column--cell'
    },
    {
      field: 'currentUsingUser',
      headerName: 'CURRENT ACCESS ',
      headerAlign: 'center',
      flex: 1,
      renderCell: ({ row: { currentUsingUser } }) => {
        return (
          <Box
            width="100%"
            m="0 auto"
            p="5px"
            display="flex"
            justifyContent="center"
            backgroundColor={colors.secondary[800]}
            borderRadius="4px"
          >
            <Box color={colors.grey[100]} sx={{ ml: '5px' }}>
              {currentUsingUser ? (
                <Stack direction="row" spacing={1} alignItems="center">
                  <Typography>By [ {currentUsingUser.name} ]</Typography>
                  <Dot color="error" />
                </Stack>
              ) : (
                <Stack direction="row" spacing={1} alignItems="center">
                  <Typography>AVAILABLE</Typography>
                  <Dot color="success" />
                </Stack>
              )}
            </Box>
          </Box>
        );
      }
    }
  ];
  const handleDeviceSelectionChange = (ids) => {
    setCurrentDeviceIdsSelection(ids);
  };
  const ClosePopupIcon = () => {
    return (
      <IconButton style={{ float: 'right' }} onClick={handleClosePopup}>
        <CloseOutlined style={{ fontSize: '16px', color: colors.primary['main'] }} />
      </IconButton>
    );
  };

  return (
    <Dialog open={openPopup} onClose={handleClosePopup} fullWidth maxWidth={connectedDeviceList.length > 0 ? 'md' : 'sm'}>
      {connectedDeviceList.length > 0 ? (
        <div>
          <DialogTitle
            sx={{ textAlign: 'center', position: 'relative', textTransform: 'uppercase', color: colors.text.primary, fontSize: '2rem' }}
          >
            Connected devices <ApiOutlined />
            <ClosePopupIcon />
          </DialogTitle>
          <DialogContent>
            <DataGrid
              checkboxSelection
              disableSelectionOnClick
              rows={connectedDeviceList}
              columns={columns}
              onRowSelectionModelChange={(ids) => handleDeviceSelectionChange(ids)}
              sx={{
                boxShadow: 2,
                border: 2,
                borderColor: colors.grey[200],
                '& .MuiDataGrid-cell:hover': {
                  color: colors.primary[900]
                },
                '& .MuiDataGrid-columnHeaders': {
                  backgroundColor: colors.grey[300]
                },
                '& .MuiDataGrid-columnHeaderTitle': {
                  fontWeight: 900
                },
                '& .MuiCheckbox-root': {
                  color: `${colors.primary[600]} !important`
                },
                '& .MuiDataGrid-row:hover': {
                  color: colors.primary[900]
                }
              }}
            />
          </DialogContent>
        </div>
      ) : (
        <>
          <DialogContent>
            <ClosePopupIcon />
            <Stack direction="column" alignItems="center" spacing={2}>
              <Box
                component="img"
                sx={{
                  height: 'auto',
                  width: '100%',
                  maxHeight: { xs: 256, md: 256 },
                  maxWidth: { xs: 256, md: 256 },
                  alignItems: 'center'
                }}
                src={ConnectImage}
              />
              <Typography variant="body1" sx={{ textAlign: 'center', alignItems: 'center', fontSize: '1.5rem' }}>
                No devices connected right now! <ApiOutlined />
              </Typography>
            </Stack>
          </DialogContent>
        </>
      )}
      {isAbleConnect === 'true' && connectedDeviceList.length > 0 && (
        <DialogActions>
          <Button
            color="primary"
            variant="contained"
            onClick={() => handleConnectToDevice(currentDeviceIdsSelection)}
            disabled={isConnectingDevice}
            sx={{
              width: '180px',
              height: '40px',
              '& .MuiButton-label': {
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }
            }}
          >
            {isConnectingDevice ? <CircularProgress color="inherit" size={25} thickness={4} /> : 'Connect'}
          </Button>
        </DialogActions>
      )}
    </Dialog>
  );
};

ConnectedDevices.propTypes = {
  isAbleConnect: PropTypes.oneOf(['true', 'false']).isRequired,
  openPopup: PropTypes.bool,
  handleClosePopup: PropTypes.func,
  handleConnectToDevice: PropTypes.func
};
export default ConnectedDevices;
