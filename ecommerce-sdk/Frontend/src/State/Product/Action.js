import { api } from "../../config/apiConfig"
import { FIND_PRODUCT_BY_ID_FAILURE, FIND_PRODUCT_BY_ID_REQUEST, FIND_PRODUCT_BY_ID_SUCCESS, FIND_PRODUCTS_FAILURE, FIND_PRODUCTS_REQUEST, FIND_PRODUCTS_SUCCESS } from "./ActionType";

export const findProducts = (reqData) => async (dispatch) => {

    dispatch({ type: FIND_PRODUCTS_REQUEST })

    const {
        topLevelCategory,
        secondLevelCategory,
        colors,
        sizes,
        minPrice,
        maxPrice,
        minDiscount,
        category,
        stock,
        sort,
        pageNumber,
        pageSize
    } = reqData;

    const params = new URLSearchParams();
    if (topLevelCategory) params.append("topLevelCategory", topLevelCategory);
    if (secondLevelCategory) params.append("secondLevelCategory", secondLevelCategory);
    if (category) params.append("category", category);
    if (colors) params.append("colors", colors);
    if (sizes) params.append("size", sizes);
    if (minPrice !== "" && minPrice != null) params.append("minPrice", minPrice);
    if (maxPrice !== "" && maxPrice != null) params.append("maxPrice", maxPrice);
    if (minDiscount !== "" && minDiscount != null) params.append("minDiscount", minDiscount);
    if (stock) params.append("stock", stock);
    if (sort) params.append("sort", sort);
    params.append("pageNumber", pageNumber);
    params.append("pageSize", pageSize);

    try {
        const { data } = await api.get(`/api/products?${params.toString()}`);
        dispatch({ type: FIND_PRODUCTS_SUCCESS, payload: data })

    } catch (error) {
        dispatch({ type: FIND_PRODUCTS_FAILURE, payload: error.message })
    }
};

export const findProductById = (productId) => async (dispatch) => {

    dispatch({ type: FIND_PRODUCT_BY_ID_REQUEST })

    try {
        const { data } = await api.get(`/api/products/id/${productId}`)

        dispatch({ type: FIND_PRODUCT_BY_ID_SUCCESS, payload: data })

    } catch (error) {
        dispatch({ type: FIND_PRODUCT_BY_ID_FAILURE, payload: error.message })
    }
};