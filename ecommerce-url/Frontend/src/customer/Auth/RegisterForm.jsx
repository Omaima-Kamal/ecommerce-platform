import { Button, Grid, TextField } from '@mui/material';
import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { getUser, register } from '../../State/Auth/Action';

export const RegisterForm = ({ switchToLogin }) => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const jwt = localStorage.getItem("jwt");
    const {auth} = useSelector(store=>store) 

    useEffect(()=> {
        if(jwt) {
            dispatch(getUser(jwt))
        }
    },[jwt,auth.jwt])

    const handleSubmit = (event) => {
        event.preventDefault();

        const data = new FormData(event.currentTarget);

        const userData = {
            firstName:data.get("firstName"),
            lastName:data.get("lastName"),
            email:data.get("email"),
            password:data.get("password")
        }
        dispatch(register(userData));
    };

    return (
        <div>
            <form onSubmit={handleSubmit}>
                <Grid container spacing={3}>

                    {/* First Name */}
                    <Grid size={{ xs: 12, sm: 6 }}>
                        <TextField
                            required
                            id="firstName"
                            name="firstName"
                            label="First Name"
                            fullWidth
                            autoComplete="given-name"
                        />
                    </Grid>

                    {/* Last Name */}
                    <Grid size={{ xs: 12, sm: 6 }}>
                        <TextField
                            required
                            id="lastName"
                            name="lastName"
                            label="Last Name"
                            fullWidth
                            autoComplete="family-name"
                        />
                    </Grid>

                    {/* Email */}
                    <Grid size={12}>
                        <TextField
                            required
                            id="email"
                            name="email"
                            label="Email"
                            type="email"
                            fullWidth
                            autoComplete="email"
                        />
                    </Grid>

                    {/* Password */}
                    <Grid size={12}>
                        <TextField
                            required
                            id="password"
                            name="password"
                            label="Password"
                            type="password"
                            fullWidth
                            autoComplete="new-password"
                        />
                    </Grid>

                    {/* Register */}
                    <Grid size={12}>
                        <Button
                            className="bg-[#9155FD] w-full"
                            type="submit"
                            variant="contained"
                            size="large"
                            sx={{
                                padding: ".8rem 0"
                            }}
                        >
                            Register
                        </Button>
                    </Grid>

                </Grid>
            </form>

            <div className='flex justify-center flex-col items-center'>
                <div className='py-3 flex items-center'>
                    <p>Already have an account ? </p>
                    <Button onClick={switchToLogin} className='ml-5' size='small'>Login</Button>
                </div>
            </div>
        </div>
    );
};