import React, { useState, useEffect } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { toast } from 'react-toastify';
import ChatWindow from './ChatWindow';
import '../Login.css';

const StudentProfile = () => {
  const { studentId } = useParams();
  const navigate = useNavigate();
  const [activeSubject, setActiveSubject] = useState('');
  const [showChat, setShowChat] = useState(false);
  const [startChat, setStartChat] = useState(false);
  const [academicData, setAcademicData] = useState({});
  const [subjects, setSubjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [studentName, setStudentName] = useState('');

  useEffect(() => {
    const fetchStudentData = async () => {
      try {
        const [academicsRes, studentRes] = await Promise.all([
          axios.get(`http://localhost:8080/api/students/${studentId}/academics`),
          axios.get(`http://localhost:8080/api/students/${studentId}`)
        ]);
        
        processData(academicsRes.data);
        if (studentRes.data && studentRes.data.name) {
          setStudentName(studentRes.data.name);
        }
      } catch (error) {
        console.error("Error fetching student data", error);
        toast.error("Failed to load student data.");
        setLoading(false);
      }
    };

    if (studentId) {
      fetchStudentData();
    }
  }, [studentId]);

  const processData = (rawData) => {
    if (!rawData || !Array.isArray(rawData)) return;

    const groupedData = {};
    const subjectList = new Set();
    
    // Process and sort by year
    const sortedData = [...rawData].sort((a, b) => a.year - b.year);

    sortedData.forEach(item => {
      const subject = item.subject;
      subjectList.add(subject);
      
      if (!groupedData[subject]) {
        groupedData[subject] = [];
      }

      // Calculate percentage and format
      const percentage = (item.marks / item.maxMarks) * 100;
      
      groupedData[subject].push({
        year: item.year.toString(),
        score: Math.round(percentage),
        rawMarks: item.marks,
        maxMarks: item.maxMarks
      });
    });

    const subjectsArr = Array.from(subjectList);
    setAcademicData(groupedData);
    setSubjects(subjectsArr);
    if (subjectsArr.length > 0) {
      setActiveSubject(subjectsArr[0]);
    }
    setLoading(false);
  };

  if (loading) {
    return (
      <div className="login-page-container">
        <div style={{ color: 'white', fontSize: '1.5rem' }}>Loading Student Profile...</div>
      </div>
    );
  }

  return (
    <div className="login-page-container">
      <div className={`student-profile-layout ${showChat ? 'split-view' : ''}`}>
        
        {/* Main Content Area (Graph & Controls) */}
        <div className="login-card profile-card">
          <h1 className="app-title">
            Student Performance Profile {studentName && `- ${studentName}`}
          </h1>
          
          <div className="login-toggle" style={{ gap: '5px', overflowX: 'auto' }}>
            {subjects.map((subject) => (
              <button
                key={subject}
                className={`toggle-btn ${activeSubject === subject ? 'active' : ''}`}
                onClick={() => setActiveSubject(subject)}
                style={{ flex: 'none' }} 
              >
                {subject}
              </button>
            ))}
          </div>

          <div className="chart-container" style={{ height: '300px', margin: '2rem 0' }}>
            {activeSubject && academicData[activeSubject] ? (
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={academicData[activeSubject]}>
                  <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.1)" />
                  <XAxis dataKey="year" stroke="#d1d5db" />
                  <YAxis stroke="#d1d5db" domain={[0, 100]} />
                  <Tooltip 
                    contentStyle={{ backgroundColor: '#1e1b4b', border: '1px solid rgba(255,255,255,0.2)', color: '#fff' }}
                    formatter={(value, name, props) => [`${value}% (${props.payload.rawMarks}/${props.payload.maxMarks})`, "Score"]}
                  />
                  <Line 
                    type="monotone" 
                    dataKey="score" 
                    stroke="#4f46e5" 
                    strokeWidth={3} 
                    dot={{ r: 6, fill: '#818cf8', strokeWidth: 2 }} 
                    activeDot={{ r: 8 }} 
                  />
                </LineChart>
              </ResponsiveContainer>
            ) : (
                <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', color: 'var(--text-dim)' }}>
                    No data available for this subject.
                </div>
            )}
          </div>


          <div className="action-buttons" style={{ display: 'flex', justifyContent: 'center', gap: '1rem' }}>
            <button 
              className="login-btn" 
              style={{ padding: '1rem 2rem', fontSize: '1.1rem', background: 'var(--secondary-color)' }}
              onClick={() => navigate(`/student-profile/${studentId}/career`)}
            >
              View Career Report
            </button>
            <button 
              className="login-btn" 
              style={{ padding: '1rem 2rem', fontSize: '1.1rem' }}
              onClick={() => {
                if (!startChat) setStartChat(true);
                setShowChat(true);
              }}
            >
              Chat with AI
            </button>
          </div>
        </div>

        {/* Chat Side Panel */}
        {startChat && (
          <div className="chat-panel" style={{ display: showChat ? 'block' : 'none' }}>
            <ChatWindow onClose={() => setShowChat(false)} />
          </div>
        )}

      </div>
    </div>
  );
};

export default StudentProfile;
