import { useState } from 'react';
import React, { useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import checklistImage from '../../components/images/createProject.png';
import styles from '../auth/login.module.css';
import LoginRegisterImg from '../../components/LoginRegiterImg';
import InputForm from '../../components/from/InputForm';
import ButtonName from '../../components/from/ButtonName';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';
import ProjectType from '../../API/ProjectType';
import 'react-toastify/dist/ReactToastify.css';
function CreateProject() {
    const navigate = useNavigate();
    const location = useLocation();
    let [projectName, setProjectName] = useState('');
    let [projectType, setProjectType] = useState('');
    let [projectKey, setProject_key] = useState('');
    let [methodPostLink, methodPutLink] = useState("/projects/create-project");
    let [methodPost, methodPut] = useState("POST");

    useEffect(() => {
        if (location.state != null) {
            methodPut("PUT")
            methodPutLink(`/projects/${location.state.id.projectId}`)
            setProjectName(location.state.id.projectName);
            setProjectType(location.state.id.projectType);
            setProject_key(location.state.id.projectKey);
        }
    }, [location.state]);

    const handleSubmit = async (event) => {
        event.preventDefault();
        if (!projectName) {
            const notify = () => {
                toast.error('Project name is required', {
                    position: toast.POSITION.TOP_LEFT,
                    closeButton: false

                });
            };
            notify()
            return;
        }
        if (!projectType) {
            const notify = () => {
                toast.error('Project type is required', {
                    position: toast.POSITION.TOP_LEFT,
                    closeButton: false
                });
            };
            notify()
            return;
        }
        if (!projectKey) {
            const notify = () => {
                toast.error('Project key is required', {
                    position: toast.POSITION.TOP_LEFT,
                    closeButton: false
                });
            };
            notify()
            return;
        }

        const data = { projectName, projectType, projectKey }
        try {
            const userData = JSON.parse(sessionStorage.getItem("userData"));
            const fetchData = async () => {
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
                const response = await fetch(`${process.env.REACT_APP_API_URL}${methodPostLink}`, {
                    method: `${methodPost}`,
                    headers: {
                        Authorization: `Bearer ${userData.token}`,
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(data),
                });
                if (response.ok) {
                    if (response.status === 201 || response.status === 200) {
                        const notify = () => {
                            toast.success(methodPost === "POST" ? 'Project added' : "Project Updated", {
                                position: toast.POSITION.TOP_LEFT,
                                closeButton: false
                            });
                        };
                        notify()
                        navigate("/projectlist")
                    } else {
                        const notify = () => {
                            toast.error('Something Went Wrong', {
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

        } catch (error) {
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
    const projectOptions = ProjectType();
    return (
        <>
            <div className={styles.mainLoginFrom}>
                <div className={styles.actualForm}>
                    <div className={styles.Loginform}>
                        <h1>{methodPost === "POST" ? 'Create Project' : "Update Project"}</h1>
                        <div>
                            <form onSubmit={handleSubmit}>
                                <InputForm type="text" value={projectName}
                                    onChange={(e) => setProjectName(e.target.value)} name="projectname"
                                    placeholder="projectName*"
                                    width="20rem"
                                />
                                <select
                                    className='form-control mr-sm-2 img-thumbnail'
                                    value={projectType}
                                    onChange={(e) => setProjectType(e.target.value)}
                                    required
                                >
                                    <option className='selectoption'>Select a project type</option>
                                    {projectOptions.map((type, i) => (
                                        <option key={i} value={type.projectType}>
                                            {type.projectType}
                                        </option>
                                    ))}
                                </select>
                              
                                <InputForm type="text"
                                    name="projectKey"
                                    value={projectKey}
                                    onChange={(e) => setProject_key(e.target.value)}
                                    placeholder="Project Key*"
                                    width="20rem"
                                />

                                <ButtonName type="submit" buttonName={methodPost === "POST" ? 'Create Project' : "Update Project"} width="20rem" />

                            </form>
                        </div>
                    </div>
                    <LoginRegisterImg imgAddress={checklistImage} width="50%" side="right" title="
Update Project ! " msg=" Take control of your tasks and boost your productivity with our powerful tools." />
                </div>
            </div>
        </>
    )
}

export default CreateProject;