import React from 'react';
import Typography from '@material-ui/core/Typography';
import UserLayoutContainer from "../AdminLayoutContainer";
import {withStyles} from "@material-ui/core";
import t from "../../../locale/locale";
import TextField from "@material-ui/core/TextField/TextField";
import CircularProgress
  from "@material-ui/core/CircularProgress/CircularProgress";
import Button from "@material-ui/core/Button/Button";
import connect from "react-redux/es/connect/connect";
import { createLxc, connectSocket, disconnectSocket, fetchLxcList } from './lxcActions';
import TableHead from "@material-ui/core/TableHead/TableHead";
import TableRow from "@material-ui/core/TableRow/TableRow";
import TableCell from "@material-ui/core/TableCell/TableCell";
import Table from "@material-ui/core/Table/Table";
import TableBody from "@material-ui/core/TableBody/TableBody";
import Dialog from "@material-ui/core/Dialog/Dialog";
import DialogActions from "@material-ui/core/DialogActions/DialogActions";
import DialogTitle from "@material-ui/core/DialogTitle/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent/DialogContent";

const styles = theme => ({
  tableContainer: {
    height: 320,
  },
  submit: {
    marginTop: theme.spacing.unit * 2,
  },
  form: {
    display: 'flex',
    flexDirection: 'row',
    alignItems: 'center',
  },
  box: {
    marginRight: theme.spacing.unit * 2,
  },
  table: {
    minWidth: 700,
  },
});

class LxcContainer extends React.Component {

  state = {
    lxcName: '',
    lxcUsername: '',
    lxcPassword: '',
    createFailed: false,
    openDialog: false,
  };

  handleChange = name => event => {
    const value = event.target.value;
    this.setState(prevProps => ({
      ...prevProps,
      [name]: value,
    }));
  };

  handleLxcCreateFailed(isFailed) {
    if (isFailed) {
      this.setState({
        ...this.state,
        lxcName: '',
        lxcUsername: '',
        lxcPassword: '',
        createFailed: false,
        openDialog: false,
      });
    }
  }

  sendCreate = () => {
    this.props.createLxc(this.state.lxcName, this.state.lxcUsername, this.state.lxcPassword);
  };

  handleClose = () => {
    this.setState({
      openDialog: true,
    })
  };

  componentDidMount() {
    this.props.connectSocket();
    this.props.fetchLxcList();
  }

  componentWillUnmount() {
    this.props.disconnectSocket();
  }

  componentDidUpdate(prevProps) {
    this.handleLxcCreateFailed(!prevProps.createFailed && this.props.createFailed);
  }

  render() {
    const { inProgress, classes, lxcList } = this.props;

    return (
      <UserLayoutContainer>
        <Typography variant="h4" gutterBottom component="h2">
          {t.admin.lxc.addLxc}
        </Typography>
        <form className={classes.form}>
          <TextField
            required
            autoComplete="username"
            label={t.admin.lxc.lxcName}
            variant="outlined"
            onChange={this.handleChange('lxcName')}
            className={classes.box}
          />
          <TextField
              required
              label={t.admin.lxc.lxcUsername}
              autoComplete="username"
              variant="outlined"
              onChange={this.handleChange('lxcUsername')}
              className={classes.box}
          />
          <TextField
              required
              label={t.admin.lxc.lxcPassword}
              variant="outlined"
              type="password"
              autoComplete="current-password"
              onChange={this.handleChange('lxcPassword')}
              className={classes.box}
          />
        </form>
        <div>
          <Button
              type="submit"
              variant="contained"
              color="primary"
              className={classes.submit}
              onClick={this.sendCreate}
              disabled={inProgress}
          >
            {inProgress ?
                <CircularProgress size={25}/>
                : t.admin.lxc.create
            }
          </Button>
          <Dialog onClose={this.handleClose} open={this.state.openDialog}>
            <DialogTitle id="customized-dialog-title" onClose={this.handleClose}>
              Info
            </DialogTitle>
            <DialogContent>
              LXC create job submitted
            </DialogContent>
            <DialogActions>
              <Button onClick={this.handleClose} color="primary">
                Close
              </Button>
            </DialogActions>
          </Dialog>
        </div>

        <br/><br/><br/>

        <Typography variant="h4" gutterBottom component="h2">
          {t.admin.lxc.lxcList}
        </Typography>
        <Table className={classes.table}>
          <colgroup>
            <col style={{width:'30%'}}/>
            <col style={{width:'70%'}}/>
          </colgroup>
          <TableHead>
            <TableRow>
              <TableCell>{t.admin.lxc.name}</TableCell>
              <TableCell>{t.admin.lxc.owner}</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {lxcList.map(lxc => (
                <TableRow key={lxc.name}>
                  <TableCell>{lxc.name}</TableCell>
                  <TableCell>{lxc.owner ? lxc.owner : t.admin.lxc.unasigned}</TableCell>
                </TableRow>
            ))}
          </TableBody>
        </Table>
      </UserLayoutContainer>
    );
  }
}

function mapStateToProps({ lxc }) {
  return {
    inProgress: lxc.inProgress,
    createFailed: lxc.createFailed,
    lxcList: lxc.lxcList,
  };
}

export default connect(mapStateToProps, {
  createLxc, connectSocket, disconnectSocket, fetchLxcList,
})(withStyles(styles)(LxcContainer));
