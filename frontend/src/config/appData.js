export const links = {
  BASE_URL: process.env.REACT_APP_API_URL,
};

export const appRoles = {
  ADMIN: 'ROLE_ADMIN',
  CLIENT: 'ROLE_CLIENT',
  NONE: 'ROLE_NONE',
};

export const routes = {
  LANDING: '/',
  ADMIN_DASHBOARD: '/admin/dashboard',
  ADMIN_USERS: '/admin/users',
  ADMIN_LXC: '/admin/lxc',
  ADMIN_LXC_DETAILS: '/admin/lxc/:lxcName',
  CLIENT_DASHBOARD: '/client/dashboard',
  CLIENT_PROFILE: '/client/profile',
  LOGOUT: '/logout'
};
