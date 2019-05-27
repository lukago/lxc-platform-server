import React, {Component} from 'react';
import {BrowserRouter, Switch} from 'react-router-dom';
import LogoutContainer from './login/LogoutContainer'
import LoginContainer from './login/LoginContainer'
import AdminDashboardContainer from './admin/AdminDashboardContainer'
import AdminListContainer from './admin/AdminListContainer'
import UserProfileContainer from "./user/UserProfileContainer";
import UserDashboardContainer from "./user/UserDashboardContainer";
import {appRoles, routes} from "../config/appData";
import AuthorizedRoute from "./AuthorizedRoute";
import './App.css';
import LxcContainer from "./admin/lxc/LxcContainer";
import LxcContainerDetails from "./admin/lxc/details/LxcDetailsContainer";

class App extends Component {
  render() {
    return (
      <BrowserRouter>
        <div>
          <Switch>
            <AuthorizedRoute path ={routes.LANDING} exact component={LoginContainer} roles={[appRoles.NONE]}/>
            <AuthorizedRoute path ={routes.ADMIN_DASHBOARD} exact component={AdminDashboardContainer} roles={[appRoles.ADMIN]} />
            <AuthorizedRoute path ={routes.ADMIN_USERS} exact component={AdminListContainer} roles={[appRoles.ADMIN]}/>
            <AuthorizedRoute path ={routes.ADMIN_LXC} exact component={LxcContainer} roles={[appRoles.ADMIN]}/>
            <AuthorizedRoute path ={routes.ADMIN_LXC_DETAILS} exact component={LxcContainerDetails} roles={[appRoles.ADMIN]}/>
            <AuthorizedRoute path ={routes.CLIENT_DASHBOARD} exact component={UserDashboardContainer} roles={[appRoles.CLIENT]}/>
            <AuthorizedRoute path ={routes.CLIENT_PROFILE} exact component={UserProfileContainer} roles={[appRoles.CLIENT]} />
            <AuthorizedRoute path ={routes.LOGOUT} exact component={LogoutContainer} roles={[appRoles.CLIENT, appRoles.ADMIN]}/>
          </Switch>
        </div>
      </BrowserRouter>
    );
  }
}
export default App;
