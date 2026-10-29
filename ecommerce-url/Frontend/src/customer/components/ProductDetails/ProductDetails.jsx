import { Button, Rating } from '@mui/material'
import React, { useEffect, useState } from 'react'
import { HomeSectionCard } from '../HomeSectionCard/HomeSectionCard'
import { useNavigate, useParams } from 'react-router-dom'
import { useDispatch, useSelector } from 'react-redux'
import { findProductById, findProducts } from '../../../State/Product/Action'
import { addItemToCart } from '../../../State/Cart/Action'

const highlights = [
  'Hand cut and sewn locally',
  'Dyed with our proprietary colors',
  'Pre-washed & pre-shrunk',
  'Ultra-soft 100% cotton',
]

export const ProductDetails = () => {
  const [selectedSize, setSelectedSize] = useState(null)
  const navigate = useNavigate();
  const param = useParams();
  const dispatch = useDispatch();
  const { product, products } = useSelector(store => store.product)

  const handleAddToCart = async () => {
    if (!selectedSize) {
      return;
    }

    const data = {
      productId: param.productId,
      size: selectedSize,
      quantity: 1
    };

    await dispatch(addItemToCart(data));
    navigate("/cart");
  };

  useEffect(() => {
    dispatch(findProductById(param.productId))
  }, [param.productId, dispatch])

  useEffect(() => {
    const category = product?.category;

    if (!category?.name || !param.productId) {
      return;
    }

    dispatch(findProducts({
      category: category.name,
      topLevelCategory: category.parentCategory?.parentCategory?.name,
      secondLevelCategory: category.parentCategory?.name,
      colors: "",
      sizes: "",
      minPrice: "",
      maxPrice: "",
      minDiscount: "",
      stock: "",
      sort: "",
      pageNumber: 0,
      pageSize: 10
    }));
  }, [
    product?.id,
    product?.category?.name,
    product?.category?.parentCategory?.name,
    product?.category?.parentCategory?.parentCategory?.name,
    param.productId,
    dispatch
  ]);

  return (
    <div className='bg-white py-10 lg:px-20'>
      <div className='mx-auto max-w-7xl px-4 pt-8 sm:px-6 lg:px-8'>
        <div className='grid grid-cols-1 lg:grid-cols-[1.2fr_1fr_0.6fr] gap-x-10 gap-y-10'>
          <div className='flex flex-col gap-4'>
            <div className='overflow-hidden rounded-lg bg-gray-100 aspect-[4/5]'>
              <img
                src={product?.imageUrl}
                alt={product?.title}
                className='h-full w-full object-cover object-center'
              />
            </div>
          </div>

          <div>
            <h1 className='text-2xl font-bold tracking-tight text-gray-900'>
              {product?.brand}
            </h1>

            <h1 className='text-lg lg:text-xl text-gray-900 opacity-60 pt-1'>
              {product?.title}
            </h1>

            <p className='mt-4 text-sm text-gray-600 leading-6'>
              {product?.description}
            </p>

            <div className='mt-8'>
              <h3 className='text-sm font-medium text-gray-900'>Highlights</h3>
              <ul className='mt-4 space-y-2'>
                {highlights.map((item) => (
                  <li key={item} className='flex items-start gap-2 text-sm text-gray-600'>
                    <span className='mt-1.5 h-1 w-1 shrink-0 rounded-full bg-gray-400' />
                    {item}
                  </li>
                ))}
              </ul>
            </div>
          </div>

          <div>
            <h2 className='sr-only'>Product Information</h2>
            <div className='flex space-x-5 items-center text-lg lg:text-xl text-gray-900 mt-6'>
              <p className='font-semibold'>${product?.discountedPrice}</p>
              <p className='opacity-50 line-through'>${product?.price}</p>
              <p className='font-semibold text-green-600'>{product?.discountPercent}% off</p>
            </div>

            <div className='mt-6 flex items-center gap-2'>
              <div className='flex items-center space-x-3'>
                <Rating name='read-only' value={product?.ratings || 0} precision={0.5} readOnly />
                <p className='opacity-50 text-sm'>{product?.numRatings || 0} Ratings</p>
                <p className='ml-3 text-sm font-medium text-indigo-600'>
                  {product?.reviews?.length || 0} Reviews
                </p>
              </div>
            </div>

            <div className='mt-8'>
              <h3 className='text-sm font-medium text-gray-900'>Color</h3>
              <div className='mt-3'>
                <span className='inline-block rounded-md border px-4 py-2 text-sm'>
                  {product?.color}
                </span>
              </div>
            </div>

            <div className='mt-8'>
              <div className='flex items-center justify-between'>
                <h3 className='text-sm font-medium text-gray-900'>Size</h3>
                <a href='#size-guide' className='text-sm font-medium text-indigo-600 hover:text-indigo-500'>
                  Size guide
                </a>
              </div>
              <div className='mt-3 grid grid-cols-4 gap-2'>
                {product?.sizes?.map((size) => (
                  <button
                    key={size.name || size}
                    onClick={() => setSelectedSize(size.name || size)}
                    className={`
                      rounded-md border py-2 text-sm font-medium text-center
                      ${selectedSize === (size.name || size)
                        ? 'bg-gray-900 text-white border-gray-900'
                        : 'border-gray-300 text-gray-900 hover:border-gray-400'}
                    `}
                  >
                    {size.name || size}
                  </button>
                ))}
              </div>
            </div>

            <Button
              disabled={!selectedSize}
              color='secondary'
              variant='contained'
              sx={{ px: '2rem', py: '1rem', bgcolor: '#9155fd', mt: 5 }}
              onClick={handleAddToCart}
            >
              Add to Cart
            </Button>
          </div>
        </div>
      </div>

      <section className='pt-10'>
        <h1 className='py-5 text-xl font-bold'>Similar Products</h1>
        <div className='flex flex-wrap gap-5'>
          {products?.content
            ?.filter(item => item.id !== product?.id)
            .map(item => (
              <HomeSectionCard key={item.id} product={item} />
            ))
          }
        </div>
      </section>
    </div>
  )
}