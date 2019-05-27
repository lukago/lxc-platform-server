import { combineReducers } from 'redux';
import authReducer from '../components/login/authReducer';
import lxcReducer from '../components/admin/lxc/lxcReducer';
import detailsReducer from "../components/admin/lxc/details/detailsReducer";

export default combineReducers({
  auth: authReducer,
  lxc: lxcReducer,
  details: detailsReducer,
});