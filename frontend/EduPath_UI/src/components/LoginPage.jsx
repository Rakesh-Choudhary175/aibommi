import React, { useState } from 'react';
import AdminLogin from './AdminLogin';
import ParentLogin from './ParentLogin';
import '../Login.css'; // We will create this for specific login styling

const LoginPage = () => {
  const [loginType, setLoginType] = useState('admin'); // 'admin' or 'parent'

  return (
    <div className="login-page-container">
      <div className="login-card">
        <h1 className="app-title">EduPath Portal</h1>
        <div className="login-toggle">
          <button
            className={`toggle-btn ${loginType === 'admin' ? 'active' : ''}`}
            onClick={() => setLoginType('admin')}
          >
            Admin Login
          </button>
          <button
            className={`toggle-btn ${loginType === 'parent' ? 'active' : ''}`}
            onClick={() => setLoginType('parent')}
          >
            Student Login
          </button>
        </div>

        <div className="login-content">
          {loginType === 'admin' ? <AdminLogin /> : <ParentLogin />}
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
