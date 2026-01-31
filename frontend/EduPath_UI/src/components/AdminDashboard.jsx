import React, { useState } from 'react';
import axios from 'axios';
import { toast } from 'react-toastify';
import '../Login.css';

const AdminDashboard = () => {
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [extraFile, setExtraFile] = useState(null);
  const [extraLoading, setExtraLoading] = useState(false);
  const [analysisStatus, setAnalysisStatus] = useState('idle'); // idle, analyzing, success, error

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!file) {
      toast.error("Please select a file first.");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

    setLoading(true);
    try {
      const response = await axios.post(
        "http://localhost:8080/api/students/upload/academics",
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );

      if (response.status === 201) {
        toast.success("Upload successful!");
        setFile(null);
        // Reset file input if needed, though state null handles the value reference usually via key or ref
      } else {
        toast.warning("Upload completed but with unexpected status.");
      }
    } catch (error) {
      console.error("Upload error:", error);
      toast.error("Some error occurred during upload.");
    } finally {
      setLoading(false);
    }
  };

  const handleExtraFileChange = (e) => {
    setExtraFile(e.target.files[0]);
  };

  const handleExtraUpload = async (e) => {
    e.preventDefault();
    if (!extraFile) {
      toast.error("Please select an extracurricular file first.");
      return;
    }

    const formData = new FormData();
    formData.append("file", extraFile);

    setExtraLoading(true);
    try {
      const response = await axios.post(
        "http://localhost:8080/api/students/upload/extracurriculars",
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );

      if (response.status === 201) {
        toast.success("Extracurricular Upload successful!");
        setExtraFile(null);
      } else {
        toast.warning("Upload completed but with unexpected status.");
      }
    } catch (error) {
      console.error("Upload error:", error);
      toast.error("Some error occurred during extracurricular upload.");
    } finally {
      setExtraLoading(false);
    }
  };

  const handleAnalyze = async () => {
    setAnalysisStatus('analyzing');
    try {
      // Assuming it's a POST request to trigger analysis
      await axios.post("http://localhost:8080/api/students/analyze");
      setAnalysisStatus('success');
      toast.success("Data analysis completed successfully!");
    } catch (error) {
      console.error("Analysis error:", error);
      setAnalysisStatus('error');
      toast.error("Failed to analyze data.");
    }
  }; // Logic for analysis button

  return (
    <div className="login-page-container">
      <div className="login-card" style={{ maxWidth: '600px' }}>
        <h1 className="app-title">Admin Dashboard</h1>
        
        <div className="admin-upload-section">
          <h2>Upload Academic Records</h2>
          <p style={{ color: 'var(--text-dim)', marginBottom: '2rem' }}>
            Select a file to upload students' academic data.
          </p>

          <form onSubmit={handleUpload} className="login-form">
            <div className="form-group">
              <label htmlFor="file-upload" className="file-upload-label">
                {file ? file.name : "Choose File"}
              </label>
              <input
                id="file-upload"
                type="file"
                onChange={handleFileChange}
                style={{ display: 'none' }}
              />
              <button 
                type="button" 
                className="toggle-btn" 
                onClick={() => document.getElementById('file-upload').click()}
                style={{ border: '1px dashed var(--text-dim)', marginTop: '0.5rem' }}
              >
                Browse Files
              </button>
            </div>

            <button 
              type="submit" 
              className="login-btn" 
              disabled={loading}
              style={{ opacity: loading ? 0.7 : 1 }}
            >
              {loading ? "Uploading..." : "Upload File"}
            </button>
          </form>

          <h2 style={{ marginTop: '2rem' }}>Upload Extracurricular Data</h2>
          <p style={{ color: 'var(--text-dim)', marginBottom: '2rem' }}>
            Select an .xlsx file for extracurricular activities.
          </p>

          <form onSubmit={handleExtraUpload} className="login-form">
            <div className="form-group">
              <label htmlFor="extra-file-upload" className="file-upload-label">
                {extraFile ? extraFile.name : "Choose Excel File"}
              </label>
              <input
                id="extra-file-upload"
                type="file"
                accept=".xlsx"
                onChange={handleExtraFileChange}
                style={{ display: 'none' }}
              />
              <button 
                type="button" 
                className="toggle-btn" 
                onClick={() => document.getElementById('extra-file-upload').click()}
                style={{ border: '1px dashed var(--text-dim)', marginTop: '0.5rem' }}
              >
                Browse Files
              </button>
            </div>

            <button 
              type="submit" 
              className="login-btn" 
              disabled={extraLoading}
              style={{ opacity: extraLoading ? 0.7 : 1 }}
            >
              {extraLoading ? "Uploading..." : "Upload Extracurricular"}
            </button>
          </form>

          <div style={{ marginTop: '3rem', borderTop: '1px solid var(--glass-border)', paddingTop: '2rem' }}>
            <h2>Data Analysis</h2>
            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', flexDirection: 'column' }}>
              <button 
                className="login-btn" 
                onClick={handleAnalyze}
                disabled={analysisStatus === 'analyzing'}
                style={{ 
                  background: analysisStatus === 'analyzing' ? '#4b5563' : 'var(--secondary-color)',
                  width: '100%'
                }}
              >
                {analysisStatus === 'analyzing' ? "Analyzing..." : "Analyze Data"}
              </button>
              
              {analysisStatus === 'analyzing' && (
                <div className="loader" style={{ border: '4px solid #f3f3f3', borderTop: '4px solid #3498db', borderRadius: '50%', width: '30px', height: '30px', animation: 'spin 1s linear infinite' }}></div>
              )}
              
              {analysisStatus === 'success' && (
                <div style={{ color: '#4ade80', fontSize: '2rem', animation: 'fadeIn 0.5s' }}>
                  ✓ Analysis Complete
                </div>
              )}

              {analysisStatus === 'error' && (
                <div style={{ color: '#ef4444', fontSize: '2rem', animation: 'fadeIn 0.5s' }}>
                  ✕ Analysis Failed
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
      <style>{`
        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  );
};

export default AdminDashboard;
