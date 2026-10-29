import React, { useEffect, useState, useRef } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { AddressCard } from '../AddressCard/AddressCard';
import { Button } from '@mui/material';
import { useLocation } from 'react-router-dom';
import { getOrderById } from '../../../State/Order/Action';
import { createPaymentLink } from '../../../State/Payment/Action';

const OrderItemRow = ({ item }) => (
    <div className='p-5 shadow-lg border rounded-md flex items-center'>
        <div className='w-[5rem] h-[5rem] lg:w-[9rem] lg:h-[9rem]'>
            <img className='w-full h-full object-cover object-top' src={item.product?.imageUrl} alt='' />
        </div>
        <div className='ml-5 space-y-1'>
            <p className='font-semibold'>{item.product?.title}</p>
            <p className='opacity-70'>Size: {item.size}, {item.product?.color}</p>
            <p className='opacity-70 mt-2'>Seller: {item.product?.brand}</p>
            <div className='flex space-x-5 items-center text-gray-900 pt-4'>
                <p className='font-semibold'>${item.discountedPrice}</p>
                <p className='opacity-50 line-through'>${item.price}</p>
                <p className='opacity-70 text-sm'>Qty: {item.quantity}</p>
            </div>
        </div>
    </div>
);

export const OrderSummary = () => {
    const dispatch = useDispatch();
    const location = useLocation();
    const paymentContainerRef = useRef(null);
    const [paymentStarted, setPaymentStarted] = useState(false);

    const searchParams = new URLSearchParams(location.search);
    const orderId = searchParams.get('order_id');
    const { order, payment, cart } = useSelector((store) => store);

    const o = order.order;

    useEffect(() => {
        if (orderId) {
            dispatch(getOrderById(orderId));
        }
    }, [dispatch, orderId]);

    const orderItems = o?.orderItems || cart?.cartItems || [];

    const totalPrice = orderItems.reduce(
        (sum, item) => sum + (item.price * item.quantity),
        0
    );

    const totalDiscountedPrice = orderItems.reduce(
        (sum, item) => sum + (item.discountedPrice * item.quantity),
        0
    );

    const discount = totalPrice - totalDiscountedPrice;

    const shippingAddress = o?.shippingAddress;

    const handleCheckout = async () => {
        if (!orderId || !paymentContainerRef.current) return;
        try {
            setPaymentStarted(true);
            await dispatch(createPaymentLink(orderId, paymentContainerRef.current));
        } catch (error) {
            setPaymentStarted(false);
        }
    };

    return (
        <div>
            <div className="p-5 shadow-lg rounded-s-md border">
                <AddressCard address={shippingAddress} />
            </div>

            <div>
                <div className="lg:grid grid-cols-3 relative">
                    <div className="col-span-2 space-y-4">
                        {orderItems.length > 0 ? (
                            orderItems.map((item) => <OrderItemRow key={item.id} item={item} />)
                        ) : (
                            <p className="p-5 text-gray-500">No items in this order.</p>
                        )}
                    </div>

                    <div ref={paymentContainerRef} className="px-5 sticky top-0 mt-5 lg:mt-0">
                        {!paymentStarted && (
                            <div className="border">
                                <p className="uppercase font-bold opacity-60 pb-4 mt-2 pl-1">Price details</p>
                                <hr />

                                <div className="space-y-3 font-semibold">
                                    <div className="flex justify-between pt-3 text-black">
                                        <span className="pl-1">Price</span>
                                        <span>${totalPrice}</span>
                                    </div>
                                    <div className="flex justify-between pt-3">
                                        <span className="pl-1">Discount</span>
                                        <span className="text-green-600">-${discount}</span>
                                    </div>
                                    <div className="flex justify-between pt-3">
                                        <span className="pl-1">Delivery Charge</span>
                                        <span className="text-green-600">Free</span>
                                    </div>
                                    <div className="flex justify-between pt-3 font-bold pb-2">
                                        <span className="pl-1">Total Amount</span>
                                        <span className="text-green-600">
                                            ${totalDiscountedPrice}
                                        </span>
                                    </div>
                                </div>

                                {payment?.error && (
                                    <p className="text-red-600 text-sm mt-3 px-1">{payment.error}</p>
                                )}

                                <Button
                                    variant="contained"
                                    className="w-full mt-5"
                                    sx={{ px: '2.5rem', py: '.7rem', bgcolor: '#9155fd', mt: 5 }}
                                    onClick={handleCheckout}
                                    disabled={payment?.loading || !orderId}
                                >
                                    {payment?.loading ? 'Loading Payment...' : 'Checkout'}
                                </Button>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};