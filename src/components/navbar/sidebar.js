import React, { useState } from 'react';
import { NavLink} from 'react-router-dom'; 
import { BiUser, BiListMinus, BiDetail, BiTime } from 'react-icons/bi';
import styles from './sidebar.module.css';
import { AiOutlineFile, AiOutlineSetting, AiOutlineProject } from 'react-icons/ai';
import TaskPopup from '../TaskPopup';

const Sidebar = (k) => {
    const [activeMenu, setActiveMenu] = useState(null);
    const [devactiveMenu, devsetActiveMenu] = useState(null);
    const [dataValidate, setDataValidate] = useState(false);

    const handleMenuClick = (menu) => {
        setActiveMenu(menu === activeMenu ? null : menu);
    };

    const devhandleMenuClick = (menu) => {
        devsetActiveMenu(menu === devactiveMenu ? null : menu);
    };

    const createIssue = (value = false) => {
        setDataValidate(value);
    };

    return (
        <div className={styles.sidebar}>
            <div className={styles.taskPopup}>
                {dataValidate && (
                    <TaskPopup
                        closeModel={createIssue}
                        projectName={k.projectName}
                        projectId={k.projectId}
                        apiRequest={`${process.env.REACT_APP_API_URL}/task/create-task`}
                        apiMethod="POST"
                    />
                )}
            </div>
            <div className={styles.userDetails}>
                <BiUser style={{ fontSize: '1.3rem' }} />
                <div>
                    <p>
                        <b>{k.userName}</b>
                    </p>
                    <p>{k.projectName}</p>
                </div>
            </div>
            <div className={styles.menus}>
                {k.projectName && (
                    <div>
                        <button onClick={() => createIssue(true)}>Create Task</button>
                        {/* <NavLink to="/weekly" className={styles.track}>
                            <BiTime style={{ fontSize: '13px', marginTop: '7px' ,gap: '2rem'}} /> 
                            Track
                        </NavLink> */}
                    </div>
                )}
                <p
                    className={`${styles.menuTitle} ${activeMenu === 'planning' ? styles.active : ''}`}
                    onClick={() => handleMenuClick('planning')}
                >
                    PLANNING
                </p>

                <ul className={`${styles.menuList} ${activeMenu === 'planning' ? styles.active : ''}`}>
                    <NavLink to="/taskmanager/projectlist" className={styles.menuLink}>
                        <li>
                            <BiListMinus style={{ fontSize: '1.2rem' }} />
                            &nbsp; Roadmap
                        </li>
                    </NavLink>
                    <NavLink to="/taskmanager/checksidebar" className={styles.menuLink}>
                        <li>
                            <BiDetail style={{ fontSize: '1.2rem' }} />
                            &nbsp; Board
                        </li>
                    </NavLink>
                </ul>
                <p
                    className={`${styles.menuTitle} ${devactiveMenu === 'development' ? styles.active : ''}`}
                    onClick={() => devhandleMenuClick('development')}
                >
                    DEVELOPMENT
                </p>
                <ul className={`${styles.menuList} ${devactiveMenu === 'development' ? styles.active : ''}`}>
                    <li>
                        <BiListMinus style={{ fontSize: '1rem' }} />
                        &nbsp; Code
                    </li>
                </ul>
            </div>
            <hr />
            <div className={styles.sidebarEnd}>
                <p>
                    <AiOutlineFile style={{ fontSize: '1.5rem' }} /> &nbsp; Project Pages
                </p>
                <p>
                    <AiOutlineProject style={{ fontSize: '1.5rem' }} />&nbsp; Add Shortcut
                </p>
                <p>
                    <AiOutlineSetting style={{ fontSize: '1.5rem' }} /> &nbsp; Project Settings
                </p>
            </div>
        </div>
    );
};

export default Sidebar;