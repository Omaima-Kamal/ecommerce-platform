import { Grid } from '@mui/material';
import React from 'react';
import AdjustIcon from '@mui/icons-material/Adjust';
import { useNavigate } from 'react-router-dom';

const statusConfig = {
    PENDING: { label: 'Payment pending', color: 'text-yellow-600' },
    PLACED: { label: 'Order placed', color: 'text-green-600' },
    CONFIRMED: { label: 'Order confirmed', color: 'text-blue-600' },
    SHIPPED: { label: 'Shipped', color: 'text-blue-600' },
    DELIVERED: { label: 'Delivered', color: 'text-green-600' },
    CANCELLED: { label: 'Cancelled', color: 'text-red-600' },
}

export const OrderCard = ({ order }) => {
    const navigate = useNavigate();

    if (!order) return null;

    const firstItem = order.orderItems?.[0];
    const extraCount = (order.orderItems?.length || 0) - 1;
    const status = statusConfig[order.orderStatus] || { label: order.orderStatus, color: 'text-gray-600' };

    const formattedDate = order.orderDate
        ? new Date(order.orderDate).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
        : null;

    return (
        <div
            onClick={() => navigate(`/account/order/${order.id}`)}
            className='p-5 shadow-md shadow-gray-700 hover:shadow-2xl border cursor-pointer'
        >
            <Grid container spacing={2} sx={{ justifyContent: "space-between" }}>
                <Grid size={{ xs: 12, sm: 6 }}>
                    <div className="flex">
                        <img
                            className="w-[5rem] h-[5rem] object-cover object-top"
                            src={firstItem?.product?.imageUrl}
                            alt={firstItem?.product?.title}
                        />

                        <div className="ml-5 space-y-2">
                            <p className="mb-2">
                                {firstItem?.product?.title}
                                {extraCount > 0 && (
                                    <span className='text-xs opacity-60'> + {extraCount} more item{extraCount > 1 ? 's' : ''}</span>
                                )}
                            </p>

                            <p className="opacity-50 text-xs font-semibold">
                                Size: {firstItem?.size}
                            </p>

                            <p className="opacity-50 text-xs font-semibold">
                                Color: {firstItem?.product?.color}
                            </p>
                        </div>
                    </div>
                </Grid>

                <Grid size={{ xs: 12, sm: 2 }}>
                    <p>${order.totalDiscountedPrice}</p>
                </Grid>

                <Grid size={{ xs: 12, sm: 4 }}>
                    <div>
                        <p>
                            <AdjustIcon
                                sx={{ width: "15px", height: "15px" }}
                                className={`${status.color} mr-2 text-sm`}
                            />
                            <span>
                                {status.label}{formattedDate ? ` on ${formattedDate}` : ''}
                            </span>
                        </p>
                    </div>
                </Grid>
            </Grid>
        </div>
    );
};