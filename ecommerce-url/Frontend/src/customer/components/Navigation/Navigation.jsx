import React, { useEffect, useRef, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';

import SearchIcon from '@mui/icons-material/Search';
import ShoppingBagOutlinedIcon from '@mui/icons-material/ShoppingBagOutlined';
import PersonOutline from '@mui/icons-material/Person2Outlined';
import Inventory2OutlinedIcon from '@mui/icons-material/Inventory2Outlined';
import LogoutIcon from '@mui/icons-material/Logout';
import { AuthModel } from '../../Auth/AuthModel';
import { Button } from '@mui/material';
import { useDispatch, useSelector } from 'react-redux';
import { getUser, logout } from '../../../State/Auth/Action';
import { getCart } from '../../../State/Cart/Action';

const womenMenu = {
  clothing: [
    { id: 'tops', name: 'Tops' },
    { id: 'dresses', name: 'Dresses' },
    { id: 'women-jeans', name: 'Women Jeans' },
    { id: 'sweaters', name: 'Sweaters' },
    { id: 't-shirts', name: 'T-Shirts' },
    { id: 'jackets', name: 'Jackets' },
    { id: 'gowns', name: 'Gowns' },
    { id: 'blouses', name: 'Blouses' },
  ],
  accessories: [
    { id: 'watches', name: 'Watches' },
    { id: 'wallets', name: 'Wallets' },
    { id: 'bags', name: 'Bags' },
    { id: 'sunglasses', name: 'Sunglasses' },
    { id: 'hats', name: 'Hats' },
    { id: 'belts', name: 'Belts' },
  ],
  brands: [
    { id: 'full-nelson', name: 'Full Nelson' },
    { id: 'my-way', name: 'My Way' },
    { id: 're-arranged', name: 'Re-Arranged' },
    { id: 'counterfeit', name: 'Counterfeit' },
    { id: 'significant-other', name: 'Significant Other' },
  ],
};

const menMenu = {
  clothing: [
    { id: 'suit', name: 'Mens Suits' },
    { id: 'shirt', name: 'Shirt' },
    { id: 'men-jeans', name: 'Men Jeans' },
    { id: 'sweaters', name: 'Sweaters' },
    { id: 't-shirts', name: 'T-Shirts' },
    { id: 'jackets', name: 'Jackets' },
  ],
  accessories: [
    { id: 'watches', name: 'Watches' },
    { id: 'wallets', name: 'Wallets' },
    { id: 'bags', name: 'Bags' },
    { id: 'sunglasses', name: 'Sunglasses' },
    { id: 'hats', name: 'Hats' },
    { id: 'belts', name: 'Belts' },
  ],
  brands: [
    { id: 're-arranged', name: 'Re-Arranged' },
    { id: 'counterfeit', name: 'Counterfeit' },
    { id: 'full-nelson', name: 'Full Nelson' },
    { id: 'my-way', name: 'My Way' },
  ],
};

const Navigation = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const location = useLocation();

  const { auth, cart } = useSelector((store) => store);

  const cartCount = auth.user ? (cart.cart?.cartItems?.length || 0) : 0;
  const [activeMenu, setActiveMenu] = useState(null);
  const [openAuthModal, setOpenAuthModal] = useState(false);
  const [isUserMenuOpen, setIsUserMenuOpen] = useState(false);
  const userMenuRef = useRef(null);

  const handleOpen = () => setOpenAuthModal(true);
  const handleClose = () => setOpenAuthModal(false);

  const handleCategoryClick = (category, section, item) => {
    navigate(`/${category}/${section}/${item.id}`);
    setActiveMenu(null);
  };

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (userMenuRef.current && !userMenuRef.current.contains(event.target)) {
        setIsUserMenuOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  useEffect(() => {
    if (auth.jwt) {
      dispatch(getUser(auth.jwt));
    }
  }, [auth.jwt]);

  useEffect(() => {
    if (auth.user) {
      dispatch(getCart());
      handleClose();
    }

    if (location.pathname === '/login' || location.pathname === '/register') {
      navigate(-1);
    }
  }, [auth.user]);


  const handleLogout = () => {
    dispatch(logout());
    setIsUserMenuOpen(false);
  };

  return (
    <nav
      className="relative z-50 bg-white border-b border-gray-200"
      onMouseLeave={() => setActiveMenu(null)}
    >
      {/* Main Navigation */}
      <div className="h-12 flex items-center justify-between px-5">
        {/* Left side */}
        <div className="flex items-center h-full">
          <Link to="/" className="flex items-center mr-8">
            <img src="/images/logo.png" alt="Logo" className="w-7 h-7 object-contain" />
          </Link>

          <div className="flex items-center h-full gap-7">
            <div
              className="relative h-full flex items-center"
              onMouseEnter={() => setActiveMenu('women')}
            >
              <Link
                to="/women"
                className={`h-full flex items-center text-sm font-medium ${
                  activeMenu === 'women' ? 'text-purple-800' : 'text-gray-700'
                }`}
              >
                Women
              </Link>
              {activeMenu === 'women' && (
                <div className="absolute left-0 bottom-0 h-[2px] w-full bg-purple-700" />
              )}
            </div>

            <div
              className="relative h-full flex items-center"
              onMouseEnter={() => setActiveMenu('men')}
            >
              <Link
                to="/men"
                className={`h-full flex items-center text-sm font-medium ${
                  activeMenu === 'men' ? 'text-purple-800' : 'text-gray-700'
                }`}
              >
                Men
              </Link>
              {activeMenu === 'men' && (
                <div className="absolute left-0 bottom-0 h-[2px] w-full bg-purple-700" />
              )}
            </div>

            <div className="relative h-full flex items-center" onMouseEnter={() => setActiveMenu(null)}>
              <Link to="/company" className="text-sm font-medium text-gray-700 hover:text-purple-800">
                Company
              </Link>
            </div>

            <div className="relative h-full flex items-center" onMouseEnter={() => setActiveMenu(null)}>
              <Link to="/stores" className="text-sm font-medium text-gray-700 hover:text-purple-800">
                Stores
              </Link>
            </div>
          </div>
        </div>

        {/* Right side */}
        <div className="flex items-center gap-6">
          {/* User avatar + dropdown */}
          {auth.user?.firstName ? (
            <div className="relative" ref={userMenuRef}>
              <button
                onClick={() => setIsUserMenuOpen((prev) => !prev)}
                className="w-9 h-9 rounded-full bg-purple-700 text-white flex items-center justify-center text-sm"
                aria-label="Account menu"
              >
                {auth.user.firstName[0].toUpperCase()}
              </button>

              {isUserMenuOpen && (
                <div className="absolute right-0 top-full mt-2 w-44 bg-white border border-gray-200 rounded-lg shadow-lg py-2">
                  <Link
                    to="/account/profile"
                    onClick={() => setIsUserMenuOpen(false)}
                    className="flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                  >
                    <PersonOutline fontSize="small" />
                    Profile
                  </Link>
                  <Link
                    to="/account/order"
                    onClick={() => setIsUserMenuOpen(false)}
                    className="flex items-center gap-3 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                  >
                    <Inventory2OutlinedIcon fontSize="small" />
                    My Orders
                  </Link>
                  <button
                    onClick={handleLogout}
                    className="flex items-center gap-3 w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                  >
                    <LogoutIcon fontSize="small" />
                    Logout
                  </button>
                </div>
              )}
            </div>
          ) : (
            <Button
              onClick={handleOpen}
              sx={{
                color: 'rgb(55 65 81)',
                textTransform: 'none',
                fontSize: '0.875rem',
                fontWeight: 500,
              }}
            >
              Signin
            </Button>
          )}

          {/* Search */}
          <button className="text-gray-500 hover:text-gray-800" aria-label="Search">
            <SearchIcon fontSize="small" />
          </button>

          {/* Cart */}
          <Link to="/cart" className="relative flex items-center text-gray-500 hover:text-gray-800">
            <ShoppingBagOutlinedIcon fontSize="small" />
            <span className="ml-2 text-xs">{cartCount}</span>
          </Link>
        </div>
      </div>

      {/* Women Mega Menu */}
      {activeMenu === 'women' && (
        <div
          className="absolute left-0 top-full w-full bg-white border-t border-gray-100 shadow-md"
          onMouseEnter={() => setActiveMenu('women')}
        >
          <div className="max-w-7xl mx-auto px-10 py-12">
            <div className="grid grid-cols-5 gap-10">
              <div>
                <h3 className="text-sm font-semibold text-gray-800 mb-4">Clothing</h3>
                <div className="space-y-3">
                  {womenMenu.clothing.map((item) => (
                    <button
                      key={item.id}
                      onClick={() => handleCategoryClick('women', 'clothing', item)}
                      className="block w-full text-left text-sm text-gray-500 hover:text-gray-900"
                    >
                      {item.name}
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <h3 className="text-sm font-semibold text-gray-800 mb-4">Accessories</h3>
                <div className="space-y-3">
                  {womenMenu.accessories.map((item) => (
                    <button
                      key={item.id}
                      onClick={() => handleCategoryClick('women', 'accessories', item)}
                      className="block w-full text-left text-sm text-gray-500 hover:text-gray-900"
                    >
                      {item.name}
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <h3 className="text-sm font-semibold text-gray-800 mb-4">Brands</h3>
                <div className="space-y-3">
                  {womenMenu.brands.map((item) => (
                    <button
                      key={item.id}
                      onClick={() => handleCategoryClick('women', 'brands', item)}
                      className="block w-full text-left text-sm text-gray-500 hover:text-gray-900"
                    >
                      {item.name}
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <h3 className="text-sm font-semibold text-gray-800 mb-1">New Arrivals</h3>
                <Link to="/products?filter=new-arrivals" className="text-sm text-gray-500 hover:text-gray-900">
                  Shop now
                </Link>
              </div>

              <div>
                <h3 className="text-sm font-semibold text-gray-800 mb-1">Basic Tees</h3>
                <Link to="/products?category=basic-tees" className="text-sm text-gray-500 hover:text-gray-900">
                  Shop now
                </Link>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Men Mega Menu */}
      {activeMenu === 'men' && (
        <div
          className="absolute left-0 top-full w-full bg-white border-t border-gray-100 shadow-md"
          onMouseEnter={() => setActiveMenu('men')}
        >
          <div className="max-w-7xl mx-auto px-10 py-12">
            <div className="grid grid-cols-5 gap-10">
              <div>
                <h3 className="text-sm font-semibold text-gray-800 mb-4">Clothing</h3>
                <div className="space-y-3">
                  {menMenu.clothing.map((item) => (
                    <button
                      key={item.id}
                      onClick={() => handleCategoryClick('men', 'clothing', item)}
                      className="block w-full text-left text-sm text-gray-500 hover:text-gray-900"
                    >
                      {item.name}
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <h3 className="text-sm font-semibold text-gray-800 mb-4">Accessories</h3>
                <div className="space-y-3">
                  {menMenu.accessories.map((item) => (
                    <button
                      key={item.id}
                      onClick={() => handleCategoryClick('men', 'accessories', item)}
                      className="block w-full text-left text-sm text-gray-500 hover:text-gray-900"
                    >
                      {item.name}
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <h3 className="text-sm font-semibold text-gray-800 mb-4">Brands</h3>
                <div className="space-y-3">
                  {menMenu.brands.map((item) => (
                    <button
                      key={item.id}
                      onClick={() => handleCategoryClick('men', 'brands', item)}
                      className="block w-full text-left text-sm text-gray-500 hover:text-gray-900"
                    >
                      {item.name}
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <h3 className="text-sm font-semibold text-gray-800 mb-1">New Arrivals</h3>
                <Link to="/products?filter=new-arrivals" className="text-sm text-gray-500 hover:text-gray-900">
                  Shop now
                </Link>
              </div>
            </div>
          </div>
        </div>
      )}

      <AuthModel handleClose={handleClose} open={openAuthModal} />
    </nav>
  );
};

export default Navigation;