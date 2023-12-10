import React, { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';
import 'react-toastify/dist/ReactToastify.css';
import Select from '@mui/material/Select'
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
const ProjectPriority = ({ onSelect, alredayDefined }) => {
  const [projectTypes, setProjectTypes] = useState([]);
  const navigate = useNavigate();
  const [projectType, setProjectType] = useState({ priority_id: '', priority_type: '' });

  useEffect(() => {
    const userData = JSON.parse(sessionStorage.getItem('userData'));

    const fetchData = async () => {
      if (!userData || !userData.token) {
        const notify = () => {
          toast.error('please Login', {
            position: toast.POSITION.TOP_LEFT,
            closeButton: false,
          });
          navigate('/login');
        };
        notify();
        return;
      }

      try {
        const response = await fetch(`${process.env.REACT_APP_API_URL}/priority/list-priority`, {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${userData.token}`,
            'Content-Type': 'application/json',
          },
        }).catch((e) => console.log('error' + e));

        if (response.status === 403) {
          const notify = () => {
            toast.error('Something Went wrong priority', {
              position: toast.POSITION.TOP_LEFT,
              closeButton: false,
            });
          };
          notify();
          navigate('/login');
          return;
        }

        if (!response.ok) {
          const notify = () => {
            toast.error('Response Not Ok', {
              position: toast.POSITION.TOP_LEFT,
              closeButton: false,
            });
          };
          notify();
          return;
        } else {
          const responseData = await response.json();
          setProjectTypes(responseData);

          if (alredayDefined) {
            const predefinedStatus = responseData.find((type) => type.priority_type === alredayDefined.priority_type);
            if (predefinedStatus) {
              setProjectType(predefinedStatus);
              // onSelect(predefinedStatus);
              return;
            }
          } else {
            setProjectType(responseData[2])
          }
        }
      } catch (error) {
        console.error(error);
        return;
      }
    };

    fetchData();
  }, [alredayDefined, navigate]);

  const handleSelectChange = (e) => {
    const selectedPriorityId = parseInt(e.target.value);
    const selectedStatus = projectTypes.find((type) => type.priority_id === selectedPriorityId);
    // setProjectType(selectedStatus || projectTypes[2]);
    setProjectType(selectedStatus || { priority_id: '', priority_type: '' });
    onSelect(selectedStatus);
  };

  return (
    <>
      {/* <select
      value={projectType.priority_id || ''}
      onChange={handleSelectChange}
    >
      <option value="" className="selectoption">
      Select Priority
      </option>
      {projectTypes.map((type) => (
        <option key={type.priority_id} value={type.priority_id}>
          {type.priority_type}
        </option>
      ))}
    </select> */}
      <FormControl fullWidth>
        <InputLabel id="demo-simple-select-label">{projectType.priority_type}</InputLabel>
        <Select
          fullWidth
          labelId="demo-simple-select-label"
          value={projectType.priority_id}
          onChange={handleSelectChange}
          label={projectType.priority_type}
        >
          {projectTypes.map((type) => (
            <MenuItem key={type.priority_id} value={type.priority_id}>{type.priority_type}</MenuItem>
          ))}
        </Select>
      </FormControl>
    </>
  );
};

export default ProjectPriority;