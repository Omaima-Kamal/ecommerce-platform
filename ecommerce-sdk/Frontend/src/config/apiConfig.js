import axios from 'axios';

export const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'https://api.promo-engine.superpay.com:5454';

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const jwt = localStorage.getItem('jwt');

    if (jwt && jwt !== 'null' && jwt !== 'undefined') {
      config.headers.Authorization = `Bearer ${jwt}`;
    } else {
      delete config.headers.Authorization;
    }

    return config;
  },
  (error) => Promise.reject(error)
);