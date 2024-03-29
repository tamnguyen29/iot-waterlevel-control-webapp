import PropTypes from 'prop-types';
import { Dialog, DialogTitle, DialogContent, IconButton, useTheme, Grid, Button, Stack, useMediaQuery } from '@mui/material';
import LoadingButton from '@mui/lab/LoadingButton';
import { DataGrid } from '@mui/x-data-grid';
import { CloseOutlined, LineChartOutlined, FilterOutlined, DeleteOutlined } from '@ant-design/icons';
import ReactApexChart from 'react-apexcharts';
import { useState } from 'react';
import MainCard from 'components/MainCard';
import { DatePicker } from 'antd';
const { RangePicker } = DatePicker;
import moment from 'moment';
import { useSelector } from 'react-redux';

const columns = [
  {
    field: 'id',
    headerName: 'ORs',
    headerAlign: 'center',
    align: 'center',
    flex: 1
  },
  {
    field: 'value',
    headerName: 'VALUE(cm)',
    headerAlign: 'center',
    align: 'center',
    flex: 1
  },
  {
    field: 'time',
    headerName: 'TIME',
    headerAlign: 'center',
    align: 'center',
    flex: 1
  }
];
const WaterLevelDataDisplay = ({ openPopup, handleClosePopup, waterLevelData, handleDeleteWaterLevelData }) => {
  const theme = useTheme();
  const colors = theme.palette;
  const [timeFilter, setTimeFilter] = useState([]);
  const [isShowChart, setIsShowChart] = useState(false);
  const isSmallScreen = useMediaQuery('(max-width:600px)');
  const [waterLevelDataDisplay, setWaterLevelDataDisplay] = useState(undefined);
  const deleteWaterLevelDataState = useSelector((state) => state.waterLevel.deleteData);

  console.log('isSmallScreen', isSmallScreen);
  const ClosePopupIcon = () => {
    return (
      <IconButton style={{ float: 'right' }} onClick={handleClosePopup}>
        <CloseOutlined style={{ fontSize: '16px', color: colors.primary['main'] }} />
      </IconButton>
    );
  };
  console.log('color', colors);
  const handleShowChart = () => {
    setIsShowChart((prev) => !prev);
  };

  const options = {
    colors: [colors.primary.main, colors.primary[700]],
    xaxis: {
      categories: (!waterLevelDataDisplay ? waterLevelData.data : waterLevelDataDisplay).map(({ time }) => {
        let timeDate = new Date(time);
        return `${timeDate.getHours()}:${timeDate.getMinutes()}:${timeDate.getSeconds()}`;
      }),
      labels: {
        style: {
          colors: [colors.secondary.main]
        }
      },
      axisBorder: {
        show: true,
        color: '#070708'
      },
      tickAmount: 11,
      title: {
        text: 'TIME'
      }
    },
    yaxis: {
      labels: {
        style: {
          colors: ['#080707']
        }
      },
      title: {
        text: 'VALUE(cm)'
      },
      min: 0,
      max: 30
    },
    grid: {
      borderColor: '#7a7373'
    },
    tooltip: {
      theme: 'light'
    },
    dataLabels: {
      enabled: false
    },
    stroke: {
      curve: 'smooth',
      width: 2
    }
  };
  const series = [
    {
      name: 'WATER LEVEL',
      data: (!waterLevelDataDisplay ? waterLevelData.data : waterLevelDataDisplay).map((item) => item.value.toFixed(2)),
      color: colors.primary.main
    },
    {
      name: 'ERROR',
      data: (!waterLevelDataDisplay ? waterLevelData.data : waterLevelDataDisplay).map((item) =>
        Math.abs(item.value - waterLevelData.setpoint).toFixed(2)
      ),
      color: colors.error.main
    },
    {
      name: 'SETPOINT',
      data: Array(waterLevelData.data.length).fill(waterLevelData.setpoint),
      color: colors.success.main
    }
  ];

  const handleFilterByTimeClick = () => {
    if (timeFilter.length === 0) return;
    const filterData = waterLevelData.data.filter((item) => {
      const time = new Date(item.time).getTime();
      return time >= timeFilter[0].valueOf() && time <= timeFilter[1].valueOf();
    });
    setWaterLevelDataDisplay(filterData);
  };

  return (
    <Dialog open={openPopup} onClose={handleClosePopup} fullWidth maxWidth="lg">
      <DialogTitle
        sx={{ textAlign: 'center', position: 'relative', textTransform: 'uppercase', color: colors.text.primary, fontSize: '2rem' }}
      >
        Water level data <LineChartOutlined />
        <ClosePopupIcon />
      </DialogTitle>

      <DialogContent>
        <Grid container rowSpacing={4.5} columnSpacing={2.75}>
          <Grid item xs={12} sx={{ mb: -2.25 }}>
            <Stack direction={isSmallScreen ? 'column' : 'row'} justifyContent="space-between" spacing={2}>
              <Stack direction={isSmallScreen ? 'column' : 'row'} spacing={2}>
                <RangePicker
                  showTime
                  getPopupContainer={(triggerNode) => {
                    return triggerNode.parentNode;
                  }}
                  onChange={(value) => setTimeFilter(value)}
                />
                <Button variant="contained" color="secondary" endIcon={<FilterOutlined />} onClick={handleFilterByTimeClick}>
                  FILTER BY TIME
                </Button>
                <LoadingButton
                  variant="contained"
                  color="error"
                  endIcon={<DeleteOutlined />}
                  onClick={() => handleDeleteWaterLevelData(waterLevelData.controlUnitId, waterLevelData.deviceId)}
                  loading={deleteWaterLevelDataState.isFetching}
                  loadingPosition="end"
                >
                  DELETE DATA
                </LoadingButton>
              </Stack>
              <Button
                variant="outlined"
                endIcon={<LineChartOutlined />}
                onClick={handleShowChart}
                color={isShowChart ? 'error' : 'success'}
              >
                {isShowChart ? 'STOP SHOW CHART' : 'CHART'}
              </Button>
            </Stack>
          </Grid>
          <Grid item xs={12} sm={12} md={12} lg={isShowChart ? 4 : 12} sx={{ height: 515 }}>
            <DataGrid
              rows={(!waterLevelDataDisplay ? waterLevelData.data : waterLevelDataDisplay).map((item, index) => ({
                ...item,
                id: index + 1,
                time: moment(item.time).format('MMMM D, YYYY, h:mm:ss A')
              }))}
              columns={columns}
              disableSelectionOnClick
              pagination
              sx={{
                boxShadow: 2,
                border: 2,
                borderColor: colors.grey[200],
                '& .MuiDataGrid-cell:hover': {
                  color: colors.primary[900]
                },
                '& .MuiDataGrid-columnHeaders': {
                  backgroundColor: colors.primary.main
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
          </Grid>
          <Grid item xs={12} sm={12} md={12} lg={isShowChart ? 8 : 12}>
            {isShowChart && (
              <MainCard contentSX={{ p: 2.25 }}>
                <ReactApexChart options={options} series={series} type="line" height={430} />
              </MainCard>
            )}
          </Grid>
        </Grid>
      </DialogContent>
    </Dialog>
  );
};
WaterLevelDataDisplay.propTypes = {
  openPopup: PropTypes.bool,
  handleClosePopup: PropTypes.func,
  waterLevelData: PropTypes.object,
  handleDeleteWaterLevelData: PropTypes.func
};

export default WaterLevelDataDisplay;
