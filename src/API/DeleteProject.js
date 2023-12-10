import * as React from 'react';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import DeleteIcon from '@mui/icons-material/Delete';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { useNavigate } from 'react-router-dom';
export default function DeleteProject(k) {
  const navigate = useNavigate();
  const [open, setOpen] = React.useState(false);

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };
  const DeleteProject = async (projectId) => {
    try {
      const userData = JSON.parse(sessionStorage.getItem("userData"));
      if (!userData || !userData.token) {
        const notify = () => {
          toast.error("Token Not Present", {
            position: toast.POSITION.TOP_LEFT,
            closeButton: false,
          });
        };
        notify();
        return;
      }
  
      const response = await fetch(`${process.env.REACT_APP_API_URL}/projects/delete-project/${projectId}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${userData.token}`,
        },
      });
  
      // Check the response status
      if (response.ok) {
        const notify = () => {
          toast.success("Project Deleted", {
            position: toast.POSITION.TOP_LEFT,
            closeButton: false,
          });
          navigate('/projectlist', { state: { new_arr: true } });
        setOpen(false);
        };
        notify();
      } else {
        const notify = () => {
          toast.error("Something went wrong while deleting the task", {
            position: toast.POSITION.TOP_LEFT,
            closeButton: false,
          });
        };
        notify();
        console.log('Failed to delete task');
      }
    } catch (error) {
      console.log('Error:', error);
    }
  };

  return (
    <div>
        
        <DeleteIcon onClick={handleClickOpen} />
   
      <Dialog
        open={open}
        onClose={handleClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">
          {"Are you sure you want to delete?"}
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
          Permanently deleting this project will also delete any associated data. Are you sure you want to proceed?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} autoFocus>No</Button>
          <Button onClick={() => DeleteProject(k.prId)} >
            Yes
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}
