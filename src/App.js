import './App.css';
import MainHeader from './components/navbar/MainHeader';
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Trial from './pages/project/trial';
import Createproject from './pages/project/CreateProject';
import ProjectDetails from './pages/project/projectDetails';
import ProjectList from './pages/project/projectList';
import Login from './pages/auth/login';
import Register from './pages/auth/register';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
// import WeeklyTimeSheet from './components/WeeklyTimeSheet';
import UserProjectRelation from './pages/project/UserProjectRelation';
import UserAdd from './pages/project/UserAdd';

function App() {
  return (
    <>
      <BrowserRouter>
        <ToastContainer />
        <Routes>
          <Route path="/" element={<MainHeader />}>
            <Route path="filter" element={<Trial />} />
            <Route path="projectlist" element={<ProjectList />} />
            <Route path="login" element={<Login />} />
            <Route path="createproject" element={<Createproject />} />
            <Route path="register" element={<Register />} />
            <Route path="project" element={<ProjectDetails />} />
            {/* <Route path="weekly" element={<WeeklyTimeSheet />} /> */}
              <Route path="view" element={<UserProjectRelation />} />
              <Route path="add" element={<UserAdd />} />
            <Route path='*' element={<Login />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </>
  );
}

export default App;