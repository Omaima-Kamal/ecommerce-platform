import React, { useEffect } from 'react'
import { CartItem } from './CartItem'
import { Button } from '@mui/material'
import { useNavigate } from 'react-router-dom'
import { useDispatch, useSelector } from 'react-redux'
import { getCart } from '../../../State/Cart/Action'

export const Cart = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const { cart } = useSelector(store => store);

    const handleCheckout = () => {
        navigate("/checkout?step=1")
    }

    useEffect(() => {
        dispatch(getCart())
    }, []);

    const cartItems = cart.cart?.cartItems || [];

    return (
        <div>
            <div className='lg:grid grid-cols-3 lg:px-16 relative'>
                <div className='col-span-2'>
                    {cartItems.length > 0 ? (
                        cartItems.map((item) => (
                            <CartItem key={item.id} item={item} />
                        ))
                    ) : (
                        <p className='p-5 text-gray-500'>Your cart is empty.</p>
                    )}
                </div>
                <div className='px-5 sticky top-0 h-[100vh] mt-5 lg:mt-0'>
                    <div className='border'>
                        <p className='uppercase font-bold opacity-60 pb-4 mt-2 pl-1'>Price details</p>
                        <hr />

                        <div className='space-y-3 font-semibold'>
                            <div className='flex justify-between pt-3 text-black'>
                                <span className='pl-1'>Price</span>
                                <span>${cart.cart?.totalPrice}</span>
                            </div>

                            <div className='flex justify-between pt-3'>
                                <span className='pl-1'>Discount</span>
                                <span className='text-green-600'>-${cart.cart?.discount}</span>
                            </div>
                            <div className='flex justify-between pt-3'>
                                <span className='pl-1'>Delivery Charge</span>
                                <span className='text-green-600'>Free</span>
                            </div>
                            <div className='flex justify-between pt-3 font-bold pb-2'>
                                <span className='pl-1'>Total Amount</span>
                                <span className='text-green-600'>${cart.cart?.totalDiscountedPrice}</span>
                            </div>
                        </div>
                        <Button
                            variant='contained'
                            className='w-full mt-5'
                            sx={{ px: '2.5rem', py: '.7rem', bgcolor: '#9155fd', mt: 5 }}
                            onClick={handleCheckout}
                        >
                            Checkout
                        </Button>
                    </div>
                </div>
            </div>
        </div>
    )
}