import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import * as authActions from './authActions';
import { reactLocalStorage } from 'reactjs-localstorage';
import LoginScreen from './LoginScreen';

class LoginContainer extends Component {
  constructor(props) {
    super(props);
    this.state = {
      credentials: {
        username: '',
        password: '',
      },
      loginFailed: false,
    };
  }

  handleChange = name => event => {
    const { credentials } = this.state;

    credentials[name] = event.target.value;

    this.setState(prevProps => ({
      ...prevProps,
      credentials,
    }));
  };

  componentDidUpdate(prevProps) {
    this.handleTokenFetched(!prevProps.token && this.props.token);
    this.handleUserFetched(!prevProps.user && this.props.user);
    this.handleLoginFailed(!prevProps.loginFailed && this.props.loginFailed);
  }

  startLogin = async (event) => {
    event.preventDefault();
    this.props.startLogin(this.state.credentials);
    return true;
  };

  handleUserFetched(isUserAvailable) {
    if (isUserAvailable) {
      this.props.saveUserInLocalStorage(this.props.user);
      if (this.props.redirect) {
        window.location = '/';
      }
    }
  }

  handleTokenFetched(isTokenAvailable) {
    if (isTokenAvailable) {
      reactLocalStorage.set('token', this.props.token);
      this.props.fetchCurrentUser();
    }
  }

  handleLoginFailed(isFailed) {
    if (isFailed) {
      this.setState({
        ...this.state,
        credentials: {
          username: '',
          password: '',
        },
      });
    }
  }

  render() {
    const loginScreenProps = {
      loginDisabled: this.props.spinnerVisible,
      credentials: this.state.credentials,
      startLogin: this.startLogin,
      handleChange: this.handleChange,
      spinnerVisible: this.props.spinnerVisible,
      loginFailed: this.props.loginFailed,
    };
    return (
        <Fragment>
            <LoginScreen {...loginScreenProps} />
        </Fragment>
    );
  }
}

LoginContainer.propTypes = {
  redirect: PropTypes.bool,
};

LoginContainer.defaultProps = {
  redirect: true,
};

const mapStateToProps = state => {
  return {
    spinnerVisible: state.auth.inProgress,
    loginFailed: state.auth.loginFailed,
    user: state.auth.user,
    token: state.auth.token,
    error: state.auth.error,
  };
};

export default connect(mapStateToProps, authActions)(LoginContainer);
