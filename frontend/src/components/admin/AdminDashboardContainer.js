import React from 'react';
import Typography from '@material-ui/core/Typography';
import UserLayoutContainer from "./AdminLayoutContainer";
import {withStyles} from "@material-ui/core";
import {reactLocalStorage} from "reactjs-localstorage";

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
        <UserLayoutContainer>
          <Typography variant="h4" gutterBottom component="h2">
            Account data
          </Typography>
          <Typography component="div" className={classes.tableContainer}>
            <div>{user.email}</div>
            <div>{user.roles}</div>
            <div>{user.username}</div>
          </Typography>
          <Typography variant="h4" gutterBottom component="h2">
            Header
          </Typography>
          <div className={classes.tableContainer}>
            Content
          </div>
        </UserLayoutContainer>
    );
  }
}

export default withStyles(styles)(AdminDashboardContainer);
