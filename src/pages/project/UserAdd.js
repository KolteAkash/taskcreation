import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './User.css';
import Button from '@mui/material/Button';

function UserAdd() {
    const [userData, setUserData] = useState({
        user_name: '',
        email: '',
        password: '',
        mobile_number: '',
    });

    const navigate = useNavigate();

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setUserData((prevData) => ({
            ...prevData,
            [name]: value,
        }));
    };


    const handleAddUser = async () => {
        try {
            const userToken = JSON.parse(sessionStorage.getItem('userData'))?.token;

            if (!userToken) {
                console.error('Token not found. Redirect to login or show an error.');
                return;
            }

            const response = await fetch(`${process.env.REACT_APP_API_URL}/admincontroll/create_user`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${userToken}`,
                },
                body: JSON.stringify(userData),
            });

            if (response.ok) {
                console.log('User created successfully');
                navigate('/view', { state: { userAdded: true } });
            } else {
                const result = await response.json();
                console.error('Error creating user:', result.body);

                const errorMessage = result.body || 'Error creating user';
                toast.error(errorMessage, {
                    position: toast.POSITION.TOP_LEFT,
                    closeButton: false,
                });
            }
        } catch (error) {
            console.error('Error creating user:', error.message);
        }
    };

    return (
        <div className="user-add-container">
            <h3 className='adduser'>Create User</h3>
            <form>
                <div>
                    <input
                        type="text"
                        name="user_name"
                        onChange={handleInputChange}
                        placeholder="Username"
                    />
                </div>
                <div>
                    <input
                        type="email"
                        name="email"
                        onChange={handleInputChange}
                        placeholder="Email"
                    />
                </div>
                <div>
                    <input
                        type="password"
                        name="password"
                        onChange={handleInputChange}
                        placeholder="Password"
                    />
                </div>
                <div>
                    <input
                        type="tel"
                        name="mobile_number"
                        onChange={handleInputChange}
                        placeholder="Mobile Number"
                    />
                </div>
                <Button type="button" variant="outlined" onClick={handleAddUser}>
                    Submit
                </Button>

            </form>
        </div>
    );
}

export default UserAdd;