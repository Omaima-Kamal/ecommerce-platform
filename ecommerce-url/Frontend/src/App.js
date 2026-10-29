import './App.css';
import { Route, Routes } from 'react-router-dom';
import CustomerRouters from './Routers/CustomerRouters';
import { useDispatch } from 'react-redux';
import { useEffect } from 'react';
import { getUser } from './State/Auth/Action';

function App() {
  const dispatch = useDispatch();

  useEffect(() => {
    const jwt = localStorage.getItem('jwt');

    if (jwt && jwt !== 'null' && jwt !== 'undefined') {
      dispatch(getUser());
    }
  }, [dispatch]);

  return (
    <div className="">
      <Routes>
        <Route path="/*" element={<CustomerRouters />} />
      </Routes>
    </div>
  );
}

export default App;
