import React, { useState, useMemo, useEffect } from 'react'
import { ProductCard } from './ProductCard'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'
import { useDispatch, useSelector } from 'react-redux'
import { findProducts } from '../../../State/Product/Action'
import Pagination from '@mui/material/Pagination';

const sortOptions = [
    { name: 'Most Popular', href: '#', current: true },
    { name: 'Best Rating', href: '#', current: false },
    { name: 'Newest', href: '#', current: false },
    { name: 'Price Low to High', href: '#', current: false },
    { name: 'Price High to Low', href: '#', current: false },
]

const subCategories = [
    { name: 'Totes', href: '#' },
    { name: 'Backpacks', href: '#' },
    { name: 'Travel Bags', href: '#' },
    { name: 'Hip Bags', href: '#' },
    { name: 'Laptop Sleeves', href: '#' },
]

const filters = [
    {
        id: 'color',
        name: 'Color',
        options: [
            { value: 'White', label: 'White', checked: false },
            { value: 'Beige', label: 'Beige', checked: false },
            { value: 'Blue', label: 'Blue', checked: true },
            { value: 'Brown', label: 'Brown', checked: false },
            { value: 'Green', label: 'Green', checked: false },
            { value: 'Purple', label: 'Purple', checked: false },
            { value: 'Yellow', label: 'Yellow', checked: false },
            { value: 'Light Green', label: 'Light Green', checked: false },
            { value: 'Pink', label: 'Pink', checked: false },
        ],
    },
    {
        id: 'category',
        name: 'Category',
        options: [
            { value: 'new-arrivals', label: 'New Arrivals', checked: false },
            { value: 'sale', label: 'Sale', checked: false },
            { value: 'travel', label: 'Travel', checked: true },
            { value: 'organization', label: 'Organization', checked: false },
            { value: 'accessories', label: 'Accessories', checked: false },
        ],
    },
    {
        id: 'size',
        name: 'Size',
        options: [
            { value: 's', label: 'S', checked: false },
            { value: 'm', label: 'M', checked: false },
            { value: 'l', label: 'L', checked: false },
            { value: 'xl', label: 'XL', checked: true },
        ],
    },
]

const PlusIcon = ({ className }) => (
    <svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 20 20' fill='currentColor' className={className}>
        <path d='M10 3a.75.75 0 01.75.75v5.5h5.5a.75.75 0 010 1.5h-5.5v5.5a.75.75 0 01-1.5 0v-5.5h-5.5a.75.75 0 010-1.5h5.5v-5.5A.75.75 0 0110 3z' />
    </svg>
)

const MinusIcon = ({ className }) => (
    <svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 20 20' fill='currentColor' className={className}>
        <path d='M4.25 9.25a.75.75 0 000 1.5h11.5a.75.75 0 000-1.5H4.25z' />
    </svg>
)

function classNames(...classes) {
  return classes.filter(Boolean).join(' ')
}

