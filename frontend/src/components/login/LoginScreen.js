import React from 'react';
import PropTypes from 'prop-types';
import Avatar from '@material-ui/core/Avatar';
import Button from '@material-ui/core/Button';
import CssBaseline from '@material-ui/core/CssBaseline';
import FormControl from '@material-ui/core/FormControl';
import LockOutlinedIcon from '@material-ui/icons/LockOutlined';
import ErrorIcon from '@material-ui/icons/ErrorOutlined';
import Typography from '@material-ui/core/Typography';
import withStyles from '@material-ui/core/styles/withStyles';
import * as t from '../../locale/translations';
import TextField from "@material-ui/core/TextField/TextField";
import CircularProgress from "@material-ui/core/CircularProgress/CircularProgress";
import Chip from "@material-ui/core/Chip/Chip";

const styles = theme => ({
  main: {
    width: 'auto',
    display: 'block',
    marginLeft: theme.spacing.unit * 3,
    marginRight: theme.spacing.unit * 3,
    [theme.breakpoints.up(400 + theme.spacing.unit * 3 * 2)]: {
      width: 400,
      marginLeft: 'auto',
      marginRight: 'auto',
    },
  },
  paper: {
    marginTop: theme.spacing.unit * 8,
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    padding: `${theme.spacing.unit * 2}px ${theme.spacing.unit * 3}px ${theme.spacing.unit * 3}px`,
  },
  avatar: {
    margin: theme.spacing.unit,
    backgroundColor: theme.palette.secondary.main,
  },
  form: {
    width: '100%',
    marginTop: theme.spacing.unit,
  },
  submit: {
    marginTop: theme.spacing.unit * 3,
  },
  chip: {
    margin: theme.spacing.unit,
    width: '100%',
  },
});

const LoginScreen = (props) => {
  const {
    loginDisabled, credentials, startLogin,
    handleChange, spinnerVisible, classes, loginFailed,
  } = props;

  return (
      <main className={classes.main}>
        <CssBaseline />
        <paper className={classes.paper}>
          <Avatar className={classes.avatar}>
            <LockOutlinedIcon />
          </Avatar>
          <Typography component="h1" variant="h5">
            Sign in
          </Typography>
          <form className={classes.form}>
            <FormControl margin="normal" required fullWidth>
              <TextField id="email"
                     name="email"
                     autoComplete="email"
                     autoFocus
                     label={t.login.username}
                     value={credentials.username}
                     onChange={handleChange('username')}
              />
            </FormControl>
            <FormControl margin="normal" required fullWidth>
              <TextField
                  name="password"
                  type="password"
                  id="password"
                  autoComplete="current-password"
                  propName="username"
                  label={t.login.password}
                  value={credentials.password}
                  onChange={handleChange('password')}
              />
            </FormControl>

            {
              loginFailed && <Chip
                  avatar={
                    <Avatar>
                      <ErrorIcon />
                    </Avatar>
                  }
                  className={classes.chip}
                  color="secondary"
                  label={t.login.failed}
              />
            }

            <Button
                type="submit"
                fullWidth
                variant="contained"
                color="primary"
                className={classes.submit}
                onClick={startLogin}
                disabled={loginDisabled}
            >
              {spinnerVisible ?
                  <CircularProgress size={25}/>
                  : t.login.start
              }
            </Button>
          </form>

        </paper>
      </main>
  );
};

LoginScreen.propTypes = {
  classes: PropTypes.object.isRequired,
  loginDisabled: PropTypes.bool.isRequired,
  credentials: PropTypes.shape({}).isRequired,
  startLogin: PropTypes.func.isRequired,
  handleChange: PropTypes.func.isRequired,
  spinnerVisible: PropTypes.bool.isRequired,
  loginFailed: PropTypes.bool.isRequired,
};

LoginScreen.defaultProps = {
  spinnerVisible: false,
};

export default withStyles(styles)(LoginScreen);
