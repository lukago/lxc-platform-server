import { combineReducers } from 'redux';
import authReducer from '../components/login/authReducer';

export default combineReducers({
  auth: authReducer,
});