import {CREATE_LXC} from './types';
import SockJS from 'sockjs-client'
import Stomp from 'stomp-websocket'

export const connectSocket = () => (dispatch) => {
  const socket = new SockJS('http://localhost:8080/sc/lxcplatform');
  const stompClient = Stomp.over(socket);
  stompClient.connect({}, (frame) => {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/sc/topic/jobs', (msg) => {
      console.log(msg);
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

export const createLxc = (lxcName) => {
  return {
    type: CREATE_LXC,
    payload: {
      request: {
        method: 'post',
        url: '/api/lxc',
        data: {
          name: lxcName,
        },
      },
    },
  };
};