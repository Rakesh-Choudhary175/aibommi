import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const AdminLogin = () => {
  const [formData, setFormData] = useState({
    adminId: '',
    password: '',
  });
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log('Admin Login Data:', formData);
    // Add validation logic here if needed
    navigate('/admin-dashboard');
  };

  return (
    <form className="login-form admin-login" onSubmit={handleSubmit}>
      <h2>Admin Login</h2>
      <div className="form-group">
        <label htmlFor="adminId">Admin ID</label>
        <input
          type="text"
          id="adminId"
          name="adminId"
          value={formData.adminId}
          onChange={handleChange}
          placeholder="Enter Admin ID"
          required
        />
      </div>
      <div className="form-group">
        <label htmlFor="password">Password</label>
        <input
          type="password"
          id="password"
          name="password"
          value={formData.password}
          onChange={handleChange}
          placeholder="Enter Password"
          required
        />
      </div>
      <button type="submit" className="login-btn">Login as Admin</button>
    </form>
  );
};

export default AdminLogin;
