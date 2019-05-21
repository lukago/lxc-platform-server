import {
  CREATE_LXC,
  CREATE_LXC_FAIL,
  CREATE_LXC_SUCCESS,
  FETCH_LXC_LIST,
  FETCH_LXC_LIST_FAIL,
  FETCH_LXC_LIST_SUCCESS,
} from './types';

const initialState = {
  createFailed: false,
  fetchLxcsFailed: false,
  inProgress: false,
  inProgressList: false,
  lxcList: []
};

export default function (state = initialState, action) {
  switch (action.type) {
    case CREATE_LXC:
      return { ...state, inProgress: true, createFailed: false };

    case CREATE_LXC_SUCCESS:
      return {
        ...state,
        inProgress: false,
        createFailed: false,
      };

    case CREATE_LXC_FAIL:
      return { ...state, inProgress: false, createFailed: true };

    case FETCH_LXC_LIST:
      return { ...state, inProgressList: true, fetchLxcsFailed: false };

    case FETCH_LXC_LIST_SUCCESS:
      return {
        ...state,
        inProgressList: false,
        fetchLxcsFailed: false,
        lxcList: action.payload.data
      };

    case FETCH_LXC_LIST_FAIL:
      return { ...state, inProgressList: false, fetchLxcsFailed: true };

    default:
      return state;
  }
}
