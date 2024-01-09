// assets
import { UserOutlined, ApiOutlined, ControlOutlined, LineChartOutlined } from '@ant-design/icons';

// icons
const icons = {
  UserOutlined,
  ApiOutlined,
  ControlOutlined,
  LineChartOutlined
};

// ==============================|| MENU ITEMS - SAMPLE PAGE & DOCUMENTATION ||============================== //

const data = {
  id: 'data',
  title: 'Data',
  type: 'group',
  children: [
    // {
    //   id: 'users-data',
    //   title: 'User',
    //   type: 'item',
    //   url: '#',
    //   icon: icons.UserOutlined
    // },
    // {
    //   id: 'device-data',
    //   title: 'Device',
    //   type: 'item',
    //   url: '#',
    //   icon: icons.ApiOutlined
    // },
    // {
    //   id: 'controller-data',
    //   title: 'Control parameter',
    //   type: 'item',
    //   url: '#',
    //   icon: icons.ControlOutlined
    // },
    {
      id: 'waterLevel-data',
      title: 'Water level data',
      type: 'item',
      url: '/data-waterlevel',
      icon: icons.LineChartOutlined
    }
  ]
};

export default data;
