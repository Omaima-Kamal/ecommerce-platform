import React, { useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useParams } from 'react-router-dom'
import { AddressCard } from '../AddressCard/AddressCard'
import { OrderTracker } from './OrderTracker'
import { Box, Grid, CircularProgress } from '@mui/material'
import { deepPurple } from '@mui/material/colors'
import StarBorderIcon from '@mui/icons-material/Star'
import { getOrderById } from '../../../State/Order/Action'

const statusToStep = {
    PENDING: -1,
    PLACED: 0,
    CONFIRMED: 1,
    SHIPPED: 2,
    DELIVERED: 3,
}
export const OrderDetails = () => {
    const { orderId } = useParams()
    const dispatch = useDispatch()
    const { order } = useSelector((store) => store)

    useEffect(() => {
        if (orderId) {
            dispatch(getOrderById(orderId))
        }
    }, [orderId, dispatch])

    if (order.loading || !order.order) {
        return (
            <div className='flex justify-center items-center h-[50vh]'>
                <CircularProgress />
            </div>
        )
    }

    const o = order.order
    const activeStep = statusToStep[o.orderStatus] ?? 0

    return (
        <div className='mt-10 mb-10 px-5 lg:px-20'>
            <div>
                <h1 className='font-bold text-xl py-7'>Delivery Address</h1>
                <AddressCard address={o.shippingAddress} />
            </div>

            <div className='py-20'>
                <OrderTracker activeStep={activeStep} />
            </div>

            <Grid container spacing={2}>
                {o.orderItems?.map((item) => (
                    <Grid
                        key={item.id}
                        size={12}
                        className="shadow-xl rounded-md p-5 border"
                        sx={{
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "space-between"
                        }}
                    >
                        <Grid size={6}>
                            <div className="flex items-center space-x-4">
                                <img
                                    className="w-[5rem] h-[5rem] object-cover object-top"
                                    src={item.product?.imageUrl}
                                    alt={item.product?.title}
                                />

                                <div className="space-y-2 ml-5">
                                    <p className='font-semibold'>{item.product?.title}</p>

                                    <p className="space-x-5 opacity-50 text-xs font-semibold">
                                        <span>Color: {item.product?.color}</span>
                                        <span>Size: {item.size}</span>
                                    </p>

                                    <p>Seller: {item.product?.brand}</p>
                                    <p>${item.discountedPrice}</p>
                                </div>
                            </div>
                        </Grid>

                        <Grid>
                            <Box
                                sx={{
                                    color: deepPurple[500],
                                    display: "flex",
                                    alignItems: "center",
                                    cursor: "pointer"
                                }}
                            >
                                <StarBorderIcon sx={{ fontSize: "2rem" }} />
                                <span className="ml-2">Rate & Review Product</span>
                            </Box>
                        </Grid>
                    </Grid>
                ))}
            </Grid>
        </div>
    )
}