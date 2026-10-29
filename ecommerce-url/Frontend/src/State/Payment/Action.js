import { api } from "../../config/apiConfig";
import { CREATE_PAYMENT_LINK_FAILURE, CREATE_PAYMENT_LINK_REQUEST, CREATE_PAYMENT_LINK_SUCCESS, UPDATE_PAYMENT_FAILURE, UPDATE_PAYMENT_REQUEST, UPDATE_PAYMENT_SUCCESS } from "./ActionType";


export const createPaymentLink = (orderId) => async (dispatch) => {
    dispatch({ type: CREATE_PAYMENT_LINK_REQUEST })

    try {
        const { data } = await api.post(`/api/payments/${orderId}`)
        dispatch({ type: CREATE_PAYMENT_LINK_SUCCESS, payload: data })

        if (data?.paymentUrl) {
            // same-tab navigation to the gateway's checkout page
            window.location.assign(data.paymentUrl)
        }
        return data
    } catch (error) {
        const message = error.response?.data?.message || error.message
        dispatch({ type: CREATE_PAYMENT_LINK_FAILURE, payload: message })
    }
}

export const updatePayment = (reqData) => async (dispatch) => {
    dispatch({ type: UPDATE_PAYMENT_REQUEST })

    try {
        const { data } = await api.get(
            `/api/payments?payment_id=${reqData.paymentId}&order_id=${reqData.orderId}`)
        dispatch({ type: UPDATE_PAYMENT_SUCCESS, payload: data })

        if (data?.paymentUrl) {
            // same-tab navigation to the gateway's checkout page
            window.location.assign(data.paymentUrl)
        }
        return data
    } catch (error) {
        const message = error.response?.data?.message || error.message
        dispatch({ type: UPDATE_PAYMENT_FAILURE, payload: message })
    }
}