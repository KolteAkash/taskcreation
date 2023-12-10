import { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';
import 'react-toastify/dist/ReactToastify.css';
import TextField from '@mui/material/TextField';
import Stack from '@mui/material/Stack';
import Autocomplete from '@mui/material/Autocomplete';

const UserAssigne = ({ onSelect, alredayDefined }) => {
  const [allUserData, setUserData] = useState([]);
  const navigate = useNavigate();
  const [inputValue, setInputValue] = useState('');


  useEffect(() => {
    if (alredayDefined) {
      setInputValue(alredayDefined);
    }
  }, [alredayDefined]);

  useEffect(() => {
    const fetchData = async () => {
      const projectdata = JSON.parse(sessionStorage.getItem("projectdata"));
      const userData = JSON.parse(sessionStorage.getItem("userData"));

      if (!userData || !userData.token) {
        toast.error("Please log in", {
          position: toast.POSITION.TOP_LEFT,
          closeButton: false
        });
        navigate("/login");
        return;
      }

      try {
        const response = await fetch(`${process.env.REACT_APP_API_URL}/admincontroll/users/${projectdata.projectId}`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${userData.token}`,
            "Content-Type": "application/json",
          },
        });

        if (response.status === 403) {
          navigate("/login");
          return;
        }

        if (!response.ok) {
          toast.error("Response not OK", {
            position: toast.POSITION.TOP_LEFT,
            closeButton: false
          });
          return;
        }

        const responseData = await response.json();
        setUserData(responseData);
      } catch (error) {
        console.error(error);
        return;
      }
    };

    fetchData();
  }, [navigate]);

  const handleUserClick = (user) => {
    setInputValue(user.email);
    onSelect(user.email);
  };
  return (
    <div>
      <Stack spacing={2} sx={{ width: 300 }}>
        <Autocomplete
          freeSolo
          id="free-solo-2-demo"
          disableClearable
          options={allUserData}
          getOptionLabel={(option) => option.email}
          inputValue={inputValue}
          onInputChange={(event, value) => setInputValue(value)}
          onChange={(event, value) => {
            if (value) {
              handleUserClick(value);
            }
          }}
          renderInput={(params) => (
            <TextField
              {...params}
              label="Assigne User"
              InputProps={{
                ...params.InputProps,
                type: 'search',
              }}
            />
          )}
        />
      </Stack>
    </div>
  );
};

export default UserAssigne;
