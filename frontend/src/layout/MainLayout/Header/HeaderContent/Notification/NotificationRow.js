import { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
// material-ui
import { Avatar, Divider, ListItemButton, ListItemAvatar, ListItemText, ListItemSecondaryAction, Typography } from '@mui/material';

// assets
import UserConnectDeviceNotificationImage from 'assets/images/notification/user_connect_device.png';
import FreeDeviceNotificationImage from 'assets/images/notification/free_device.png';
import Dot from 'components/@extended/Dot';
import moment from 'moment';

const NotificationRow = ({ index, content, notificationType, seen, time }) => {
  const notificationImageUrl = notificationType === 'USING_DEVICE' ? UserConnectDeviceNotificationImage : FreeDeviceNotificationImage;
  const formattedTime = moment(time).format('h:mm A');
  const [timeAgo, setTimeAgo] = useState('');

  useEffect(() => {
    const updateOnlineStatus = () => {
      const currentTime = moment();
      const lastOnlineTime = moment(time);
      const minutesSinceLastOnline = currentTime.diff(lastOnlineTime, 'minutes');

      const formattedTime = moment.duration(minutesSinceLastOnline, 'minutes').humanize();

      setTimeAgo(`${formattedTime} ago`);
    };

    updateOnlineStatus();

    const intervalId = setInterval(updateOnlineStatus, 60 * 1000);

    return () => clearInterval(intervalId);
  }, [time]);
  return (
    <>
      <ListItemButton key={index}>
        <ListItemAvatar>
          <Avatar
            sx={{
              color: 'success.main',
              bgcolor: 'success.lighter'
            }}
            src={notificationImageUrl}
          />
        </ListItemAvatar>
        <ListItemText primary={<Typography variant="h6">{content}</Typography>} secondary={timeAgo} />
        <ListItemSecondaryAction>
          <Typography variant="caption" noWrap>
            {formattedTime}
          </Typography>
          {!seen && <Dot color="info" />}
        </ListItemSecondaryAction>
      </ListItemButton>
      <Divider />
    </>
  );
};
NotificationRow.propTypes = {
  index: PropTypes.any,
  content: PropTypes.string,
  notificationType: PropTypes.string,
  seen: PropTypes.bool,
  time: PropTypes.string
};
export default NotificationRow;
