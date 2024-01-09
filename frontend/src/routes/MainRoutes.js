import { lazy } from 'react';

// project import
import Loadable from 'components/Loadable';
import MainLayout from 'layout/MainLayout';
import ProtectedRoute from './ProtectedRoute';
import { Navigate } from 'react-router-dom';
// render - dashboard
const DashboardDefault = Loadable(lazy(() => import('pages/dashboard')));
const WaterLevel = Loadable(lazy(() => import('pages/data-pages/water-level')));
const ClientConnection = Loadable(lazy(() => import('pages/online-connection')));
// render - utilities
const PageNotFound = Loadable(lazy(() => import('pages/not-found-page')));
// ==============================|| MAIN ROUTING ||============================== //

const MainRoutes = {
  path: '/',
  element: (
    <ProtectedRoute>
      <MainLayout />
    </ProtectedRoute>
  ),
  children: [
    {
      path: '/',
      element: <DashboardDefault />
    },
    {
      path: 'dashboard',
      children: [
        {
          path: 'default',
          element: <DashboardDefault />
        }
      ]
    },
    {
      path: 'data-waterlevel',
      element: <WaterLevel />
    },
    {
      path: 'client-connection',
      element: <ClientConnection />
    },
    {
      path: '404',
      element: <PageNotFound />
    },
    {
      path: '*',
      element: <Navigate to="/404" replace />
    }
  ]
};

export default MainRoutes;
