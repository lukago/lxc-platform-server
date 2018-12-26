import React from 'react';
import { Route, IndexRoute } from 'react-router-dom';

import App from './App';
import SimpleListComponent from './SimpleListComponent';
import PrivatePage from './PrivatePage';
import LoginPage from './LoginPage';
import privateRoute from './privateRoute';

export default (onLogout) => (
  <Route path="/" name="app" component={App}>
    <Route exact path="/" component={SimpleListComponent}/>
    <Route path="private" component={privateRoute(PrivatePage)}/>
    <Route path="login" component={LoginPage}/>
    <Route path="logout" onEnter={onLogout}/>
  </Route>
);
