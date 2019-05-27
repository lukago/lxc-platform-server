import Dialog from "@material-ui/core/Dialog/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle/DialogTitle";
import t from "../../locale/locale";
import DialogContent from "@material-ui/core/DialogContent/DialogContent";
import DialogActions from "@material-ui/core/DialogActions/DialogActions";
import Button from "@material-ui/core/Button/Button";
import React from "react";

export const OkDialog = (props) => {
  return (
      <Dialog onClose={props.handleClose} open={props.open}>
        <DialogTitle id="customized-dialog-title">
          {t.dialog.titleOk}
        </DialogTitle>
        <DialogContent>
          {t.dialog.contentOk}
        </DialogContent>
        <DialogActions>
          <Button onClick={props.handleClose} color="primary">
            {t.dialog.close}
          </Button>
        </DialogActions>
      </Dialog>
  );
};

export const FailDialog = (props) => {
  return (
      <Dialog onClose={props.handleClose} open={props.open}>
        <DialogTitle id="customized-dialog-title">
          {t.dialog.titleFail}
        </DialogTitle>
        <DialogContent>
          {t.dialog.contentFail}
        </DialogContent>
        <DialogActions>
          <Button onClick={props.handleClose} color="primary">
            {t.dialog.close}
          </Button>
        </DialogActions>
      </Dialog>
  );
};