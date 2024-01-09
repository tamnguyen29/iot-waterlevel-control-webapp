import PropTypes from 'prop-types';
import { useState, Fragment } from 'react';

// material-ui
import { useTheme } from '@mui/material/styles';
import { List, ListItemButton, ListItemIcon, ListItemText } from '@mui/material';
// assets
import { EditOutlined, LogoutOutlined, UserOutlined } from '@ant-design/icons';
import EditProfileElement from './EditProfileElement';
// ==============================|| HEADER PROFILE - PROFILE TAB ||============================== //

const ProfileTab = ({ handleLogout }) => {
  const theme = useTheme();
  const [selectedIndex, setSelectedIndex] = useState(undefined);
  const handleListItemClick = (event, index) => {
    setSelectedIndex(index);
  };

  const handleCloseEditProfile = () => {
    setSelectedIndex(undefined);
  };

  return (
    <>
      <List component="nav" sx={{ p: 0, '& .MuiListItemIcon-root': { minWidth: 32, color: theme.palette.grey[500] } }}>
        <ListItemButton selected={selectedIndex === 0} onClick={(event) => handleListItemClick(event, 0)}>
          <ListItemIcon>
            <EditOutlined />
          </ListItemIcon>
          <ListItemText primary="Edit Profile" />
        </ListItemButton>
        <ListItemButton selected={selectedIndex === 1} onClick={(event) => handleListItemClick(event, 1)}>
          <ListItemIcon>
            <UserOutlined />
          </ListItemIcon>
          <ListItemText primary="View Profile" />
        </ListItemButton>
        <ListItemButton selected={selectedIndex === 2} onClick={handleLogout}>
          <ListItemIcon>
            <LogoutOutlined />
          </ListItemIcon>
          <ListItemText primary="Logout" />
        </ListItemButton>
      </List>
      <Fragment>
        <EditProfileElement openEditProfile={selectedIndex === 0} handleCloseEditProfile={handleCloseEditProfile} />
      </Fragment>
    </>
  );
};

ProfileTab.propTypes = {
  handleLogout: PropTypes.func
};

export default ProfileTab;
