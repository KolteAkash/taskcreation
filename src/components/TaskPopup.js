import React, { useState, useEffect } from 'react';
import AllStatusList from './AllStatusList';
import ProjectPriority from './ProjectPriority';
import styles from './TaskPopup.module.css';
import UserAssigne from './UserAssigne';
import InputForm from './from/InputForm';
import ButtonName from './from/ButtonName';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { useNavigate } from 'react-router-dom';
import { BiTrash } from 'react-icons/bi';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import WorkLogPopup from './navbar/WorkLogPopup';
import { AiOutlineDelete } from 'react-icons/ai';
const TaskPopup = (k) => {
    const navigate = useNavigate();
    const [taskDetails, setTaskDetails] = useState('');
    const [taskDescription, setTaskDescription] = useState('');
    const [selectedFile, setSelectedFile] = useState(null);
    const [assignee, setAssignee] = useState('');
    const [reporter, setReporter] = useState('');
    const [statusList, setStatusList] = useState({});
    const [priority, setPriority] = useState({});
    const [value, setValue] = useState(true);
    const [workLogHistory, setWorkLogHistory] = useState([]);
    const [workLogData, setWorkLogData] = useState({
        logTime: '',
        dateStarted: '',
        workDescription: '',
    });
    const [workLogs, setWorkLogs] = useState([]);
    const [isNewTask, setIsNewTask] = useState(true);
    const [showWorkLogPopup, setShowWorkLogPopup] = useState(false);
    const handleWorkLogClick = () => {
        setShowWorkLogPopup(true);
    };
    const [isTaskCreated, setIsTaskCreated] = useState(false);
    const calculateRemainingTime = (dateStarted, logTime) => {
        const logTimeInMinutes = parseLogTime(logTime);
        const currentDate = new Date();
        const startDate = new Date(dateStarted);
        const timeDifferenceMs = currentDate - startDate;
        const totalMinutes = Math.max(0, logTimeInMinutes - timeDifferenceMs / (60 * 1000));
        const weeks = Math.floor(totalMinutes / (7 * 24 * 60));
        const days = Math.floor((totalMinutes % (7 * 24 * 60)) / (24 * 60));
        const hours = Math.floor((totalMinutes % (24 * 60)) / 60);
        const minutes = Math.floor(totalMinutes % 60);
        const formattedRemainingTime = `${weeks}w ${days}d ${hours}h ${minutes}m`;
        return formattedRemainingTime;
    };
    const parseLogTime = (logTime) => {
        if (!logTime) {
            return 0;
        }
        const logTimeParts = logTime.match(/(-?\d+)([wdhms])/g);
        if (!logTimeParts) {
            return 0;
        }
        let totalMinutes = 0;
        for (const part of logTimeParts) {
            const value = parseInt(part);
            const unit = part.slice(-1);
            if (isNaN(value) || value < 0) {
                return 0;
            }
            switch (unit) {
                case 'w':
                    totalMinutes += value * 7 * 24 * 60;
                    break;
                case 'd':
                    totalMinutes += value * 24 * 60;
                    break;
                case 'h':
                    totalMinutes += value * 60;
                    break;
                case 'm':
                    totalMinutes += value;
                    break;
                default:
                    break;
            }
        }
        return totalMinutes;
    };
    const handleSave = (logTime, dateStarted, workDescription) => {
        const remainingTime = calculateRemainingTime(dateStarted, logTime);
        const newWorkLogData = {
            logTime,
            dateStarted,
            workDescription,
            timeRemaining: remainingTime,
        };
        setWorkLogData({
            logTime: '',
            dateStarted: '',
            workDescription: '',
        });
        setShowWorkLogPopup(false);
    };
    const handleCancel = () => {
        setWorkLogData({
            logTime: '',
            dateStarted: '',
            workDescription: '',
        });
        setShowWorkLogPopup(false);
    };
    const userData = JSON.parse(sessionStorage.getItem('userData'));
    useEffect(() => {
        if (k.otherdata) {
            const {
                task_summary,
                task_details,
                assignee: assigneeEmail,
                reporter: reporterEmail,
                projectStatus,
                priority: taskPriority,
                workLogHistory: initialWorkLogHistory,
            } = k.otherdata;
            setTaskDetails(task_summary);
            setTaskDescription(task_details);
            setAssignee(assigneeEmail);
            setReporter(reporterEmail);
            const { status_id } = projectStatus;
            setStatusList({ status_id });
            setPriority(taskPriority);
            setIsNewTask(false);
            setWorkLogHistory(initialWorkLogHistory || []);
        }
    }, [k.otherdata]);
    const resetForm = () => {
        setTaskDetails('');
        setTaskDescription('');
        setSelectedFile(null);
        setAssignee('');
        setReporter('');
        setWorkLogData({
            logTime: '',
            dateStarted: '',
            workDescription: '',
        });
        setIsNewTask(true);
    };
    const DeleteTask = async (taskId) => {
        try {
            if (!userData || !userData.token) {
                const notify = () => {
                    toast.error('Token Not Present', {
                        position: toast.POSITION.TOP_LEFT,
                        closeButton: false,
                    });
                };
                notify();
                return;
            }
            const response = await fetch(`${process.env.REACT_APP_API_URL}/task/delete-task/${taskId}`, {
                method: 'DELETE',
                headers: {
                    Authorization: `Bearer ${userData.token}`,
                },
            });
            if (response.ok) {
                const notify = () => {
                    toast.success('Task Deleted', {
                        position: toast.POSITION.TOP_LEFT,
                        closeButton: false,
                    });
                    navigate('/project', { state: { new_arr: true } });
                    k.closeModel(false);
                };
                notify();
                resetForm();
            } else {
                const notify = () => {
                    toast.error('Something went wrong while deleting the task', {
                        position: toast.POSITION.TOP_LEFT,
                        closeButton: false,
                    });
                };
                notify();
                console.error('Failed to delete the task');
            }
        } catch (error) {
            console.error('Error:', error);
        }
    };
    let reporter_value = k.otherdata && k.otherdata.reporter ? k.otherdata.reporter : userData.email;
    const editTextArea = (show) => {
        setValue(false);
    };
    const handleSubmit = async (event) => {
        event.preventDefault();
        setIsTaskCreated(true);
        const data = {
            project_id: k.projectId,
            task_summary: taskDetails,
            task_details: taskDescription,
            assignee: assignee,
            reporter: reporter,
            status_id: statusList.status_id,
            priority_id: priority.priority_id,
            workLog_id: workLogData.workLog_id,
            logTime: workLogData.logTime,
            dateStarted: workLogData.dateStarted,
            workDescription: workLogData.workDescription,
        };
        const requiredData = JSON.stringify(data);
        const formData = new FormData();
        formData.append('file', selectedFile);
        formData.append('requiredData', requiredData);
        try {
            if (!userData || !userData.token) {
                const notify = () => {
                    toast.error('Token Not Present', {
                        position: toast.POSITION.TOP_LEFT,
                        closeButton: false,
                    });
                };
                notify();
                return;
            }
            const response = await fetch(k.apiRequest, {
                method: k.apiMethod,
                body: formData,
                headers: {
                    Authorization: `Bearer ${userData.token}`,
                },
            });
            if (response.ok) {
                const responseJson = await response.json();
                console.log('Task Created. Task ID:', responseJson);
                const notify = () => {
                    toast.success('Task Added', {
                        position: toast.POSITION.TOP_LEFT,
                        closeButton: false,
                    });
                    navigate('/project', { state: { new_arr: true } });
                    k.closeModel(false);
                };
                notify();
                resetForm();
            } else {
                const notify = () => {
                    toast.error('Something went wrong task Popup', {
                        position: toast.POSITION.TOP_LEFT,
                        closeButton: false,
                    });
                };
                notify();
                console.error('Failed to create the task');
            }
        } catch (error) {
            console.error('Error:', error);
        }
    };
    const handleFileChange = (event) => {
        setSelectedFile(event.target.files[0]);
    };
    const formatDateTimeInAMPM = (dateTimeStr) => {
        if (!dateTimeStr) {
            return 'N/A';
        }
        try {
            const date = new Date(dateTimeStr);
            if (isNaN(date)) {
                return 'Invalid Date';
            }
            const options = {
                year: 'numeric',
                month: 'numeric',
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit',
                hour12: true,
            };
            return date.toLocaleString('en-US', options);
        } catch (error) {
            console.error('Error while formatting date:', error);
            return 'Invalid Date';
        }
    };
    const [open, setOpen] = useState(false);
    const handleClickOpen = () => {
        setOpen(true);
    };
    const addWorkLog = (newWorkLog) => {
        setWorkLogHistory([...workLogHistory, newWorkLog]);
    };
    const handleClose = () => {
        setOpen(false);
    };
    const createWorkLog = async (workLogData, userEmail, callback) => {
        try {
            if (!userData || !userData.token) {
                const notify = () => {
                    toast.error('Token Not Present', {
                        position: toast.POSITION.TOP_LEFT,
                        closeButton: false,
                    });
                };
                notify();
                return;
            }
            const taskID = k.otherdata.id;
            const response = await fetch(`${process.env.REACT_APP_API_URL}/worklogs/${taskID}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${userData.token}`,
                },
                body: JSON.stringify({
                    loggedTimeString: workLogData.logTime,
                    startDate: workLogData.dateStarted,
                    logDescription: workLogData.workDescription,
                    userEmail,
                }),
            });
            if (response.ok) {
                const newWorkLog = await response.json();
                console.log('Work Log Created:', newWorkLog);
                setWorkLogs((prevWorkLogs) => [...prevWorkLogs, newWorkLog]);
                setShowWorkLogPopup(false);
            } else {
                const notify = () => {
                    toast.error('Failed to create work log', {
                        position: toast.POSITION.TOP_LEFT,
                        closeButton: false,
                    });
                };
                notify();
                console.error('Failed to create work log');
            }
        } catch (error) {
            console.error('Error:', error);
        }
    };
    const fetchWorkLogs = async () => {
        try {
            if (!userData || !userData.token) {
                const notify = () => {
                    toast.error('Token Not Present', {
                        position: toast.POSITION.TOP_LEFT,
                        closeButton: false,
                    });
                };
                notify();
                return;
            }
            const taskID = k.otherdata.id;
            const response = await fetch(`${process.env.REACT_APP_API_URL}/worklogs/task/${taskID}`, {
                method: 'GET',
                headers: {
                    Authorization: `Bearer ${userData.token}`,
                },
            });
            if (response.ok) {
                const workLogsData = await response.json();
                setWorkLogs(workLogsData);
            }
        } catch (error) {
        }
    };
    useEffect(() => {
        fetchWorkLogs();
    }, [fetchWorkLogs]);
    const handleDeleteWorkLog = async (workLogId) => {
        try {
            if (!userData || !userData.token) {
                const notify = () => {
                    toast.error('Token Not Present', {
                        position: toast.POSITION.TOP_LEFT,
                        closeButton: false,
                    });
                };
                notify();
                return;
            }
            const response = await fetch(`${process.env.REACT_APP_API_URL}/worklogs/${workLogId}`, {
                method: 'DELETE',
                headers: {
                    Authorization: `Bearer ${userData.token}`,
                },
            });

            if (response.ok) {
                const notify = () => {
                    toast.success('Work Log Deleted', {
                        position: toast.POSITION.TOP_LEFT,
                        closeButton: false,
                    });
                };
                notify();
                setWorkLogs((prevWorkLogs) => prevWorkLogs.filter((log) => log.id !== workLogId));
            } else {
                const notify = () => {
                    toast.error('Failed to delete work log', {
                        position: toast.POSITION.TOP_LEFT,
                        closeButton: false,
                    });
                };
                notify();
                console.error('Failed to delete work log');
            }
        } catch (error) {
            console.error('Error:', error);
        }
    };
    return (
        <>
            <div className={styles.container}>
                <div className={styles.taskpopup}>
                    <div className={styles.closebar}>
                        <div className={styles.editButton}>
                            <b>Create Issue</b>
                            {!isNewTask ? (
                                <Button variant="outlined" onClick={handleClickOpen}>
                                    <BiTrash size="1.5rem" color="rgba(6, 90, 215, 1)" />
                                </Button>
                            ) : null}
                            <Dialog
                                open={open}
                                onClose={handleClose}
                                aria-labelledby="alert-dialog-title"
                                aria-describedby="alert-dialog-description">
                                <DialogTitle id="alert-dialog-title">{"You really want to delete this task"}</DialogTitle>
                                <DialogContent>
                                    <DialogContentText id="alert-dialog-description">You won't retrieve this task again</DialogContentText>
                                </DialogContent>
                                <DialogActions>
                                    <Button onClick={handleClose}>No</Button>
                                    <Button onClick={() => DeleteTask(k.otherdata.id)} autoFocus>Yes
                                    </Button>
                                </DialogActions>
                            </Dialog>
                        </div>
                        <p className={styles.closeButton} onClick={() => k.closeModel(false)}>
                            X
                        </p>
                    </div>
                    <div className={styles.taskAddDetails}>
                        <form id="CreateTask" onSubmit={handleSubmit} className={styles.CreateTask}>
                            <p>{k.projectName}</p>
                            <div>
                                <InputForm
                                    type="text"
                                    placeholder="Task Details"
                                    name="taskDetails"
                                    id="taskDetails"
                                    value={taskDetails}
                                    onChange={(event) => setTaskDetails(event.target.value)}
                                    width="100%"
                                />
                            </div>
                            <div>
                                {!isNewTask ? (
                                    <div onClick={() => editTextArea(true)}>
                                        {value ? (
                                            <div>
                                                <b>Description</b>
                                                <div dangerouslySetInnerHTML={{ __html: k.otherdata.task_details }} />
                                            </div>
                                        ) : (
                                            <ReactQuill theme="snow" value={taskDescription} onChange={setTaskDescription} />
                                        )}
                                    </div>
                                ) : (
                                    <ReactQuill theme="snow" value={taskDescription} onChange={setTaskDescription} />
                                )}
                            </div>
                            <div>
                                <UserAssigne name="assigne" onSelect={(user) => setAssignee(user)} alredayDefined={k.otherdata && k.otherdata.assignee} />
                            </div>
                            <div>
                                <b>Reporter</b>
                                <p>{reporter_value}</p>
                            </div>
                            <div>
                                <AllStatusList
                                    onSelect={(status) => setStatusList(status)}
                                    alredayDefined={k.otherdata && k.otherdata.projectStatus && k.otherdata.projectStatus.status}
                                />
                            </div>
                            <div>
                                <ProjectPriority onSelect={(priority) => setPriority(priority)} alredayDefined={k.otherdata && k.otherdata.priority} />
                            </div>
                            <div className={styles.worklog}>
                                {!isNewTask && (
                                    <Button variant="outlined" onClick={handleWorkLogClick}>
                                        Add Work Log
                                    </Button>
                                )}
                            </div>
                            <div className={styles.workLogHistory}>
                                <h3 className={styles.title1}>History</h3>
                                <ul>
                                    {workLogs.map((workLog, index) => {
                                        const remainingTime = calculateRemainingTime(workLog.startDate, workLog.loggedTimeString);
                                        return (
                                            <li key={index} className="workLogHistory">
                                                <p>
                                                    Date Started: {formatDateTimeInAMPM(workLog.startDate)}
                                                    {!isNewTask && (
                                                        <AiOutlineDelete style={{ cursor: 'pointer', fontSize: '1.3rem', color: 'black', marginLeft: '248px' }} onClick={() => handleDeleteWorkLog(workLog.id)} />
                                                    )}
                                                </p>
                                                <p>Log Time: {workLog.loggedTimeString}</p>
                                                <p>Time Remaining: {remainingTime}</p>
                                                <p>Work Description: {workLog.logDescription} </p>
                                                <div style={{ color: 'darkslategray', marginLeft: '355px' }}>{workLog.userEmail}</div>
                                            </li>
                                        );
                                    })}
                                </ul>
                            </div>
                            <div>
                                <input type="file" onChange={handleFileChange} />
                            </div>
                            <ButtonName type="submit" buttonName="Submit" width="5.8rem" />
                        </form>
                    </div>
                </div>
            </div>
            {showWorkLogPopup && (
                <div className={styles.workLogPopup}>
                    <WorkLogPopup
                        onSave={(logTime, dateStarted, workDescription) => {
                            handleSave(logTime, dateStarted, workDescription);
                            const remainingTime = calculateRemainingTime(dateStarted, logTime);
                            const newWorkLogData = {
                                logTime,
                                dateStarted,
                                workDescription,
                                timeRemaining: remainingTime,
                            };
                            createWorkLog(newWorkLogData, addWorkLog);
                        }}
                        onCancel={handleCancel}
                        setWorkLogData={setWorkLogData}
                    />
                </div>
            )}
        </>
    );
};
export default TaskPopup; 