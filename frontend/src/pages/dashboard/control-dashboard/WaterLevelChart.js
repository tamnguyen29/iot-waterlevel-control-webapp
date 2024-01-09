import PropTypes from 'prop-types';
import { useState, useEffect, useRef } from 'react';

// material-ui
import { useTheme } from '@mui/material/styles';

// third-party
import ReactApexChart from 'react-apexcharts';
import { useSelector } from 'react-redux';

// chart options
const areaChartOptions = {
  chart: {
    height: 500,
    type: 'area',
    toolbar: {
      show: false
    }
  },
  dataLabels: {
    enabled: false
  },
  stroke: {
    curve: 'smooth',
    width: 2
  },
  grid: {
    strokeDashArray: 0
  }
};

// ==============================|| INCOME AREA CHART ||============================== //

const WaterLevelChart = ({ slot }) => {
  const currentData = useSelector((state) => state.waterLevel.data);
  const seriesData = useRef({
    xData: [],
    yData: []
  });
  const theme = useTheme();
  console.log('current data', currentData);

  const { primary, secondary } = theme.palette.text;
  const line = theme.palette.divider;

  const [options, setOptions] = useState(areaChartOptions);
  const [series, setSeries] = useState([
    {
      name: 'Water level data',
      data: []
    }
  ]);

  useEffect(() => {
    const time = new Date(currentData.time);
    const timePoint = `${time.getHours()}:${time.getMinutes()}:${time.getSeconds()}`;

    seriesData.current.xData.push(timePoint);
    seriesData.current.yData.push(currentData.value.toFixed(2));

    if (seriesData.current.xData.length > slot) {
      seriesData.current.xData.shift();
      seriesData.current.xData.shift();
      seriesData.current.yData.shift();
      seriesData.current.yData.shift();
    }
    setOptions((prevState) => ({
      ...prevState,
      colors: [theme.palette.primary.main, theme.palette.primary[700]],
      xaxis: {
        categories: seriesData.current.xData,
        labels: {
          style: {
            colors: [secondary]
          }
        },
        axisBorder: {
          show: true,
          color: line
        },
        tickAmount: 11,
        title: {
          text: 'TIME'
        }
      },
      yaxis: {
        labels: {
          style: {
            colors: [secondary]
          }
        },
        title: {
          text: 'VALUE(cm)'
        },
        min: 0,
        max: 30
      },
      grid: {
        borderColor: line
      },
      tooltip: {
        theme: 'light'
      }
    }));
    // const setPoint = Array.from({ length: seriesData.current.xData.length }).fill(20);
    setSeries([
      {
        name: 'Water level data',
        data: seriesData.current.yData
      }
      // ,
      // {
      //   name: 'Setpoint',
      //   data: setPoint
      // }
    ]);
  }, [primary, secondary, line, theme, currentData, slot]);

  return <ReactApexChart options={options} series={series} type="line" height={600} />;
};

WaterLevelChart.propTypes = {
  slot: PropTypes.number
};

export default WaterLevelChart;
