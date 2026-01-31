import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const ParentLogin = () => {
  const [parentId, setParentId] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setParentId(e.target.value);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log('Student Login ID:', parentId);
    
    if (parentId.trim()) {
      // Remove 'S' (case-insensitive) and parse to integer (e.g., "S001" -> 1)
      const cleanId = parentId.trim().replace(/^s/i, '');
      const studentId = parseInt(cleanId, 10);

      if (!isNaN(studentId)) {
        navigate(`/student-profile/${studentId}`);
      } else {
        // Fallback for non-numeric IDs if valid
        navigate(`/student-profile/${parentId}`);
      }
    }
  };

  return (
    <form className="login-form parent-login" onSubmit={handleSubmit}>
      <h2>Student Login</h2>
      <div className="form-group">
        <label htmlFor="parentId">Student ID</label>
        <input
          type="text"
          id="parentId"
          name="parentId"
          value={parentId}
          onChange={handleChange}
          placeholder="Enter Student ID"
          required
        />
      </div>
      <button type="submit" className="login-btn">Login as Student</button>
    </form>
  );
};

export default ParentLogin;
