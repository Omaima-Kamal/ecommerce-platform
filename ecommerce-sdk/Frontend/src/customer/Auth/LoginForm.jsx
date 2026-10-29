import { Button, Grid, TextField } from '@mui/material';
import React from 'react';
import { useDispatch } from 'react-redux';
import { login } from '../../State/Auth/Action';

export const LoginForm = ({ switchToRegister }) => {
    const dispatch = useDispatch();

    const handleSubmit = (event) => {
        event.preventDefault();

        const data = new FormData(event.currentTarget);

        const userData = {
            email: data.get("email"),
            password: data.get("password")
        }

        dispatch(login(userData));
    };

    return (
        <div>
            <form onSubmit={handleSubmit}>
                <Grid container spacing={3}>

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

                    {/* Login */}
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
                            Login
                        </Button>
                    </Grid>

                </Grid>
            </form>

            <div className='flex justify-center flex-col items-center'>
                <div className='py-3 flex items-center'>
                    <p>Not registered yet ? </p>
                    <Button onClick={switchToRegister} className='ml-5' size='small'>Register</Button>
                </div>
            </div>
        </div>
    );
};