import {
  SESSION_TIMEOUT,
  START_AUTHENTICATION,
  START_AUTHENTICATION_SUCCESS,
  START_AUTHENTICATION_FAIL,
  FETCH_CURRENT_USER,
  FETCH_CURRENT_USER_FAIL,
  FETCH_CURRENT_USER_SUCCESS,
  LOGOUT,
  LOGOUT_SUCCESS,
  LOGOUT_FAIL,
  USER_SAVED_IN_LOCAL_STORAGE,
} from './types';

const initialState = {
  user: undefined,
  isAuthenticated: false,
  loginFailed: false,
  inProgress: false,
  token: '',
  loggedOut: false,
};

export default function (state = initialState, action) {
  switch (action.type) {
    case START_AUTHENTICATION:
      return { ...state, inProgress: true, loginFailed: false };

    case START_AUTHENTICATION_SUCCESS:
      return {
        ...state,
        token: action.payload.data.token,
        inProgress: false,
        loginFailed: false,
      };

    case START_AUTHENTICATION_FAIL:
      return { ...state, inProgress: false, loginFailed: true };

    case FETCH_CURRENT_USER:
      return { ...state };

    case FETCH_CURRENT_USER_SUCCESS:
      return { ...state, user: action.payload.data };

    case FETCH_CURRENT_USER_FAIL:
      return { ...state };

    case USER_SAVED_IN_LOCAL_STORAGE:
      return { ...state, inProgress: false, isAuthenticated: true };

    case LOGOUT:
      return { ...state };

    case LOGOUT_SUCCESS:
      return { ...state, loggedOut: true };

    case LOGOUT_FAIL:
      return { ...state, loggedOut: true };

    case SESSION_TIMEOUT:
      return initialState;

    default:
      return state;
  }
}