export const Product = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const param = useParams();
    const dispatch = useDispatch();
    const { product } = useSelector(store => store);

    const [openFilters, setOpenFilters] = useState({ color: true, category: true, size: true })

    const decodedQueryString = decodeURIComponent(location.search);
    const searchParams = new URLSearchParams(decodedQueryString);
    const colorValue = searchParams.get("color");
    const sizeValue = searchParams.get("size");
    const priceValue = searchParams.get("price");
    const discount = searchParams.get("discount");
    const sortValue = searchParams.get("sort");
    const pageNumber = searchParams.get("page") || 1;
    const stock = searchParams.get("stock");


    const toggleFilter = (id) => {
        setOpenFilters((prev) => ({ ...prev, [id]: !prev[id] }))
    }

    const handleFilterChange = (filterId, value, isChecked) => {
        const params = new URLSearchParams(location.search)
        if (isChecked) {
            params.append(filterId, value)
        } else {
            const existing = params.getAll(filterId).filter((v) => v !== value)
            params.delete(filterId)
            existing.forEach((v) => params.append(filterId, v))
        }
        navigate({ search: params.toString() })
    }

    const isOptionChecked = (filterId, value) => {
        return searchParams.getAll(filterId).includes(value)
    }

    const filteredProducts = useMemo(() => {
        const colorValues = searchParams.getAll('color')
        const categoryValues = searchParams.getAll('category')
        const sizeValues = searchParams.getAll('size')

        if (!product.products?.content) return [];

        return product.products.content.filter((p) => {
            const matchesColor = colorValues.length === 0 || colorValues.includes(p.color)
            const matchesCategory = categoryValues.length === 0 || categoryValues.includes(p.category)
            const matchesSize = sizeValues.length === 0 || sizeValues.includes(p.size)
            return matchesColor && matchesCategory && matchesSize
        })
    }, [location.search, product.products])


    useEffect(() => {
        const [minPrice, maxPrice] = priceValue === null ? [0, 10000] : priceValue.split("-").map(Number);

        const data = {
            topLevelCategory: param.levelOne,
            secondLevelCategory: param.levelTwo,
            category: param.levelThree,
            colors: colorValue || [],
            sizes: sizeValue || [],
            minPrice,
            maxPrice,
            minDiscount: discount || 0,
            sort: sortValue || "price_low",
            pageNumber: pageNumber - 1,
            pageSize: 12,
            stock: stock

        }
        dispatch(findProducts(data))
    }, [
        param.levelThree,
        colorValue,
        sizeValue,
        priceValue,
        discount,
        sortValue,
        pageNumber,
        stock
    ])

    return (
        <div className="min-h-screen bg-gray-50">

            <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">

                {/* Header */}
                <div className="flex items-center justify-between border-b border-gray-200 py-6">
                    <div>
                        <h1 className="text-2xl font-bold tracking-tight text-gray-900 sm:text-3xl">
                            Products
                        </h1>

                        <p className="mt-1 text-sm text-gray-500">
                            Discover our latest products
                        </p>
                    </div>

                    {/* Sort */}
                    <div className="flex items-center gap-3">
                        <span className="hidden text-sm text-gray-500 sm:block">
                            Sort by
                        </span>

                        <select
                            className="
                            rounded-lg
                            border border-gray-300
                            bg-white
                            px-4 py-2
                            text-sm font-medium text-gray-700
                            shadow-sm
                            outline-none
                            transition
                            hover:border-gray-400
                            focus:border-black
                            focus:ring-1
                            focus:ring-black
                        "
                            defaultValue={sortOptions.find((o) => o.current)?.name}
                        >
                            {sortOptions.map((option) => (
                                <option key={option.name}>
                                    {option.name}
                                </option>
                            ))}
                        </select>
                    </div>
                </div>


                {/* Main content */}
                <div className="grid grid-cols-1 gap-8 py-8 lg:grid-cols-4">

                    {/* Sidebar */}
                    <aside className="lg:col-span-1">

                        <div className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">

                            {/* Categories */}
                            <div>
                                <h2 className="text-sm font-semibold uppercase tracking-wider text-gray-900">
                                    Categories
                                </h2>

                                <ul className="mt-4 space-y-1">
                                    {subCategories.map((category) => (
                                        <li key={category.name}>
                                            <Link
                                                to={category.href}
                                                className="
                                                block rounded-lg
                                                px-3 py-2
                                                text-sm text-gray-600
                                                transition
                                                hover:bg-gray-100
                                                hover:text-gray-900
                                            "
                                            >
                                                {category.name}
                                            </Link>
                                        </li>
                                    ))}
                                </ul>
                            </div>


                            {/* Filters */}
                            <div className="mt-6">

                                {filters.map((filter) => {
                                    const isOpen = openFilters[filter.id]

                                    return (
                                        <div
                                            key={filter.id}
                                            className="border-t border-gray-200"
                                        >

                                            <button
                                                onClick={() => toggleFilter(filter.id)}
                                                className="
                                                flex w-full items-center
                                                justify-between
                                                py-4
                                                text-left
                                                text-sm font-semibold
                                                text-gray-900
                                                transition
                                                hover:text-gray-600
                                            "
                                            >
                                                <span>
                                                    {filter.name}
                                                </span>

                                                {isOpen ? (
                                                    <MinusIcon className="h-4 w-4 text-gray-500" />
                                                ) : (
                                                    <PlusIcon className="h-4 w-4 text-gray-500" />
                                                )}
                                            </button>


                                            {isOpen && (
                                                <div className="pb-5">
                                                    <div className="space-y-3">

                                                        {filter.options.map((option, idx) => (
                                                            <div
                                                                key={option.value}
                                                                className="flex items-center"
                                                            >
                                                                <input
                                                                    id={`filter-${filter.id}-${idx}`}
                                                                    type="checkbox"
                                                                    checked={isOptionChecked(filter.id, option.value)}
                                                                    onChange={(e) =>
                                                                        handleFilterChange(
                                                                            filter.id,
                                                                            option.value,
                                                                            e.target.checked
                                                                        )
                                                                    }
                                                                    className="
                h-4 w-4
                rounded
                border-gray-300
                text-black
                accent-black
                focus:ring-black
            "
                                                                />

                                                                <label
                                                                    htmlFor={`filter-${filter.id}-${idx}`}
                                                                    className="
                ml-3
                cursor-pointer
                text-sm
                text-gray-600
                transition
                hover:text-gray-900
            "
                                                                >
                                                                    {option.label}
                                                                </label>
                                                            </div>
                                                        ))}
                                                    </div>
                                                </div>
                                            )}
                                        </div>
                                    )
                                })}
                            </div>
                        </div>
                    </aside>

                    {/* Products */}
                    <div className="lg:col-span-3">
                        <div className="mb-5 flex items-center justify-between">
                            <p className="text-sm text-gray-500">
                                Showing <span className="font-medium text-gray-900">{filteredProducts.length}</span> products
                            </p>

                            <button className="rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 transition hover:bg-gray-100">
                                Filters
                            </button>
                        </div>

                        <div className="grid grid-cols-2 gap-4 sm:grid-cols-2 md:grid-cols-3">
                            {filteredProducts.map((item) => (
                                <ProductCard key={item.id} product={item} />
                            ))}
                        </div>

                        {product.products?.totalPages > 1 && (
                            <div className="w-full px-[3.6rem]">
                                <div className="px-4 py-5 flex justify-center">
                                    <Pagination
                                        count={product.products.totalPages}
                                        page={Number(pageNumber)}
                                        color="secondary"
                                        onChange={(event, value) => {
                                            const params = new URLSearchParams(location.search)
                                            params.set('page', value)
                                            navigate({ search: params.toString() })
                                        }}
                                    />
                                </div>
                            </div>
                        )}
                    </div>

                </div>
            </div>
        </div>
    )
}