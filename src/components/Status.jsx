import styles from './TaskStatus.module.css'
import { BiDotsHorizontalRounded } from "react-icons/bi";
import React, { useState } from 'react';
import TaskPopup from './TaskPopup';
const Status = (k) => {
    const [dataValidate, setDataValidate] = useState(false);

    const borderRadiusStyle = {
        color: k.color,
    };
    // const priority_style = {
    //     backgroundColor: k.priority.color,
    // };
    const dragStared = (e, name) => {

        e.dataTransfer.setData("task", name)
    }
    const createIssue = (value=false) => {
        setDataValidate(value)
    }
    return (
        <>
        <div>
                 {dataValidate&&<TaskPopup closeModel={createIssue} projectName={k.projectName} projectId={k.projectId}  apiRequest={`${process.env.REACT_APP_API_URL}/task/update-task/${k.id}`} apiMethod="PUT" otherdata={k.taskPopupData}/>}
        </div>
            <div className={styles.status} style={borderRadiusStyle}>
                <div className={styles.status_container} draggable={true} onDragStart={(e) => dragStared(e, k.id)}>
                    <div className={styles.status_data}>
                        <div>
                            <p>{k.name}</p>
                            <p>{k.taskPopupData.assignee}</p>
                        </div>
                        <div className={styles.status_data}>
                        <div className={styles.editButton} onClick={()=>createIssue(true)}  > <BiDotsHorizontalRounded size="1.5rem" color="rgba(6,90,215,1)" /></div> 
                        </div>
                    </div>
                    <div>
                    <img src={`/jira_priority/${k.priority.color}.png`} alt={k.priority.color}/>
                        {/* <em style={priority_style} className={styles.priority_class}>{k.priority.priority_type}</em> */}
                    </div>
                </div>
            </div>
        </>
    )
}
export default Status;