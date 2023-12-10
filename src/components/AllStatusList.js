import { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';
import 'react-toastify/dist/ReactToastify.css';
// import InputLabel from '@mui/material/InputLabel';
// import MenuItem from '@mui/material/MenuItem';
// import Select  from '@mui/material/Select'
// import FormControl from '@mui/material/FormControl';
const AllStatusList = ({ onSelect, alredayDefined }) => {
    const [projectTypes, setProjectTypes] = useState([]);
    const navigate = useNavigate();
    let [projectType, setProjectType] = useState({});
    useEffect(() => {
        const userData = JSON.parse(sessionStorage.getItem("userData"));
        const projectdata = JSON.parse(sessionStorage.getItem("projectdata"));
        const fetchData = async () => {
            if (!userData || !userData.token) {
                const notify = () => {
                    toast.error("Please Login", {
                        position: toast.POSITION.TOP_LEFT,
                        closeButton: false
                    });
                };
                notify()
                return;
            }
            try {
                const response = await fetch(`${process.env.REACT_APP_API_URL}/task/status/all-project-status/${projectdata.projectId}`, {
                    method: "GET",
                    headers: {
                        Authorization: `Bearer ${userData.token}`,
                        "Content-Type": "application/json",
                    },
                }).catch(e => console.log("error" + e));
                if (response.status === 403) {
                    const notify = () => {
                        toast.error("Something Went wrong All status", {
                            position: toast.POSITION.TOP_LEFT,
                            closeButton: false
                        });
                    };
                    notify()
                    navigate("/login");
                    return;
                };
                if (!response.ok) {
                    const notify = () => {
                        toast.error("Response Not Ok", {
                            position: toast.POSITION.TOP_LEFT,
                            closeButton: false
                        });
                    };
                    notify()
                    return;
                } else {
                    const responseData = await response.json();
                    setProjectTypes(responseData)
                    if (alredayDefined) {
                        const predefinedStatus = responseData.find((type) => type.status === alredayDefined.status);
                        if (predefinedStatus) {
                            setProjectType(predefinedStatus);
                            //   onSelect(predefinedStatus);
                            return;
                        }
                    } else {
                        setProjectType(responseData[0] || {});
                    }

                }
            } catch (error) {
                console.error(error);
                return;
            }
        };
        fetchData();
    }, [alredayDefined, navigate]);

    useEffect(() => {
        onSelect(projectType);
    }, [onSelect, projectType]);
    const handleSelectChange = (e) => {
        const selectedStatus = projectTypes.find(type => type.status === e.target.value);
        setProjectType(selectedStatus);
    };
    return (
        <>
            <select
                className='form-control mr-sm-2 img-thumbnail'
                value={projectType.status}
                onChange={handleSelectChange}
            >
                {projectTypes.map((type, i) => (
                    <option key={i} value={type.status}>
                        {type.status}
                    </option>
                ))}
            </select>
            {/* <FormControl fullWidth>
            <InputLabel id="demo-simple-select-label">{projectType.status}</InputLabel>
            <Select fullWidth
                labelId="demo-simple-select-label"
                value={projectType.status}
                onChange={handleSelectChange}
                label={projectType.status}
            >
                {projectTypes.map((type) => (
                    <MenuItem key={type.status_id} value={type.status}>{type.status}</MenuItem>
                ))}
            </Select>
            </FormControl> */}
        </>

    )
}
export default AllStatusList;