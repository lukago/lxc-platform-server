import {CREATE_LXC, FETCH_LXC_LIST} from './types';
import SockJS from 'sockjs-client'
import Stomp from 'stomp-websocket'
import {reactLocalStorage} from "reactjs-localstorage";
import {links} from "../../../config/appData";

export const connectSocket = () => (dispatch) => {
  const socket = new SockJS(`${links.BASE_URL}/sc/lxcplatform`);
  const stompClient = Stomp.over(socket);
  const token = reactLocalStorage.get('token');
  stompClient.connect({Authorization: `Bearer ${token}`}, (frame) => {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/sc/topic/jobs', (frame) => {
      const job = JSON.parse(frame.body);
      console.log(job);
      if (job.jobStatus === "DONE") {
        console.log('fetching lxc list dispatch');
        dispatch(fetchLxcList())
      }
    });
  });
  dispatch({
    type: 'SOCKET_CONNECT',
    payload: stompClient,
  });
};

export const disconnectSocket = (stompClient) => (dispatch) => {
  if (stompClient != null) {
    stompClient.disconnect();
    console.log("Disconnected");
  }

  dispatch({
    type: 'SOCKET_DC',
  });
};

export const createLxc = (lxcName, lxcUsername, lxcPassword) => {
  return {
    type: CREATE_LXC,
    payload: {
      request: {
        method: 'post',
        url: '/api/lxc',
        data: {
          name: lxcName,
          username: lxcPassword,
          password: lxcPassword
        },
      },
    },
  };
};


export const fetchLxcList = () => {
  return {
    type: FETCH_LXC_LIST,
    payload: {
      request: {
        method: 'get',
        url: '/api/lxc',
      },
    },
  };
};