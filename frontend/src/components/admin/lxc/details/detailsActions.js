import {ASSIGN_LXC, FETCH_LXC_STATUS, START_LXC, STOP_LXC} from "./types";

export const fetchLxcStatus = (lxcName) => {
  return {
    type: FETCH_LXC_STATUS,
    payload: {
      request: {
        method: 'get',
        url: `/api/lxc/${lxcName}/status`,
      },
    },
  };
};

export const startLxc = (lxcName) => {
  return {
    type: START_LXC,
    payload: {
      request: {
        method: 'post',
        url: `/api/lxc/${lxcName}/start`,
      },
    },
  };
};

export const stopLxc = (lxcName) => {
  return {
    type: STOP_LXC,
    payload: {
      request: {
        method: 'post',
        url: `/api/lxc/${lxcName}/stop`,
      },
    },
  };
};

export const assignLxc = (lxcName, username) => {
  return {
    type: ASSIGN_LXC,
    payload: {
      request: {
        method: 'post',
        url: `/api/lxc/${lxcName}/assign?username=${username}`,
      },
    },
  };
};

export const unassignLxc = (lxcName) => {
  return {
    type: ASSIGN_LXC,
    payload: {
      request: {
        method: 'post',
        url: `/api/lxc/${lxcName}/unassign`,
      },
    },
  };
};