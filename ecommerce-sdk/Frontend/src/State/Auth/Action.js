import { api } from "../../config/apiConfig"
import { GET_USER_FAILURE, GET_USER_REQUEST, GET_USER_SUCCESS, LOGIN_FAILURE, LOGIN_REQUEST, LOGIN_SUCCESS, LOGOUT, REGISTER_FAILURE, REGISTER_REQUEST, REGISTER_SUCCESS } from "./ActionType"

const registerRequest = () => ({ type: REGISTER_REQUEST });
const registerSuccess = (payload) => ({ type: REGISTER_SUCCESS, payload });
const registerFailure = (error) => ({ type: REGISTER_FAILURE, payload: error });

export const register = (userData) => async (dispatch) => {
    dispatch(registerRequest())
    try {
        const response = await api.post(`/auth/signup`, userData)
        const data = response.data;
        if (data.jwt) {
            localStorage.setItem("jwt", data.jwt)
        }
        dispatch(registerSuccess(data))
    } catch (error) {
        dispatch(registerFailure(error.response?.data?.message || error.message))
    }
}

const loginRequest = () => ({ type: LOGIN_REQUEST });
const loginSuccess = (payload) => ({ type: LOGIN_SUCCESS, payload });
const loginFailure = (error) => ({ type: LOGIN_FAILURE, payload: error });

export const login = (userData) => async (dispatch) => {
    dispatch(loginRequest())
    try {
        const response = await api.post(`/auth/signin`, userData)
        const data = response.data;
        if (data.jwt) {
            localStorage.setItem("jwt", data.jwt)
        }
        dispatch(loginSuccess(data))
    } catch (error) {
        dispatch(loginFailure(error.response?.data?.message || error.message))
    }
}

const getUserRequest = () => ({ type: GET_USER_REQUEST });
const getUserSuccess = (user) => ({ type: GET_USER_SUCCESS, payload: user });
const getUserFailure = (error) => ({ type: GET_USER_FAILURE, payload: error });

export const getUser = () => async (dispatch) => {
    dispatch(getUserRequest())
    try {
        const response = await api.get(`/api/users/profile`)
        dispatch(getUserSuccess(response.data))
    } catch (error) {
        dispatch(getUserFailure(error.message))

        if (error.response?.status === 401) {
            localStorage.removeItem("jwt");
        }
    }
}

export const logout = () => (dispatch) => {
    localStorage.removeItem("jwt");
    dispatch({ type: LOGOUT, payload: null });
};