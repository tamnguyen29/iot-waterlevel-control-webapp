// material-ui
import { useEffect, useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { createJWTAxios } from 'apis/createInstance';
import { Accordion, AccordionSummary, AccordionDetails, Typography, Button, useTheme, Avatar, Stack } from '@mui/material';
import { DownOutlined } from '@ant-design/icons';
import { DataGrid } from '@mui/x-data-grid';
import MainCard from 'components/MainCard';
import services from 'apis/index';
import WaterLevelDataDisplay from './WaterLevelDataDisplay';
import ESP32Image from 'assets/images/devices/esp32.jpg';
// ==============================|| WATER LEVEL PAGE ||============================== //

const WaterLevel = () => {
  const theme = useTheme();
  const colors = theme.palette;
  const loginUser = useSelector((state) => state.auth.login.currentUser);
  const dispatch = useDispatch();
  const jwtAxios = createJWTAxios(loginUser, dispatch);
  const [controlData, setControlData] = useState([]);
  const [waterLevelData, setWaterLevelData] = useState({
    deviceId: '',
    controlUnitId: '',
    data: []
  });
  const [openPopup, setOpenPopup] = useState(false);

  useEffect(() => {
    getDataControl();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
  const handleClosePopup = () => {
    setOpenPopup(false);
  };

  const getDataControl = async () => {
    const data = await services.getAllControlData(jwtAxios, loginUser.user.id);
    setControlData(data);
  };
  const handleLoadData = async (id, deviceId) => {
    const data = await services.getWaterLevelData(jwtAxios, loginUser.user.id, id, deviceId);
    setWaterLevelData({
      deviceId: deviceId,
      controlUnitId: id,
      data: data
    });
    setOpenPopup(true);
  };

  const handleDeleteWaterLevelData = async (controlUnitId, deviceId) => {
    await services.deleteWaterLevelData(dispatch, jwtAxios, loginUser.user.id, controlUnitId, deviceId);
    await getDataControl();
    setOpenPopup(false);
  };

  const columns = [
    { field: 'id', headerName: 'ID' },
    {
      field: 'deviceId',
      headerName: 'DEVICE ID'
    },
    {
      field: 'name',
      headerName: 'NAME',
      headerAlign: 'center',
      align: 'center',
      flex: 1
    },
    {
      field: 'kp',
      headerName: 'Kp',
      headerAlign: 'center',
      align: 'center',
      flex: 1
    },
    {
      field: 'setpoint',
      headerName: 'SETPOINT',
      headerAlign: 'center',
      align: 'center',
      flex: 1,
      cellClassName: 'name-column--cell'
    },
    {
      field: 'action',
      headerName: 'ACTION ',
      headerAlign: 'center',
      align: 'center',
      flex: 1,
      renderCell: ({ row: { deviceId, id } }) => {
        return (
          <Button
            fullWidth
            onClick={() => handleLoadData(id, deviceId)}
            sx={{
              color: `${colors.text.primary} !important`,
              backgroundColor: `${colors.primary.light} !important`,
              fontWeight: 900
            }}
          >
            Load data
          </Button>
        );
      }
    }
  ];

  const WaterLevelElement = () => {
    const accordion = controlData?.map((value) => {
      return (
        <Accordion key={value.deviceId}>
          <AccordionSummary expandIcon={<DownOutlined />}>
            <Stack direction="row" alignItems="center" spacing={2}>
              <Avatar
                alt="ESP32"
                src={ESP32Image}
                sx={{
                  border: '2px solid #c9c6c5'
                }}
              />
              <Typography>
                {value.deviceName} [{value.deviceDescription}]
              </Typography>
            </Stack>
          </AccordionSummary>
          <AccordionDetails>
            <DataGrid
              rows={value.controlUnitList.map((item) => ({ ...item, deviceId: value.deviceId }))}
              columns={columns}
              hideFooterPagination
              disableSelectionOnClick
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
          </AccordionDetails>
        </Accordion>
      );
    });

    return <div>{accordion}</div>;
  };

  return (
    <MainCard>
      <WaterLevelElement />
      <WaterLevelDataDisplay
        openPopup={openPopup}
        handleClosePopup={handleClosePopup}
        waterLevelData={waterLevelData}
        handleDeleteWaterLevelData={handleDeleteWaterLevelData}
      />
    </MainCard>
  );
};

export default WaterLevel;
