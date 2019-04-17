import { Component } from 'react';
import { connect } from 'react-redux';
import { reactLocalStorage } from 'reactjs-localstorage';
import * as authActions from './authActions';

class LogoutContainer extends Component {
  componentDidMount() {
    this.props.handleLogout();
  }

  componentDidUpdate() {
    const token = reactLocalStorage.get('token');
    if (!!token && this.props.loggedOut) {
      reactLocalStorage.clear();
      window.location = this.props.mainPageUrl;
    }
  }

  render() {
    return null;
  }
}

function mapStateToProps({ config, auth }) {
  return {
    mainPageUrl: '/',
    loggedOut: auth.loggedOut,
  };
}

export default connect(mapStateToProps, authActions)(LogoutContainer);
