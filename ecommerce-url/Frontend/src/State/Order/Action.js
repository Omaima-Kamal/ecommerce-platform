import { api } from "../../config/apiConfig"
import { CREATE_ORDER_FAILURE, CREATE_ORDER_REQUEST, CREATE_ORDER_SUCCESS, GET_ORDER_BY_ID_FAILURE, GET_ORDER_BY_ID_REQUEST, GET_ORDER_BY_ID_SUCCESS, GET_USER_ORDERS_FAILURE, GET_USER_ORDERS_REQUEST, GET_USER_ORDERS_SUCCESS } from "./ActionType";


export const createOrder = (address, navigate) => async (dispatch) => {
    dispatch({ type: CREATE_ORDER_REQUEST });

    try {
        const { data } = await api.post(
            `/api/orders/`,
            address
        );

        dispatch({
            type: CREATE_ORDER_SUCCESS,
            payload: data
        });

        if (data.id) {
            navigate(`/checkout?step=2&order_id=${data.id}`);
        }

    } catch (error) {
        dispatch({
            type: CREATE_ORDER_FAILURE,
            payload: error.message
        });
    }
};

export const getOrderById = (orderId) => async (dispatch) => {
        dispatch({ type: GET_ORDER_BY_ID_REQUEST});

    try {
        const { data } = await api.get(`/api/orders/${orderId}`);
        dispatch({type: GET_ORDER_BY_ID_SUCCESS, payload:data})

    } catch (error) {
        dispatch({ type: GET_ORDER_BY_ID_FAILURE, payload:error.message});

    }
    
}


export const getUserOrders = () => async (dispatch) => {
    dispatch({ type: GET_USER_ORDERS_REQUEST });

    try {
        const { data } = await api.get(`/api/orders/user`);
        dispatch({ type: GET_USER_ORDERS_SUCCESS, payload: data });
    } catch (error) {
        dispatch({ type: GET_USER_ORDERS_FAILURE, payload: error.message });
    }
};