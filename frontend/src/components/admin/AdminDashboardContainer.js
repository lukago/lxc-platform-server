import React from 'react';
import _ from 'lodash';
import Typography from '@material-ui/core/Typography';
import AdminLayoutContainer from "./AdminLayoutContainer";
import {withStyles} from "@material-ui/core";
import {reactLocalStorage} from "reactjs-localstorage";
import t from "../../locale/locale";

const styles = theme => ({
  root: {
    display: 'flex',
  },
  tableContainer: {
    height: 320,
  },
});

class AdminDashboardContainer extends React.Component {

  state = {
    user: reactLocalStorage.getObject('user')
  };

  render() {
    const { user } = this.state;
    const { classes } = this.props;

    return (
        <AdminLayoutContainer>
          <Typography variant="h4" gutterBottom component="h2">
            {t.admin.dashboard.accountData}
          </Typography>
          <Typography component="div" className={classes.tableContainer}>
            <div>{user.email}</div>
            <div>{_.toString(user.roles)}</div>
            <div>{user.username}</div>
          </Typography>
          <Typography variant="h4" gutterBottom component="h2">
            {t.admin.dashboard.header}
          </Typography>
          <div className={classes.tableContainer}>
            {t.admin.dashboard.content}
          </div>
        </AdminLayoutContainer>
    );
  }
}

export default withStyles(styles)(AdminDashboardContainer);
