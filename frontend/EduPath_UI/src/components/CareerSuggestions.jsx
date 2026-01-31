import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { toast } from 'react-toastify';
import '../Login.css';

const CareerSuggestions = () => {
  const { studentId } = useParams();
  const navigate = useNavigate();
  const [careers, setCareers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [visibleFormula, setVisibleFormula] = useState(null);

  const formulas = {
    "ENGINEERING": "0.5 * Mathematics + 0.3 * Physics + 0.2 * Computer Science",
    "DESIGN": "0.6 * Art + 0.4 * Drawing",
    "MEDICINE": "0.4 * Biology + 0.3 * Chemistry + 0.3 * Physics",
    "SPORTS": "Physical Education",
    "SINGING": "0.7 * choir + 0.3 * sing",
    "COMMERCE": "0.5 * Mathematics + 0.3 * Economics + 0.2 * English",
    "DATA_SCIENCE": "0.5 * Mathematics + 0.5 * Computer Science",
    "ARCHITECTURE": "0.4 * Mathematics + 0.4 * Art + 0.2 * Physics",
    "LAW" : "0.5 * English + 0.4 * History + 0.2 * Mathematics",
    "MEDIA" : "0.5 * English + 0.3 * Art/Drawing + 0.2 * Music",
    
  };

  useEffect(() => {
    const fetchCareers = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/api/students/${studentId}/profiles/latest`);
        const profileData = response.data.profile;
        
        // Transform profile object to array
        const careersArray = Object.entries(profileData).map(([career, data]) => ({
          career: career, // e.g., "Engineering"
          score: data.score,
          confidence: data.confidence,
          // We can generate a detailed formula string from 'basedOn' if needed, 
          // but for now we'll stick to the static formula map or fallback.
          // map lookup needs uppercase
          formulaKey: career.toUpperCase()
        }));

        // Sort by score descending
        const sortedData = careersArray.sort((a, b) => b.score - a.score);
        setCareers(sortedData);
      } catch (error) {
        console.error("Error fetching career suggestions", error);
        toast.error("Failed to load career suggestions.");
      } finally {
        setLoading(false);
      }
    };

    if (studentId) {
      fetchCareers();
    }
  }, [studentId]);

  const getConfidenceColor = (confidence) => {
    switch (confidence) {
      case 'HIGH': return '#4ade80';
      case 'MEDIUM': return '#fbbf24';
      case 'LOW': return '#ef4444';
      default: return '#fff';
    }
  };

  const toggleFormula = (careerName) => {
    if (visibleFormula === careerName) {
      setVisibleFormula(null);
    } else {
      setVisibleFormula(careerName);
    }
  };

  if (loading) {
    return (
      <div className="login-page-container">
        <div style={{ color: 'white', fontSize: '1.5rem' }}>Loading Career Suggestions...</div>
      </div>
    );
  }

  return (
    <div className="login-page-container">
      <div className="login-card" style={{ maxWidth: '800px', width: '90%' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
          <h1 className="app-title" style={{ margin: 0 }}>Career Recommendations</h1>
          <button 
            className="toggle-btn"
            onClick={() => navigate(`/student-profile/${studentId}`)}
            style={{ padding: '0.5rem 1rem' }}
          >
            ← Back to Profile
          </button>
        </div>

        <div className="career-list">
          {careers.map((item) => (
            <div key={item.career} className="career-item" style={{ 
              background: 'rgba(255, 255, 255, 0.05)', 
              borderRadius: '12px', 
              padding: '1.5rem', 
              marginBottom: '1rem',
              display: 'flex',
              flexDirection: 'column',
              gap: '0.5rem',
              border: '1px solid var(--glass-border)'
            }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h3 style={{ margin: 0, fontSize: '1.5rem', color: '#fff' }}>{item.career}</h3>
                <span style={{ 
                  background: getConfidenceColor(item.confidence), 
                  color: '#1e1b4b', 
                  padding: '0.25rem 0.75rem', 
                  borderRadius: '20px', 
                  fontSize: '0.8rem', 
                  fontWeight: 'bold' 
                }}>
                  {item.confidence} CONFIDENCE
                </span>
              </div>
              
              <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                <div style={{ flex: 1, background: 'rgba(255,255,255,0.1)', height: '10px', borderRadius: '5px', overflow: 'hidden' }}>
                  <div style={{ 
                    width: `${Math.min(item.score, 100)}%`, 
                    background: 'var(--primary-color)', 
                    height: '100%',
                    borderRadius: '5px'
                  }}></div>
                </div>
                <span style={{ color: 'var(--text-light)', fontWeight: 'bold' }}>{item.score.toFixed(1)}</span>
                
                <button 
                  onClick={() => toggleFormula(item.career)}
                  style={{ 
                    background: 'none', 
                    border: '1px solid var(--text-dim)', 
                    color: 'var(--text-dim)', 
                    borderRadius: '50%', 
                    width: '24px', 
                    height: '24px', 
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontSize: '0.8rem'
                  }}
                  title="Show calculation formula"
                >
                  i
                </button>
              </div>

              {visibleFormula === item.career && (
                <div style={{ 
                  marginTop: '0.5rem', 
                  padding: '1rem', 
                  background: 'rgba(0,0,0,0.2)', 
                  borderRadius: '8px', 
                  fontSize: '0.9rem', 
                  color: '#a5b4fc',
                  borderLeft: '3px solid var(--secondary-color)'
                }}>
                  <strong>Calculation Formula:</strong><br/>
                  {formulas[item.formulaKey] || "Check academic weighting matrix"}
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default CareerSuggestions;
