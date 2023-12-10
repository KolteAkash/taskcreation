import React, { useState } from 'react';
import styles from './TaskStatus.module.css'
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { useNavigate } from 'react-router-dom';
import Badge from '@mui/material/Badge';
const TaskStatus = (k) => {
  const navigate = useNavigate();
  const [isHovered, setIsHovered] = useState(false);
  const borderRadiusStyle={
    paddingBottom: isHovered ? "5rem" : "0"
  }
  const draggingOver = (e,name=false) => {
    setIsHovered(true);
    e.preventDefault();
  } 
  const handleMouseEnter = () => {
    setIsHovered(true);
  };
  const handleMouseLeave = () => {
    setIsHovered(false);
  };
  const dragDroped = (e, status_id) => { 
    setIsHovered(false);
    let transfer = e.dataTransfer.getData("task");
    const data = { "status_id": status_id }
    try {
      const userData = JSON.parse(sessionStorage.getItem("userData"));
      const fetchData = async () => {
        if (!userData || !userData.token) {
     
          navigate("/login");
          return;
        }
        const response = await fetch(`${process.env.REACT_APP_API_URL}/task/update-status-task/${transfer}`, {
          method: `PUT`,
          headers: {
            Authorization: `Bearer ${userData.token}`,
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(data),
        });
        if (response.ok) {
          if (response.status === 201 || response.status === 200) {
            navigate('/project', { state: { new_arr: true } });
          } else {
            const notify = () => {
              toast.error('Something Went Wrong Task Status Jsx', {
                position: toast.POSITION.TOP_LEFT,
                closeButton: false
              });
            };
            notify()
          }

        } else {
          const notify = () => {
            toast.error('Some thing Went Wrong  ' + response.status, {
              position: toast.POSITION.TOP_LEFT,
              closeButton: false
            });
          };
          notify()
        }
      }
      fetchData();

    } catch (e) { 
      const notify = () => {
        toast.error('Error In Catch', {
            position: toast.POSITION.TOP_LEFT,
            closeButton: false
        });
    };
    notify()
    return;
    }
  }

  return (
    <>
      <div droppable="true" 
      onDragEnter={handleMouseEnter}
      onDragLeave={handleMouseLeave}
      onDragOver={(e) => draggingOver(e, true)} onDrop={(e) => dragDroped(e, k.status_id)} 
      className={styles.task}
      style={borderRadiusStyle}>
        <b className={styles.taskHeading}>{k.name} <Badge badgeContent={k.status_length} color="primary"></Badge></b>
        <div className={styles.task_status_width}>
          {k.status}
        </div>
      </div>
    </>
  )
}
export default TaskStatus;