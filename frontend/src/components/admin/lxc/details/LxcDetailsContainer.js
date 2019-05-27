import React, {Component} from 'react';
import Typography from '@material-ui/core/Typography';
import AdminLayoutContainer from "../../AdminLayoutContainer";
import {withStyles} from "@material-ui/core";
import t from "../../../../locale/locale";
import connect from "react-redux/es/connect/connect";
import {
  fetchLxcStatus, startLxc, stopLxc, assignLxc, unassignLxc
} from "./detailsActions";
import TextField from "@material-ui/core/TextField/TextField";
import Button from "@material-ui/core/Button/Button";
import Divider from "@material-ui/core/Divider/Divider";
import DialogTitle from "@material-ui/core/DialogTitle/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent/DialogContent";
import DialogActions from "@material-ui/core/DialogActions/DialogActions";
import Dialog from "@material-ui/core/Dialog/Dialog";
import {FailDialog, OkDialog} from "../../../common/dialogs";

const styles = theme => ({
  root: {
    display: 'flex',
  },
  tableContainer: {
    minHeight: 100,
  },
  form: {
    display: 'flex',
    flexDirection: 'row',
    alignItems: 'center',
  },
  btn: {
    margin: theme.spacing.unit * 2,
  },
  hdr: {
    marginTop: theme.spacing.unit * 2,
    marginBottom: theme.spacing.unit * 2,
  },
  divider: {
    marginTop: theme.spacing.unit * 2,
  },
});

class LxcDetailsContainer extends Component {
  state = {
    username: '',
    showOk: false,
    showFail: false,
  };

  componentDidMount() {
    this.fetchStatus();
  }

  componentDidUpdate(prevProps) {
    this.handleLxcActionDone(prevProps.inProgressSend && !this.props.inProgressSend)
  }

  handleLxcActionDone(done) {
    if (done) {
      this.fetchStatus();
      this.props.failedSend ? this.setState({showFail: true}) : this.setState({showOk: true})
    }
  }

  fetchStatus = () => {
    this.props.fetchLxcStatus(this.props.match.params.lxcName);
  };

  sendStart = () => {
    this.props.startLxc(this.props.match.params.lxcName)
  };

  sendStop = () => {
    this.props.stopLxc(this.props.match.params.lxcName)
  };

  sendAssign = () => {
    this.props.assignLxc(this.props.match.params.lxcName, this.state.username)
  };

  sendUnassign = () => {
    this.props.unassignLxc(this.props.match.params.lxcName)
  };

  handleChange = name => event => {
    const value = event.target.value;
    this.setState(prevProps => ({
      ...prevProps,
      [name]: value,
    }));
  };

  handleClose = () => {
    this.setState({
      showOk: false,
      showFail: false,
    })
  };

  render() {
    const {classes, match} = this.props;
    return (
        <AdminLayoutContainer>
          <OkDialog open={this.state.showOk} handleClose={this.handleClose}/>
          <FailDialog open={this.state.showFail} handleClose={this.handleClose}/>
          <Dialog onClose={this.handleClose} open={this.state.showOk}>
            <DialogTitle id="customized-dialog-title">
              {t.dialog.titleOk}
            </DialogTitle>
            <DialogContent>
              {t.dialog.contentOk}
            </DialogContent>
            <DialogActions>
              <Button onClick={this.handleClose} color="primary">
                {t.dialog.close}
              </Button>
            </DialogActions>
          </Dialog>
          <Typography variant="h5" gutterBottom component="h2">
            {t.admin.lxc.details.header + ` (${match.params.lxcName})`}
          </Typography>
          <Typography component="div" className={classes.tableContainer}>
            {JSON.stringify(this.props.lxcStatus)}
          </Typography>
          <Divider variant="middle"/>
          <Typography variant="h5" gutterBottom component="h2" className={classes.hdr}>
            {t.admin.lxc.details.actions.assign}
          </Typography>
          <form className={classes.form}>
            <TextField
                required
                autoComplete="username"
                label={t.admin.lxc.details.username}
                variant="outlined"
                onChange={this.handleChange('username')}
                className={classes.box}
            />
            <Button
                type="submit"
                variant="contained"
                color="primary"
                className={classes.btn}
                onClick={this.sendAssign}
            >
              {t.admin.lxc.details.assign}
            </Button>
          </form>
          <Divider variant="middle" className={classes.divider}/>
          <Typography variant="h5" gutterBottom component="h2" className={classes.hdr}>
            {t.admin.lxc.details.actions.start}
          </Typography>
          <Button
              type="submit"
              variant="contained"
              color="primary"
              onClick={this.sendStart}
          >
            {t.admin.lxc.details.start}
          </Button>
          <Divider variant="middle" className={classes.divider}/>
          <Typography variant="h5" gutterBottom component="h2" className={classes.hdr}>
            {t.admin.lxc.details.actions.stop}
          </Typography>
          <Button
              type="submit"
              variant="contained"
              color="primary"
              onClick={this.sendStop}
          >
            {t.admin.lxc.details.stop}
          </Button>
          <Divider variant="middle" className={classes.divider}/>
          <Typography variant="h5" gutterBottom component="h2" className={classes.hdr}>
            {t.admin.lxc.details.actions.unassign}
          </Typography>
          <Button
              type="submit"
              variant="contained"
              color="primary"
              onClick={this.sendUnassign}
          >
            {t.admin.lxc.details.unassing}
          </Button>

        </AdminLayoutContainer>
    );
  }
}

function mapStateToProps({ details }) {
  return {
    inProgressFetchStatus: details.inProgress,
    failedFetchStatus: details.createFailed,
    lxcStatus: details.lxcStatus,
    inProgressSend: details.inProgressSend,
    failedSend: details.failedSend,
  };
}

export default connect(mapStateToProps, {
  fetchLxcStatus, startLxc, stopLxc, assignLxc, unassignLxc,
})(withStyles(styles)(LxcDetailsContainer));