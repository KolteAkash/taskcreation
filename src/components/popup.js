import { useState } from 'react';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { useNavigate } from "react-router-dom";
import styles from './popup.module.css'
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
const Popup = (k) => {
  const [status, setStatus] = useState('');
  const navigate = useNavigate();
  const handleSubmit = async (event) => {
    event.preventDefault();
    const data = {
      "project_id": k.project,
      "status": status
    }
    try {
      const userData = JSON.parse(sessionStorage.getItem("userData"));
      if (!userData || !userData.token) {
        const notify = () => {
          toast.error('Token Not Present', {
            position: toast.POSITION.TOP_LEFT,
            closeButton: false
          });
        };
        notify()
        navigate("/login");
        return;
      }
      const response = await fetch(`${process.env.REACT_APP_API_URL}/task/create-status`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${userData.token}`,
        },
        body: JSON.stringify(data),
      }).catch(e => console.log("error " + e));
      if (response.status === 201 || response.status === 200) {
        const notify = () => {
          toast.success("Status Added", {
            position: toast.POSITION.TOP_LEFT,
            closeButton: false
          });
        };
        notify()
        navigate("/project", { state: { new_arr: true, fun: false } })
      } else {
        const notify = () => {
          toast.error("Something went Wrong Popup", {
            position: toast.POSITION.TOP_LEFT,
            closeButton: false
          });
        };
        notify()
      }
    } catch (error) {
      const notify = () => {
        toast.error("Error Notification !", {
          position: toast.POSITION.TOP_LEFT,
          closeButton: false

        });
      };
      notify()
      console.error(error);
    }
  }
  return (
    <>
      <Dialog open={k.isOpen} onClose={() => k.closeModel()} PaperProps={{ style: { width: '500px' } }}>
      <form className={styles.popupForm} onSubmit={handleSubmit}>
        <DialogTitle>Add Status</DialogTitle>
        <DialogContent>
          <DialogContentText>
            status for changes task status
          </DialogContentText>
          <TextField
            autoFocus
            margin="dense"
            id="name"
            label="Add Status"
            type="text"
            fullWidth
            variant="standard"
            onChange={(e) => setStatus(e.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => k.closeModel()}>Cancel</Button>
          <Button type="submit" >Create</Button>
        </DialogActions>
        </form>
      </Dialog>
    </>
  )
}
export default Popup;