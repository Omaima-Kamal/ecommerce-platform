import { Grid, CircularProgress } from '@mui/material';
import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { OrderCard } from './OrderCard';
import { getUserOrders } from '../../../State/Order/Action';

const orderStatus = [
    { label: "Placed", value: "PLACED" },
    { label: "Confirmed", value: "CONFIRMED" },
    { label: "Shipped", value: "SHIPPED" },
    { label: "Delivered", value: "DELIVERED" },
    { label: "Cancelled", value: "CANCELLED" },
]

export const Order = () => {
    const dispatch = useDispatch();
    const { order } = useSelector((store) => store);
    const [selectedStatuses, setSelectedStatuses] = useState([]);

    useEffect(() => {
        dispatch(getUserOrders());
    }, [dispatch]);

    const toggleStatus = (value) => {
        setSelectedStatuses((prev) =>
            prev.includes(value)
                ? prev.filter((v) => v !== value)
                : [...prev, value]
        );
    };

    const orders = order.orders || [];

    const filteredOrders = selectedStatuses.length === 0
        ? orders
        : orders.filter((o) => selectedStatuses.includes(o.orderStatus));

    return (
        <div className='mt-7 mb-10 px-5 lg:px-20'>
            <Grid container sx={{ justifyContent: "space-between" }}>
                {/* Filter */}
                <Grid size={{ xs: 12, md: 2.5 }}>
                    <div className="h-auto shadow-lg bg-white p-5 sticky top-5">
                        <h1 className="font-bold text-lg">Filter</h1>

                        <div className="space-y-4 mt-10">
                            <h1 className="font-semibold">ORDER STATUS</h1>

                            {orderStatus.map((option) => (
                                <div key={option.value} className="flex items-center">
                                    <input
                                        id={option.value}
                                        checked={selectedStatuses.includes(option.value)}
                                        onChange={() => toggleStatus(option.value)}
                                        type="checkbox"
                                        className="h-4 w-4 border-gray-300 text-indigo-600 focus:ring-indigo-500"
                                    />
                                    <label
                                        className="ml-3 text-sm text-gray-600"
                                        htmlFor={option.value}
                                    >
                                        {option.label}
                                    </label>
                                </div>
                            ))}
                        </div>
                    </div>
                </Grid>

                {/* Orders */}
                <Grid size={{ xs: 12, md: 9 }}>
                    {order.loading ? (
                        <div className='flex justify-center py-20'>
                            <CircularProgress />
                        </div>
                    ) : filteredOrders.length > 0 ? (
                        <div className='space-y-5'>
                            {filteredOrders.map((item) => (
                                <OrderCard key={item.id} order={item} />
                            ))}
                        </div>
                    ) : (
                        <p className='text-center text-gray-500 py-20'>No orders found.</p>
                    )}
                </Grid>
            </Grid>
        </div>
    );
};