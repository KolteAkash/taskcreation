import { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';
import 'react-toastify/dist/ReactToastify.css';

const ProjectType= () => {
  const navigate = useNavigate();
  const [projectOptions,setProjectOptions]=useState([]);
  useEffect(() => {
    const fetchData = async () => {
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
        const response = await fetch(`${process.env.REACT_APP_API_URL}/project_type/list-project-type`, {
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
        setProjectOptions(responseData);
      } catch (error) {
        console.error(error);
        return;
      }
    };

    fetchData();
  }, [navigate]);
  return projectOptions;
};

export default ProjectType;