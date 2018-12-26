import React, {Component} from 'react';
import {BrowserRouter, Link, Route, Switch} from 'react-router-dom';
import {connect} from 'react-redux';
import {getSession} from '../reducers/authentication';
import {setLocale} from '../reducers/locale';
import {locales} from '../config/translation';
import SimpleListComponent from "./SimpleListComponent";
import privateRoute from "./privateRoute";
import {PrivatePage} from "./PrivatePage";
import LoginPage from "./LoginPage";
import '../../assets/stylus/main.styl';

const LocaleSwitcher = ({currentLocale, onLocaleChange}) => (
    <select value={currentLocale}
            onChange={e => onLocaleChange(e.target.value)}>
      {locales.map(lang => <option key={lang} value={lang}>{lang}</option>)}
    </select>
);

const TopMenu = (props) => {
  const items = props.items.map((item, key) => (
      <li key={key} className="pure-menu-item">
        <Link to={item.link} className="pure-menu-link">{item.label}</Link>
      </li>
  ));
  return (
      <div className="pure-menu pure-menu-horizontal">
        <ul className="pure-menu-list">
          {items}
        </ul>
        <LocaleSwitcher currentLocale={props.currentLocale}
                        onLocaleChange={props.setLocale}/>
      </div>
  );
};

export class App extends Component {

  componentDidMount() {
    this.props.getSession();
  }

  render() {
    const {currentLocale, setLocale} = this.props;
    const menuItems = [
      {label: 'Home', link: '/'},
      this.props.isAuthenticated ? {label: 'Logout', link: '/logout'}
          : {label: 'Login', link: '/login'},
      {label: 'Private page', link: '/private'}
    ];

    return (
      <BrowserRouter>
        <div id="application">
          <TopMenu items={menuItems}
                   currentLocale={currentLocale}
                   setLocale={setLocale}
          />
          <Switch>
            <Route exact path="/" component={SimpleListComponent}/>
            <Route path="private" component={privateRoute(PrivatePage)}/>
            <Route path="login" component={LoginPage}/>
            <Route path="logout"/>
          </Switch>
        </div>
      </BrowserRouter>
    );
  }
}

export default connect(
    state => ({
      isAuthenticated: state.authentication.isAuthenticated,
      currentLocale: state.locale.currentLocale
    }),
    {getSession, setLocale}
)(App);
