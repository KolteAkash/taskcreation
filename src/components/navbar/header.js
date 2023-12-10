import { NavLink } from "react-router-dom";
import React, { useEffect } from "react";
import styles from './navbar.module.css';
import { BiCaretDown } from "react-icons/bi";
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';
import 'react-toastify/dist/ReactToastify.css';

function Header() {
    const userData = JSON.parse(sessionStorage.getItem("userData"));
    const navigate = useNavigate();

    const handleLogout = () => {
        sessionStorage.removeItem("userData");

        // Show logout toast message
        toast.info("Logged out successfully", {
            position: toast.POSITION.TOP_LEFT,
            closeButton: false
        });

        // Redirect to login page
        navigate("/login");
    };

    useEffect(() => {
        if (!userData || !userData.token) {
            navigate("/login");
        }
    }, [userData, navigate]);

    return (
        <header className={styles.header}>
         {/* <header className={`${styles.header} ${styles.fixedHeader}`}> */}
            <nav className={styles.mainNav}  >
                <div className={styles.logo}>
                    <p>Task Manager</p>
                </div>
                <ul>
                    <li>Your Work <BiCaretDown />
                        <ul>
                            <NavLink to="/projectlist"> <li>Assigned to me</li></NavLink>
                            <NavLink to="/project"> <li>Dont Know</li></NavLink>
                        </ul>
                    </li>
                    <li>Projects <BiCaretDown />
                        <ul>
                            <NavLink to="/projectlist"> <li>View</li></NavLink>
                            <NavLink to="/createproject"> <li>Create</li></NavLink>
                        </ul>
                    </li>
                    <li>Client <BiCaretDown />
                        <ul>
                            <NavLink to="/projectlist"> <li>View</li></NavLink>
                            <NavLink to="/projectDetails/checksidebar"> <li>Create</li></NavLink>
                        </ul>
                    </li>
                    <NavLink to="/filter"><li>Teams</li></NavLink>
                    <li>Admin <BiCaretDown />
                        <ul>
                            <NavLink to="/view"> <li>Assign project</li></NavLink>
                            <NavLink to="/add"> <li>Create User</li></NavLink>
                        </ul>
                    </li>
                    <li>Client <BiCaretDown />
                        <ul>
                            <NavLink to="/projectlist"> <li>Invite People</li></NavLink>
                        </ul>
                    </li>
                    {userData ? <li onClick={handleLogout}>Logout</li> : <NavLink to="/login"><li>SignUp</li></NavLink>}
                </ul>
            </nav>
        </header>
    );
}

export default Header;
