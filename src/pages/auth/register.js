import RegisterImg from '../../components/images/Checklist.png';
import LoginRegisterImg from '../../components/LoginRegiterImg';
import InputForm from '../../components/from/InputForm';
import styles from './login.module.css';
import ButtonName from '../../components/from/ButtonName';
const Register = () => {
    return (
        <>
            <div className={styles.mainLoginFrom}>
                <div className={styles.actualForm}>
                    <div className={styles.Loginform}>
                        <h1>Register</h1>
                        <div>
                            <form>
                                <InputForm type="text"
                                    placeholder="Username"
                                    name="username"
                                    width="20rem"
                                />
                                <InputForm type="email"
                                    placeholder="Email"
                                    name="email"
                                    width="20rem"
                                />
                                <InputForm type="password"
                                    placeholder="Password"
                                    width="20rem"
                                    name="password"
                                />
                                <InputForm type="password"
                                    placeholder="Confirm Password"
                                    width="20rem"
                                    name="confirmpassword"
                                />
                                <InputForm type="number"
                                    placeholder="Mobile Number"
                                    width="20rem"
                                    name="mobile_number"
                                />
                                <ButtonName type="submit" buttonName="Register" width="20rem" />
                                
                            </form>
                        </div>
                    </div>
                    <LoginRegisterImg imgAddress={RegisterImg} width="50%" side="right" title="
Register As Admin ! " msg=" Take control of your tasks and boost your productivity with our powerful tools." />
                </div>
            </div>
        </>
    )
}
export default Register;