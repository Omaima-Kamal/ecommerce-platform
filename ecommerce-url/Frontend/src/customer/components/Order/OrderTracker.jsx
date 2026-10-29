import { Step, StepLabel, Stepper } from '@mui/material'
import React from 'react'

const steps = ["Placed", "Confirmed", "Shipped", "Delivered"]

export const OrderTracker = ({ activeStep }) => {
    return (
        <div className='w-full'>
            <Stepper
                activeStep={activeStep}
                alternativeLabel
                sx={{
                    '& .MuiStepIcon-root.Mui-active': { color: '#9155fd' },
                    '& .MuiStepIcon-root.Mui-completed': { color: '#9155fd' },
                }}
            >
                {steps.map((label) => (
                    <Step key={label}>
                        <StepLabel>{label}</StepLabel>
                    </Step>
                ))}
            </Stepper>
        </div>
    )
}
