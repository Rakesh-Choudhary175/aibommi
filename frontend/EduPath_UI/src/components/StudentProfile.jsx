import React, { useState } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import ChatWindow from './ChatWindow';
import '../Login.css';

const data = {
  Maths: [
    { year: '2021', score: 65 },
    { year: '2022', score: 70 },
    { year: '2023', score: 75 },
    { year: '2024', score: 82 },
    { year: '2025', score: 88 },
  ],
  Science: [
    { year: '2021', score: 60 },
    { year: '2022', score: 65 },
    { year: '2023', score: 72 },
    { year: '2024', score: 78 },
    { year: '2025', score: 85 },
  ],
  Arts: [
    { year: '2021', score: 80 },
    { year: '2022', score: 82 },
    { year: '2023', score: 85 },
    { year: '2024', score: 88 },
    { year: '2025', score: 92 },
  ],
};

const StudentProfile = () => {
  const [activeSubject, setActiveSubject] = useState('Maths');
  const [showChat, setShowChat] = useState(false);

  return (
    <div className="login-page-container">
      <div className={`student-profile-layout ${showChat ? 'split-view' : ''}`}>
        
        {/* Main Content Area (Graph & Controls) */}
        <div className="login-card profile-card">
          <h1 className="app-title">Student Performance Profile</h1>
          
          <div className="login-toggle">
            {Object.keys(data).map((subject) => (
              <button
                key={subject}
                className={`toggle-btn ${activeSubject === subject ? 'active' : ''}`}
                onClick={() => setActiveSubject(subject)}
              >
                {subject}
              </button>
            ))}
          </div>

          <div className="chart-container" style={{ height: '300px', margin: '2rem 0' }}>
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={data[activeSubject]}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.1)" />
                <XAxis dataKey="year" stroke="#d1d5db" />
                <YAxis stroke="#d1d5db" />
                <Tooltip 
                  contentStyle={{ backgroundColor: '#1e1b4b', border: '1px solid rgba(255,255,255,0.2)', color: '#fff' }}
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
          </div>

          <div className="action-buttons" style={{ display: 'flex', justifyContent: 'center' }}>
            <button 
              className="login-btn" 
              style={{ padding: '1rem 3rem', fontSize: '1.2rem' }}
              onClick={() => setShowChat(true)}
            >
              Suggest Career Path
            </button>
          </div>
        </div>

        {/* Chat Side Panel */}
        {showChat && (
          <div className="chat-panel">
            <ChatWindow onClose={() => setShowChat(false)} />
          </div>
        )}

      </div>
    </div>
  );
};

export default StudentProfile;
