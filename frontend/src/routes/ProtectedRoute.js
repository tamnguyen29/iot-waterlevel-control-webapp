import { Navigate } from 'react-router-dom';
import PropTypes from 'prop-types';
import { useSelector } from 'react-redux';

const ProtectedRoute = ({ children }) => {
  const loginUser = useSelector((state) => state.auth.login.currentUser?.user);

  if (!loginUser) {
    return <Navigate to="/login" />;
  }
  return children;
};

ProtectedRoute.propTypes = {
  children: PropTypes.node
};

export default ProtectedRoute;
