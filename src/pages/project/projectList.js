import styles from './viewProjects.module.css';
import { useState, useTransition, useEffect } from 'react';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import InputForm from '../../components/from/InputForm';
import { useNavigate } from 'react-router-dom';
import { styled } from '@mui/material/styles';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell, { tableCellClasses } from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import IconButton from '@mui/material/IconButton';
import EditIcon from '@mui/icons-material/Edit';
import AddIcon from '@mui/icons-material/Add';
import Box from '@mui/material/Box';
import Skeleton from '@mui/material/Skeleton';
import DeleteProject from '../../API/DeleteProject';
import { useLocation } from 'react-router-dom';

const ProjectList = () => {
    const location = useLocation();
    const [rearr, setnewArr] = useState(false);
    useEffect(() => {
        if (location.state != null) {
            setnewArr(location.state.new_arr);
        }
    }, [location.state]);

    const StyledTableCell = styled(TableCell)(({ theme }) => ({
        [`&.${tableCellClasses.head}`]: {
            backgroundColor: 'rgba(6, 90, 215, 1)',
            color: theme.palette.common.white,
        },
        [`&.${tableCellClasses.body}`]: {
            fontSize: 14,
        },
    }));

    const StyledTableRow = styled(TableRow)(({ theme }) => ({
        '&:nth-of-type(odd)': {
            backgroundColor: theme.palette.action.hover,
        },
        '&:last-child td, &:last-child th': {
            border: 0,
        },
    }));

    const navigate = useNavigate();

    const handleEditClick = (item) => {
        navigate('/createproject', { state: { id: item } });
    };

    const handleCreateClick = () => {
        navigate('/add');
    };

    const [arr, newArr] = useState([]);
    const [data_validate, setdata_validate] = useState("Project List");

    useEffect(() => {
        const userData = JSON.parse(sessionStorage.getItem("userData"));

        const fetchData = async () => {
            if (!userData || !userData.token) {
                const notify = () => {
                    toast.error("Please Login", {
                        position: toast.POSITION.TOP_LEFT,
                        closeButton: false,
                    });
                    navigate("/login");
                };
                notify();
                return;
            }

            try {
                const response = await fetch(`${process.env.REACT_APP_API_URL}/projects`, {
                    method: "GET",
                    headers: {
                        Authorization: `Bearer ${userData.token}`,
                        "Content-Type": "application/json",
                    },
                }).catch((e) => console.log("error" + e));

                if (response.status === 403) {
                    const notify = () => {
                        toast.error("Check Login", {
                            position: toast.POSITION.TOP_LEFT,
                            closeButton: false,
                        });
                    };
                    notify();
                    navigate("/login");
                    return;
                }

                if (response.status === 204) {
                    setdata_validate("No Data Found");
                    return;
                }

                if (!response.ok) {
                    const notify = () => {
                        toast.error("Please Login", {
                            position: toast.POSITION.TOP_LEFT,
                            closeButton: false,
                        });
                        navigate("/login");
                    };
                    notify();
                    newArr({ projectName: "No data Found", projectKey: "asdas" });
                    return;
                } else {
                    const responseData = await response.json();
                    if (responseData.length <= 0) {
                        setdata_validate("No Data Found");
                    }
                    newArr(responseData);
                }
            } catch (error) {
                const notify = () => {
                    toast.error("Please Login", {
                        position: toast.POSITION.TOP_LEFT,
                        closeButton: false,
                    });
                    navigate("/");
                };
                notify();
                console.log(error);
                return;
            }
        };

        fetchData();

        if (rearr === true) {
            fetchData();
            setnewArr(false);
        }
    }, [navigate, rearr]);

    const [query, setQuery] = useState('');
    const [inputValue, setInputValue] = useState('');
    const [isPending, startTransition] = useTransition();

    const changeHandler = (event) => {
        setInputValue(event.target.value);
        startTransition(() => {
            setQuery(event.target.value);
        });
    };

    const handleToViewDetails = (item) => {
        navigate('/project');
        sessionStorage.setItem('projectdata', JSON.stringify(item));
    };

    const filterHandler = arr.filter((item) => {
        item["method"] = "PUT";
        return (
            item.projectName.toLowerCase().includes(query.toLowerCase()) ||
            item.projectKey.toLowerCase().includes(query.toLowerCase()) ||
            item.createdBy.toLowerCase().includes(query.toLowerCase())
        );
    });

    return (
        <>
            <section className={styles.MainProjectList}>
                <h1>{data_validate}</h1>
                <div className={styles.ProjectList}>
                    <InputForm inputValue={inputValue} onChange={changeHandler} className={styles.projectListInput} placeholder="Search Project" />
                    <TableContainer component={Paper}>
                        <Table aria-label="customized table">

<TableHead>
    <TableRow>
        <StyledTableCell align="left">Name</StyledTableCell>
        <StyledTableCell align="left">Type</StyledTableCell>
        <StyledTableCell align="left">User</StyledTableCell>
        <StyledTableCell align="left">Key</StyledTableCell>
        <StyledTableCell align="left">Action</StyledTableCell>
    </TableRow>
</TableHead>
<TableBody>
    {filterHandler.map((row, i) => (
        <StyledTableRow key={i}>
            <StyledTableCell align="left" onClick={() => handleToViewDetails(row)} style={{ cursor: 'pointer' }}>
                {isPending ? (
                    <Box>
                        <Skeleton />
                    </Box>
                ) : (
                    row.projectName
                )}
            </StyledTableCell>
            <StyledTableCell align="left">
                {isPending ? (
                    <Box>
                        <Skeleton />
                    </Box>
                ) : (
                    row.projectType
                )}
            </StyledTableCell>
            <StyledTableCell align="left">
                {isPending ? (
                    <Box>
                        <Skeleton />
                    </Box>
                ) : (
                    row.createdBy
                )}
            </StyledTableCell>
            <StyledTableCell align="left">
                {isPending ? (
                    <Box>
                        <Skeleton />
                    </Box>
                ) : (
                    row.projectKey
                )}
            </StyledTableCell>
            <StyledTableCell style={{ width: '150px' }}>
    {isPending ? (
        <Box>
            <Skeleton />
        </Box>
    ) : (
        <>
            <IconButton aria-label="create" onClick={handleCreateClick} style={{ marginRight: '8px' }}>
                <AddIcon fontSize="small" />
            </IconButton>
            <IconButton aria-label="edit" onClick={() => handleEditClick(row)} style={{ marginRight: '8px' }}>
                <EditIcon fontSize="small" />
            </IconButton>
            <IconButton>
            <DeleteProject prId={row.projectId} /></IconButton>
        </>
    )}
</StyledTableCell>

        </StyledTableRow>
    ))}
</TableBody>

                        </Table>
                    </TableContainer>
                </div>
            </section>
        </>
    );
};

export default ProjectList;
