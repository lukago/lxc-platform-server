import axiosMiddleware from 'redux-axios-middleware';
import { reactLocalStorage } from 'reactjs-localstorage';
import { axiosClient } from './axiosClient';
import { sessionTimeout, clearUserData } from '../components/login/authActions';

function handleUnauthorized(dispatch, status) {
  if (status === 401) {
    clearUserData();
    dispatch(sessionTimeout());
  }
}

const options = {
  interceptors: {
    request: [{
      success: (obj = {}, req) => {
        const token = reactLocalStorage.get('token');
        if (token) {
          req.headers.Authorization = `Bearer ${token}`;
        }
        return req;
      },
    }],
    response: [{
      error: ({ dispatch }, error) => {
        handleUnauthorized(dispatch, error.response.status);
        return Promise.reject(error);
      },
    }]
  },
};

export default axiosMiddleware(axiosClient, options);
