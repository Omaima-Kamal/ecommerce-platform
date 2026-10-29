import React, { useState } from 'react'
import { Box, Modal } from '@mui/material'
import { RegisterForm } from './RegisterForm';
import { LoginForm } from './LoginForm';

const style = {
  position: 'absolute',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: 500,
  bgcolor: 'background.paper',
  outline: "none",
  boxShadow: 24,
  p: 4
};

export const AuthModel = ({ handleClose, open }) => {
  const [showLogin, setShowLogin] = useState(true);

  return (
    <div>
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <Box sx={style}>
          {showLogin
            ? <LoginForm switchToRegister={() => setShowLogin(false)} />
            : <RegisterForm switchToLogin={() => setShowLogin(true)} />
          }
        </Box>
      </Modal>
    </div>
  )
}