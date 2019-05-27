import {
  FETCH_LXC_STATUS,
  FETCH_LXC_STATUS_FAIL,
  FETCH_LXC_STATUS_SUCCESS,
  START_LXC,
  START_LXC_FAIL,
  START_LXC_SUCCESS,
  STOP_LXC,
  STOP_LXC_FAIL,
  STOP_LXC_SUCCESS,
  ASSIGN_LXC,
  ASSIGN_LXC_FAIL,
  ASSIGN_LXC_SUCCESS,
  UNASSIGN_LXC,
  UNASSIGN_LXC_FAIL,
  UNASSIGN_LXC_SUCCESS,
} from './types';

const initialState = {
  failedFetchStatus: false,
  inProgressFetchStatus: false,
  failedSend: false,
  inProgressSend: false,
  lxcStatus: {}
};

export default function (state = initialState, action) {
  switch (action.type) {
    case FETCH_LXC_STATUS:
      return { ...state, inProgressFetchStatus: true, failedFetchStatus: false };

    case FETCH_LXC_STATUS_SUCCESS:
      return {
        ...state,
        inProgressFetchStatus: false,
        failedFetchStatus: false,
        lxcStatus: action.payload.data
      };

    case FETCH_LXC_STATUS_FAIL:
      return { ...state, inProgressFetchStatus: false, failedFetchStatus: true };

    case START_LXC:
    case STOP_LXC:
    case ASSIGN_LXC:
    case UNASSIGN_LXC:
      return { ...state, inProgressSend: true, failedSend: false };

    case START_LXC_SUCCESS:
    case STOP_LXC_SUCCESS:
    case ASSIGN_LXC_SUCCESS:
    case UNASSIGN_LXC_SUCCESS:
      return {
        ...state,
        inProgressSend: false,
        failedSend: false,
      };

    case START_LXC_FAIL:
    case STOP_LXC_FAIL:
    case ASSIGN_LXC_FAIL:
    case UNASSIGN_LXC_FAIL:
      return { ...state, inProgressSend: false, failedSend: true };

    default:
      return state;
  }
}
