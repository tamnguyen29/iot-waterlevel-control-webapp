import { CheckCircleOutlined } from '@ant-design/icons';

// icons
const icons = {
  CheckCircleOutlined
};

// ==============================|| MENU ITEMS - EXTRA PAGES ||============================== //

const connections = {
  id: 'connections',
  title: 'Online Connection',
  type: 'group',
  children: [
    {
      id: 'user-device-connected',
      title: 'Users online',
      type: 'item',
      url: '/client-connection',
      icon: icons.CheckCircleOutlined
    }
  ]
};

export default connections;
