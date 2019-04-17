import React, { Component } from 'react';
import {BrowserRouter, Route, Switch} from 'react-router-dom';
import { reactLocalStorage } from 'reactjs-localstorage'
import jwtDecode from 'jwt-decode'
import LogoutContainer from './login/LogoutContainer'
import LoginContainer from './login/LoginContainer'
import UserDashboardContainer from './admin/AdminDashboardContainer'
import UserListContainer from './admin/AdminListContainer'
import './App.css';

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      token: reactLocalStorage.get('token'),
      user: reactLocalStorage.get('token') != null ? jwtDecode(reactLocalStorage.get('token')) : null,
    }
  }

  render() {
    if (!this.state.user) {
      return (
          <BrowserRouter>
            <div>
              <Switch>
                <Route path ="/" exact component={LoginContainer}/>
              </Switch>
            </div>
          </BrowserRouter>
      );
    }

    if (this.state.user) {
      return (
          <BrowserRouter>
            <div>
              <Switch>
                <Route path ="/logout" component={LogoutContainer} />
                <Route path ="/users" exact component={UserListContainer} />
                <Route path ="/" exact component={UserDashboardContainer} />
              </Switch>
            </div>
          </BrowserRouter>
      )
    }
  }
}

export default App;
