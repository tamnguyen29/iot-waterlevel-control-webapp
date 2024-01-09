import { useSelector } from 'react-redux';
import ReactApexChart from 'react-apexcharts';
import { Typography, Stack } from '@mui/material';

const options = {
  chart: {
    height: 200,
    type: 'radialBar',
    toolbar: {
      show: true
    }
  },
  plotOptions: {
    radialBar: {
      startAngle: -135,
      endAngle: 225,
      hollow: {
        margin: 0,
        size: '70%',
        background: '#fff',
        image: undefined,
        imageOffsetX: 0,
        imageOffsetY: 0,
        position: 'front',
        dropShadow: {
          enabled: true,
          top: 3,
          left: 0,
          blur: 4,
          opacity: 0.24
        }
      },
      track: {
        background: '#fff',
        strokeWidth: '67%',
        margin: 0,
        dropShadow: {
          enabled: true,
          top: -3,
          left: 0,
          blur: 4,
          opacity: 0.35
        }
      },
      dataLabels: {
        show: true,
        name: {
          offsetY: -10,
          show: true,
          color: '#888',
          fontSize: '17px'
        },
        value: {
          formatter: function (val) {
            return parseFloat(val);
          },
          color: '#111',
          fontSize: '36px',
          show: true
        }
      }
    }
  },
  fill: {
    type: 'gradient',
    gradient: {
      shade: 'dark',
      type: 'horizontal',
      shadeIntensity: 0.5,
      gradientToColors: ['#ABE5A1'],
      inverseColors: true,
      opacityFrom: 1,
      opacityTo: 1,
      stops: [0, 50]
    }
  },
  stroke: {
    lineCap: 'round'
  },
  labels: ['Water level(cm)']
};
const formattedDateTime = new Intl.DateTimeFormat('en-US', {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
  hour: 'numeric',
  minute: 'numeric',
  second: 'numeric'
});
const RadialBarWaterLevelChart = () => {
  const currentData = useSelector((state) => state.waterLevel.data);
  const series = [currentData.value.toFixed(2)];
  const time = new Date(currentData.time);

  return (
    <>
      <Stack direction="column" alignItems="center" justifyContent="center" spacing={1}>
        <ReactApexChart options={options} series={series} type="radialBar" height={300} />
        <Typography>{formattedDateTime.format(time)}</Typography>
      </Stack>
    </>
  );
};

export default RadialBarWaterLevelChart;
