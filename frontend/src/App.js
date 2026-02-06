import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login';
import Register from './components/Register';
import Dashboard from './components/Dashboard';
import IssueList from './components/IssueList';
import IssueForm from './components/IssueForm';
import Analytics from './components/Analytics';
import Navbar from './components/Navbar';
import './App.css';

function App() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('token');
    const userData = localStorage.getItem('user');
    if (token && userData) {
      setUser(JSON.parse(userData));
    }
    setLoading(false);
  }, []);

  const handleLogin = (userData) => {
    setUser(userData);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  };

  if (loading) {
    return <div className="loading">Loading...</div>;
  }

  return (
    <Router>
      <div className="App">
        {user && <Navbar user={user} onLogout={handleLogout} />}
        <div className="container">
          <Routes>
            <Route path="/login" element={
              user ? <Navigate to="/dashboard" /> : <Login onLogin={handleLogin} />
            } />
            <Route path="/register" element={
              user ? <Navigate to="/dashboard" /> : <Register onLogin={handleLogin} />
            } />
            <Route path="/dashboard" element={
              user ? <Dashboard /> : <Navigate to="/login" />
            } />
            <Route path="/issues" element={
              user ? <IssueList /> : <Navigate to="/login" />
            } />
            <Route path="/issues/new" element={
              user ? <IssueForm /> : <Navigate to="/login" />
            } />
            <Route path="/issues/:id/edit" element={
              user ? <IssueForm /> : <Navigate to="/login" />
            } />
            <Route path="/analytics" element={
              user ? <Analytics /> : <Navigate to="/login" />
            } />
            <Route path="/" element={<Navigate to={user ? "/dashboard" : "/login"} />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;
