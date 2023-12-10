import React, { useState, useEffect } from 'react';
import './UserProject.css';
import { styled } from '@mui/material/styles';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell, { tableCellClasses } from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import IconButton from '@mui/material/IconButton';
import AssignmentTurnedInIcon from '@mui/icons-material/AssignmentTurnedIn';
import FormControl from '@mui/material/FormControl';
import Autocomplete from '@mui/material/Autocomplete';

const StyledTableRow = styled(TableRow)(({ theme }) => ({
  '&:nth-of-type(odd)': {
    backgroundColor: theme.palette.action.hover,
  },
  '&:last-child td, &:last-child th': {
    border: 0,
  },
}));

const StyledTableCell = styled(TableCell)(({ theme }) => ({
  [`&.${tableCellClasses.head}`]: {
    backgroundColor: 'rgba(6, 90, 215, 1)',
    color: theme.palette.common.white,
  },
  [`&.${tableCellClasses.body}`]: {
    fontSize: 14,
  },
}));

function UserView() {
  const [assignments, setAssignments] = useState([]);
  const [selectedProject, setSelectedProject] = useState('');
  const [selectedUser, setSelectedUser] = useState('');
  const [projects, setProjects] = useState([]);
  const [users, setUsers] = useState([]);
  const [error, setError] = useState(null);
  const userToken = JSON.parse(sessionStorage.getItem('userData'))?.token;

  useEffect(() => {
    if (!userToken) {
      console.error('Token not found or invalid. Redirect to login or show an error.');
      return;
    }

    const fetchData = async () => {
      try {
        await fetchProjectsFromApi(userToken);
        await fetchUsersFromApi(userToken);
      } catch (error) {
        console.error('Error fetching data:', error.message);
        setError('Error fetching data. Please try again later.');
      }
    };

    fetchData();
  }, [userToken]);

  const fetchProjectsFromApi = async (userToken) => {
    try {
      const response = await fetch(`${process.env.REACT_APP_API_URL}/projects`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${userToken}`,
        },
      });

      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      const data = await response.json();
      console.log('Fetched projects:', data);
      setProjects(data);
    } catch (error) {
      console.error('Error fetching projects:', error.message);
      setError('Error fetching projects. Please try again later.');
    }
  };

  const fetchUsersFromApi = async (userToken) => {
    try {
      const response = await fetch(`${process.env.REACT_APP_API_URL}/admincontroll/client/users`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${userToken}`,
        },
      });

      if (!response.ok) {
        console.error('HTTP error! Status:', response.status);
        const errorText = await response.text();
        console.error('Error details:', errorText);
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      const data = await response.json();

      if (!data || data.length === 0) {
        console.warn('Empty response or invalid JSON format.');
        setUsers([]);
      } else {
        console.log('Fetched users:', data);
        setUsers(data);
      }
    } catch (error) {
      console.error('Error fetching users:', error.message);
    }
  };

  const createUserProjectRelation = async (userToken, relationData) => {
    try {
      const response = await fetch(`${process.env.REACT_APP_API_URL}/projects/relation-user-project`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${userToken}`,
        },
        body: JSON.stringify(relationData), 
      });
  
      if (!response.ok) {
        const errorMessage = await response.text();
        throw new Error(`HTTP error! Status: ${response.status}. Details: ${errorMessage}`);
      }
  
      const data = await response.json();
      console.log('User-Project relation created:', data);
    } catch (error) {
      console.error('Error creating user-project relation:', error.message);
      throw error;
    }
  };
  
  const handleAssignUser = async () => {
    if (selectedProject && selectedUser) {
      const project = projects.find((project) => project.projectId === Number(selectedProject));
      const user = users.find((user) => user.email === selectedUser);
  
      if (project && user) {
        const newAssignment = { project: project.projectName, user: user.email };
        setAssignments([...assignments, newAssignment]);
        setSelectedProject('');
        setSelectedUser('');
  
        try {
          const relationData = [
            {
              projectId: project.projectId,
              userName: user.email,
            },
          ];
  
          await createUserProjectRelation(userToken, relationData);
        } catch (error) {
          console.error('Error creating user-project relation:', error.message);
        }
      } else {
        alert('Selected project or user not found.');
      }
    } else {
      alert('Please select both a project and a user.');
    }
  };
  
  const handleRemoveAssignment = (index) => {
    const updatedAssignments = [...assignments];
    updatedAssignments.splice(index, 1);
    setAssignments(updatedAssignments);
  };
  return (
    <div className="user-view-container">
      <h2>Assigned Project To User</h2>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <TableContainer>
        <Table>
          <TableHead>
            <TableRow>
              <StyledTableCell>Project</StyledTableCell>
              <StyledTableCell>User</StyledTableCell>
              <StyledTableCell>Action</StyledTableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {assignments.map((assignment, index) => (
              <StyledTableRow key={index}>
                <StyledTableCell>{assignment.project}</StyledTableCell>
                <StyledTableCell>{assignment.user}</StyledTableCell>
                <StyledTableCell>
                  <IconButton onClick={() => handleRemoveAssignment(index)}>
                    <AssignmentTurnedInIcon />
                  </IconButton>
                </StyledTableCell>
              </StyledTableRow>
            ))}
            <StyledTableRow>
              <StyledTableCell>
                <FormControl>
                  <Autocomplete
                    options={projects}
                    getOptionLabel={(option) => option.projectName}
                    value={projects.find((project) => project.projectId === selectedProject) || null}
                    onChange={(_, newValue) => setSelectedProject(newValue?.projectId || '')}
                    renderInput={(params) => (
                      <div ref={params.InputProps.ref}>
                        <input {...params.inputProps} placeholder="Select Project" className="autocomplete-inputuser" />
                      </div>
                    )}
                  />
                </FormControl>
              </StyledTableCell>
              <StyledTableCell>
                <FormControl>
                  <Autocomplete
                    options={users}
                    getOptionLabel={(option) => option.email}
                    value={users.find((user) => user.email === selectedUser) || null}
                    onChange={(_, newValue) => setSelectedUser(newValue?.email || '')}
                    renderInput={(params) => (
                      <div ref={params.InputProps.ref}>
                        <input {...params.inputProps} placeholder="Select User" className="autocomplete-inputuser" />
                      </div>
                    )}
                  />
                </FormControl>
              </StyledTableCell>
              <StyledTableCell>
                <IconButton onClick={handleAssignUser}>
                  <AssignmentTurnedInIcon />
                </IconButton>
              </StyledTableCell>
            </StyledTableRow>
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
}

export default UserView;