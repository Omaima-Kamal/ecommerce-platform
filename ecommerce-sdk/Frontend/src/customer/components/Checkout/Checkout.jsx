import { Box, Button, Step, StepLabel, Stepper, Typography } from '@mui/material';
import * as React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { DeliveryAddressForm } from './DeliveryAddressForm';
import { OrderSummary } from './OrderSummary';
import { PaymentSuccess } from '../../Payment/PaymentSuccess';

const steps = ['Login', 'Delivery Address', 'Order Summary', 'Payment'];

export const Checkout = () => {
    const location = useLocation();
    const navigate = useNavigate();

    const querySearch = new URLSearchParams(location.search);
    const step = Number(querySearch.get('step') || 0);
    const searchParams = new URLSearchParams(location.search);
    const orderId = searchParams.get('order_id');

    const handleNext = () => {
        if (step === 2) {
            navigate(`/payment/${orderId}`);
            return;
        }
        navigate(`?step=${step + 1}`);
    };

    const handleBack = () => {
        navigate(`?step=${step - 1}`);
    };

    return (
        <div className="px-10 lg:px-20 mb-5 mt-10">
            <Box sx={{ width: '100%' }}>

                <Stepper activeStep={step}>
                    {steps.map((label) => (
                        <Step key={label}>
                            <StepLabel>{label}</StepLabel>
                        </Step>
                    ))}
                </Stepper>

                {step === steps.length ? (
                    <React.Fragment>
                        <Typography sx={{ mt: 2, mb: 1 }}>
                            All steps completed - you're finished
                        </Typography>
                    </React.Fragment>
                ) : (
                    <React.Fragment>

                        <Box
                            sx={{
                                display: 'flex',
                                flexDirection: 'row',
                                pt: 2
                            }}
                        >
                            <Button
                                color="inherit"
                                disabled={step === 0}
                                onClick={handleBack}
                                sx={{ mr: 1 }}
                            >
                                Back
                            </Button>
                            <Box sx={{ flex: '1 1 auto' }} />
                            <Button
                                onClick={handleNext}
                            >
                                {step === steps.length - 1 ? 'Finish' : 'Next'}
                            </Button>
                        </Box>

                        <div className='mt-10'>
                            {step === 1 && <DeliveryAddressForm />}
                            {step === 2 && <OrderSummary />}
                            {step === 3 && <PaymentSuccess />}
                        </div>

                    </React.Fragment>
                )}

            </Box>
        </div>
    );
};