import React, { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useParams, Link } from 'react-router-dom'
import { Alert, AlertTitle, CircularProgress } from '@mui/material'

import { getOrderById } from '../../State/Order/Action'
import { OrderTracker } from '../components/Order/OrderTracker'
import { AddressCard } from '../components/AddressCard/AddressCard'

const PAID_STATUSES = ['COMPLETED', 'PAID', 'SUCCESS']
const FAILED_STATUSES = ['FAILED', 'CANCELLED']
const MAX_ATTEMPTS = 10
const POLL_MS = 2000

export const PaymentSuccess = () => {
    const { orderId } = useParams()
    const dispatch = useDispatch()
    const { order } = useSelector((store) => store)
    const [timedOut, setTimedOut] = useState(false)

    const o = String(order.order?.id) === String(orderId) ? order.order : null

    const paymentStatus = o?.paymentDetails?.status?.toUpperCase()
    const isPaid = PAID_STATUSES.includes(paymentStatus)
    const isFailed = FAILED_STATUSES.includes(paymentStatus)

    useEffect(() => {
        if (!orderId || isPaid || isFailed) return

        let attempts = 0
        dispatch(getOrderById(orderId))

        const timer = setInterval(() => {
            attempts += 1
            if (attempts >= MAX_ATTEMPTS) {
                setTimedOut(true)
                clearInterval(timer)
                return
            }
            dispatch(getOrderById(orderId))
        }, POLL_MS)

        return () => clearInterval(timer)
    }, [orderId, isPaid, isFailed, dispatch])

    return (
        <div className='px-2 lg:px-36'>
            <div className='mt-5 mb-5 flex flex-col justify-center items-center'>
                {isPaid && (
                    <Alert variant='filled' severity='success' sx={{ mb: 6, width: 'fit-content' }}>
                        <AlertTitle>Payment Success</AlertTitle>
                        Congratulations! Your order has been placed.
                    </Alert>
                )}

                {isFailed && (
                    <Alert variant='filled' severity='error' sx={{ mb: 6, width: 'fit-content' }}>
                        <AlertTitle>Payment Failed</AlertTitle>
                        Your payment was not completed. You can try again from your orders.
                    </Alert>
                )}

                {!isPaid && !isFailed && (
                    <Alert
                        variant='outlined'
                        severity='info'
                        icon={timedOut ? undefined : <CircularProgress size={20} />}
                        sx={{ mb: 6, width: 'fit-content' }}
                    >
                        <AlertTitle>{timedOut ? 'Still confirming your payment' : 'Confirming your payment...'}</AlertTitle>
                        {timedOut
                            ? <>This is taking longer than usual. Check <Link to='/account/order' className='underline'>My Orders</Link> in a moment.</>
                            : 'Please wait, this only takes a few seconds.'}
                    </Alert>
                )}
            </div>

            <OrderTracker activeStep={isPaid ? 1 : 0} />

            {o && (
                <div className='py-5 pt-16 space-y-5'>
                    <div className='shadow-xl rounded-md p-5'>
                        <p className='font-semibold mb-3'>Delivery Address</p>
                        <AddressCard address={o.shippingAddress} />
                    </div>

                    {o.orderItems?.map((item) => (
                        <div key={item.id} className='shadow-xl rounded-md p-5 flex items-center'>
                            <img
                                className='w-[5rem] h-[5rem] object-cover object-top'
                                src={item.product?.imageUrl}
                                alt={item.product?.title}
                            />
                            <div className='ml-5 space-y-2'>
                                <p>{item.product?.title}</p>
                                <div className='opacity-50 text-xs font-semibold space-x-5'>
                                    <span>Color: {item.product?.color}</span>
                                    <span>Size: {item.size}</span>
                                </div>
                                <p>Seller: {item.product?.brand}</p>
                                <p>$ {item.discountedPrice}</p>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    )
}