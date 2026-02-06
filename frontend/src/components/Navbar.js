import React from 'react';
import { Link } from 'react-router-dom';
import './Navbar.css';

function Navbar({ user, onLogout }) {
  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/dashboard" className="navbar-brand">
          Issue Tracker
        </Link>
        <div className="navbar-menu">
          <Link to="/dashboard" className="navbar-link">Dashboard</Link>
          <Link to="/issues" className="navbar-link">Issues</Link>
          <Link to="/analytics" className="navbar-link">Analytics</Link>
        </div>
        <div className="navbar-user">
          <span className="navbar-username">{user.username}</span>
          <button onClick={onLogout} className="btn btn-secondary">Logout</button>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
