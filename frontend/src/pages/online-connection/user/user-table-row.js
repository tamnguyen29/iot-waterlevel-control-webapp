import { useState, useEffect } from 'react';
import PropTypes from 'prop-types';

import Stack from '@mui/material/Stack';
import Avatar from '@mui/material/Avatar';
import Popover from '@mui/material/Popover';
import TableRow from '@mui/material/TableRow';
import Checkbox from '@mui/material/Checkbox';
import MenuItem from '@mui/material/MenuItem';
import TableCell from '@mui/material/TableCell';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';

import Label from 'components/label';
import DefaultAvatar from 'assets/images/users/default_avatar.png';
import { MoreOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import moment from 'moment';
// ----------------------------------------------------------------------

export default function UserTableRow({ selected, name, avatarUrl, email, role, onlineAt, handleClick }) {
  const [open, setOpen] = useState(null);

  const [timeAgo, setTimeAgo] = useState('');

  useEffect(() => {
    const updateOnlineStatus = () => {
      const currentTime = moment();
      const lastOnlineTime = moment(onlineAt);
      const minutesSinceLastOnline = currentTime.diff(lastOnlineTime, 'minutes');

      const formattedTime = moment.duration(minutesSinceLastOnline, 'minutes').humanize();

      setTimeAgo(`${formattedTime} ago`);
    };

    updateOnlineStatus();

    const intervalId = setInterval(updateOnlineStatus, 60 * 1000);

    return () => clearInterval(intervalId);
  }, [onlineAt]);

  const handleOpenMenu = (event) => {
    setOpen(event.currentTarget);
  };

  const handleCloseMenu = () => {
    setOpen(null);
  };

  const avatar = avatarUrl === 'DEFAULT' ? DefaultAvatar : avatarUrl;

  return (
    <>
      <TableRow hover tabIndex={-1} role="checkbox" selected={selected}>
        <TableCell padding="checkbox">
          <Checkbox disableRipple checked={selected} onChange={handleClick} />
        </TableCell>

        <TableCell component="th" scope="row" padding="none">
          <Stack direction="row" alignItems="center" spacing={2}>
            <Avatar alt={name} src={avatar} />
            <Typography variant="h6" noWrap>
              {name}
            </Typography>
          </Stack>
        </TableCell>

        <TableCell>{email}</TableCell>

        <TableCell>{role}</TableCell>

        <TableCell align="center">{timeAgo}</TableCell>

        <TableCell>
          <Label color={'success'}>Online</Label>
        </TableCell>

        <TableCell align="right">
          <IconButton onClick={handleOpenMenu}>
            <MoreOutlined />
          </IconButton>
        </TableCell>
      </TableRow>

      <Popover
        open={!!open}
        anchorEl={open}
        onClose={handleCloseMenu}
        anchorOrigin={{ vertical: 'top', horizontal: 'left' }}
        transformOrigin={{ vertical: 'top', horizontal: 'right' }}
        PaperProps={{
          sx: { width: 140 }
        }}
      >
        <MenuItem onClick={handleCloseMenu}>
          <EditOutlined style={{ marginRight: '2px' }} />
          Edit
        </MenuItem>

        <MenuItem onClick={handleCloseMenu} sx={{ color: 'error.main' }}>
          <DeleteOutlined style={{ marginRight: '2px' }} />
          Delete
        </MenuItem>
      </Popover>
    </>
  );
}

UserTableRow.propTypes = {
  avatarUrl: PropTypes.any,
  email: PropTypes.any,
  handleClick: PropTypes.func,
  onlineAt: PropTypes.any,
  name: PropTypes.any,
  role: PropTypes.any,
  selected: PropTypes.any,
  status: PropTypes.string
};
