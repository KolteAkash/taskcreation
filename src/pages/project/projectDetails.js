
import React, { useState, useEffect } from 'react';
import styles from './CreateProject.module.css';
import { useLocation } from 'react-router-dom';
import Sidebar from '../../components/navbar/sidebar';
import TaskStatus from '../../components/TaskStatus';
import Status from '../../components/Status';
import Auth from '../auth/auth';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { useNavigate } from 'react-router-dom';
import Popup from '../../components/popup';
import Button from '@mui/material/Button';
import Avatar from 'react-avatar';

const ProjectDetails = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [project, setProjectName] = useState({});
  const [sessionProject, setSessionProject] = useState({});
  const [userData, setUserData] = useState([]);
  const [arr, setArr] = useState({});
  const [dataValidate, setDataValidate] = useState(false);
  const [rearr, setnewArr] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [filteredTasks, setFilteredTasks] = useState([]);
  const [avatarData, setAvatarData] = useState([]);
  const [selectedAssignees, setSelectedAssignees] = useState([]);
  const [showAssigneeDropdown, setShowAssigneeDropdown] = useState(false);

  const myModel = (value = false) => {
    setDataValidate(value);
  }

  const toggleAssigneeDropdown = () => {
    setShowAssigneeDropdown(!showAssigneeDropdown);
  };

  const handleAssigneeClick = (avatar) => {
    const updatedAssignees = [...selectedAssignees];
  
    if (updatedAssignees.includes(avatar)) {
      updatedAssignees.splice(updatedAssignees.indexOf(avatar), 1);
    } else {
      updatedAssignees.push(avatar);
    }
  
    setSelectedAssignees(updatedAssignees);

  
    
    localStorage.setItem('selectedAssignees', JSON.stringify(updatedAssignees));
  };
  
  useEffect(() => {
    if (location.state != null) {
      setnewArr(location.state.new_arr);
      myModel(location.state.fun);
    }
  }, [location.state]);

  useEffect(() => {
    const userData = Auth();

    if (!userData) {
      const notify = () => {
        toast.error("Please Login", {
          position: toast.POSITION.TOP_LEFT,
          closeButton: false,
        });
        navigate("/login");
      };
      notify();
      return;
    } else {
      setUserData(userData);

      const fetchData = async () => {
        try {
          const projectdata = JSON.parse(sessionStorage.getItem("projectdata"));
          setSessionProject(projectdata);
          const response = await fetch(`${process.env.REACT_APP_API_URL}/task/status/${projectdata.projectId}`, {
            method: "GET",
            headers: {
              Authorization: `Bearer ${userData.token}`,
              "Content-Type": "application/json",
            },
          }).catch(e => console.log("error" + e));
          if (response.status === 403) {
            navigate("/login");
            return;
          }

          if (response.status === 204) {
            setDataValidate(true);
            return;
          }

          if (!response.ok) {
            const notify = () => {
              toast.error("Please Login", {
                position: toast.POSITION.TOP_LEFT,
                closeButton: false,
              });
              navigate("/login");
            };
            notify();
            setArr({ projectName: "No data Found", projectKey: "asdas" });
            return;
          } else {
            const responseData = await response.json();
            setProjectName(responseData.project);
            setArr(responseData);
          }
        } catch (error) {
          const notify = () => {
            toast.error("Please Login", {
              position: toast.POSITION.TOP_LEFT,
              closeButton: false,
            });
            navigate("/");
          };
          notify();
          console.log(error);
          return;
        }
      };

      fetchData();
      if (rearr === true) {
        fetchData();
        setnewArr(false);
      }
    }
  }, [navigate, rearr]);


useEffect(() => {
  if (arr && arr.statusResponses) {
    const filteredTasks = arr.statusResponses.map(statusResponse => ({
      ...statusResponse,
      tasks: (statusResponse.tasks || []).filter(task =>
        task.task_summary.toLowerCase().includes(searchQuery.toLowerCase()) &&
        (selectedAssignees.length === 0 || selectedAssignees.includes(task.assignee))
      ),
    }));
    setFilteredTasks(filteredTasks);

    const avatars = arr.statusResponses.flatMap(statusResponse =>
      (statusResponse.tasks || []).map(task => task.assignee)
    );
    setAvatarData(avatars.filter((avatar, index) => avatars.indexOf(avatar) === index));
  } else {
    
    console.error("arr or arr.statusResponses is undefined or null");
  }
}, [searchQuery, arr, selectedAssignees, setAvatarData]);


  useEffect(() => {
   
    const storedAssignees = localStorage.getItem('selectedAssignees');
    if (storedAssignees) {
      setSelectedAssignees(JSON.parse(storedAssignees));
    }
  }, []);

  return (
    <>
      <div className={styles.projectDetails}>
        <Sidebar projectName={project.projectName} userName={userData.userName} projectId={project.projectId} />
        <div className={styles.taskdetails}>
        <div className={styles.fixedHeading}>
            <h1>{!dataValidate && project.projectName}</h1>
          {/* </div> */}

          <div className={styles.avatars}>
            <div className={styles.searchBar}>
              <input
                type="text"
                placeholder="Search tasks"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            {/* </div> */}
            {avatarData.slice(0, 2).map((avatar, index) => (
              <Avatar
                key={index}
                name={avatar}
 
                size="35"
                round={true}
                onClick={() => handleAssigneeClick(avatar)}
                className={selectedAssignees.includes(avatar) ? styles.selectedAvatar : ''}
              />
            ))}
            {avatarData.length > 2 && (
              <Avatar
                key="plus-sign"
                name={`+${avatarData.length - 2}`}
                size="35"
                round={true}
                onClick={toggleAssigneeDropdown}
              />
            )}
            
                  {showAssigneeDropdown && (
  <div className='subassigne'>
    <div className={styles.assigneeDropdown}>
      {avatarData.slice(2).map((avatar, index) => (
        <div
          key={index}
          onClick={() => handleAssigneeClick(avatar)}
          className={`${styles.assigneeItem} ${selectedAssignees.includes(avatar) ? styles.selectedAssignee : ''}`}
        >
          <Avatar name={avatar} size="35" round={true} />
          <span className={styles.assigneeName}>{avatar}</span>
        </div>
      ))}
    </div>
  </div>
  
)} 
     </div>
        </div>
        {/* </div> */}
        <div className={styles.tasks}>
          {dataValidate && (
            <Popup closeModel={myModel} project={sessionProject.projectId} isOpen={true} projectName={sessionProject.projectName} />
          )}
        </div>
        <div className={styles.task_details}>
          {filteredTasks.map((statusResponse) => (
            <TaskStatus
            className="abc"
              key={statusResponse.status_id}
              name={statusResponse.status.status}
              status_id={statusResponse.status_id}
              status_length={(statusResponse.tasks || []).length}
              status={(statusResponse.tasks || []).map((task, i) => (
                <div key={i}>
                  <Status name={task.task_summary} details={task.task_details} id={task.id} priority={task.priority} projectName={project.projectName} projectId={project.projectId} taskPopupData={task} />
                </div>
              ))}
            />
          ))}
          {!dataValidate && (
            <Button variant="outlined" onClick={() => myModel(true)}>
              +
            </Button>
          )}
        </div>


      </div>
      </div>
      </div>
    </>
  );
};

export default ProjectDetails;












































