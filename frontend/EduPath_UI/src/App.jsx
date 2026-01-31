import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import LoginPage from './components/LoginPage';
import StudentProfile from './components/StudentProfile';
import './App.css';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/student-profile" element={<StudentProfile />} />
      </Routes>
    </Router>
  );
}

export default App
