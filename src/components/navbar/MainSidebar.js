import React from "react";
import { Outlet } from "react-router-dom";
import Sidebar from "./sidebar";
import styles from './sidebar.module.css'
import {useState} from 'react'
const MainSidebar=()=>{
        const [projectName, setProjectName] = useState('');
        return(
            <>
            <div className={styles.mainBar}>
                <Sidebar projectName={projectName}/>
                <div>
                <Outlet />
                </div>
            </div>
           </>
        )
}
export default MainSidebar;