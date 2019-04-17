import React from 'react';
import Typography from '@material-ui/core/Typography';
import UserLayoutContainer from "./UserLayoutContainer";
import {withStyles} from "@material-ui/core";

const styles = theme => ({
  root: {
    display: 'flex',
  },
  tableContainer: {
    height: 320,
  },
});

const UserListContainer = (props) => {
  const { classes } = props;

  return (
      <UserLayoutContainer>
        <Typography variant="h4" gutterBottom component="h2">
          User list
        </Typography>
        <Typography component="div" className={classes.tableContainer}>
          Content
        </Typography>
      </UserLayoutContainer>
  );
};

export default withStyles(styles)(UserListContainer);
