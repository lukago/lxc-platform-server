import Cookies from 'universal-cookie';
import { reactLocalStorage } from 'reactjs-localstorage';
import {
  START_AUTHENTICATION,
  SESSION_TIMEOUT,
  FETCH_CURRENT_USER,
  LOGOUT,
  USER_SAVED_IN_LOCAL_STORAGE,
} from './types';


export const saveUserInLocalStorage = user => async (dispatch) => {
  reactLocalStorage.set('user', JSON.stringify(user));
  dispatch({ type: USER_SAVED_IN_LOCAL_STORAGE });
};

export const startLogin = (credentials) => {
  return {
    type: START_AUTHENTICATION,
    payload: {
      request: {
        method: 'post',
        url: '/api/auth/signin',
        data: {
          username: credentials.username,
          password: credentials.password,
        },
      },
    },
  };
};

export const fetchCurrentUser = () => {
  return {
    type: FETCH_CURRENT_USER,
    payload: {
      request: {
        method: 'get',
        url: '/api/users/me',
      },
    },
  };
};

export const handleLogout = () => {
  return {
    type: LOGOUT,
    payload: {
      request: {
        method: 'post',
        url: '/api/auth/logout',
      },
    },
  };
};

export function sessionTimeout() {
  return {
    type: SESSION_TIMEOUT,
  };
}

export const clearUserData = () => {
  try {
    console.log('User not logged in. Clear user.');
    let cookies = new Cookies();
    cookies.remove('login');
    reactLocalStorage.clear();
    window.location = '/';
  } catch (error) {
    console.log('error', error);
  }
};