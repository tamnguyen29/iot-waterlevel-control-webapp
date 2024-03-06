import { IconButton } from '@mui/material';
import { CloseOutlined } from '@ant-design/icons';
import PropType from 'prop-types';
import { useTheme } from '@mui/material/styles';

const ClosePopupIcon = ({ handleClosePopup }) => {
  const theme = useTheme();
  const colors = theme.palette;
  return (
    <IconButton
      // style={{ float: 'right' }}
      onClick={handleClosePopup}
      sx={{
        position: 'absolute',
        top: -20,
        right: -20,
        m: 2
      }}
    >
      <CloseOutlined style={{ fontSize: '16px', color: colors.primary['main'] }} />
    </IconButton>
  );
};
ClosePopupIcon.propTypes = {
  handleClosePopup: PropType.func.isRequired
};

export default ClosePopupIcon;
