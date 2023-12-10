import loginImg from '../../components/images/login.png';
import LoginRegisterImg from '../../components/LoginRegiterImg';
import InputForm from '../../components/from/InputForm';
import styles from './login.module.css';
import {Link} from 'react-router-dom';
import { useState } from 'react';
import { useNavigate } from "react-router-dom";
import {  toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import Button from '@mui/material/Button';
const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();
    const handleSubmit = async (event) => {
      event.preventDefault();
      const data = { email, password };
      try {
        const response = await fetch(`${process.env.REACT_APP_API_URL}/api/v1/auth/login`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(data),
        }).catch(e=>console.log("error "+e));
        const result = await response.json();
          if(response.status===200){
            sessionStorage.setItem('userData', JSON.stringify(result));
            navigate("/");
          }
         else {
            throw new Error(result.body); 
        }
      } catch (error) {
        const errorMessage = error.message || "Error Notification!";
        const notify = () => {
          toast.error(errorMessage, {
            position: toast.POSITION.TOP_LEFT,
            closeButton: false
         
          });
        };
        notify()
        console.error(error);
      }
      

    };
    return (
      <>
          <div className={styles.mainLoginFrom}>
              <div className={styles.actualForm}>
                  <LoginRegisterImg imgAddress={loginImg} width="50%" side="left" title="
Welcome to our sign-in page! " msg=" Take control of your tasks and boost your productivity with our powerful tools."/>
                  <div className={styles.Loginform}>
                      <h1>Login</h1>
                      <div>
                          <form onSubmit={handleSubmit}>
                              <InputForm type="email"
                                  placeholder="Email"
                                  name="email"
                                  width="20rem"
                              value={email} 
                              onChange={(e) => setEmail(e.target.value) } 
                              />
                              <InputForm type="password"
                                  placeholder="Password"
                                  width="20rem"
                                  name="password"
                              value={password} 
                              onChange={(e) => setPassword(e.target.value) } 
                              />
                              <Button type="submit"  variant="outlined">Login</Button>
                              <Link to="/register">New Here </Link>
                          </form>
                      </div>
                  </div>
              </div>
          </div>
      </>
  )
}
export default Login;