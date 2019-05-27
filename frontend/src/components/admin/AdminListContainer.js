import React from 'react';
import Typography from '@material-ui/core/Typography';
import AdminLayoutContainer from "./AdminLayoutContainer";
import {withStyles} from "@material-ui/core";
import t from "../../locale/locale";

const styles = theme => ({
  root: {
    display: 'flex',
  },
  tableContainer: {
    height: 320,
  },
});

const AdminListContainer = (props) => {
  const { classes } = props;

  return (
      <AdminLayoutContainer>
        <Typography variant="h4" gutterBottom component="h2">
          {t.admin.list.userList}
        </Typography>
        <Typography component="div" className={classes.tableContainer}>
          {t.admin.list.content}
        </Typography>
      </AdminLayoutContainer>
  );
};

export default withStyles(styles)(AdminListContainer);
