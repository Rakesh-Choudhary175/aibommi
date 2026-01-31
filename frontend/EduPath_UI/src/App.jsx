import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import LoginPage from './components/LoginPage';
import StudentProfile from './components/StudentProfile';
import AdminDashboard from './components/AdminDashboard';
import './App.css';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/student-profile/:studentId" element={<StudentProfile />} />
        <Route path="/admin-dashboard" element={<AdminDashboard />} />
      </Routes>
      <ToastContainer position="top-right" autoClose={3000} />
    </Router>
  );
}

export default App
