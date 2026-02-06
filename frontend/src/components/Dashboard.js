import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { issueService, analyticsService } from '../services/api';
import './Dashboard.css';

function Dashboard() {
  const [stats, setStats] = useState(null);
  const [recentIssues, setRecentIssues] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const [analyticsRes, issuesRes] = await Promise.all([
        analyticsService.getAnalytics(),
        issueService.getAll(),
      ]);
      setStats(analyticsRes.data);
      setRecentIssues(issuesRes.data.slice(0, 5));
    } catch (error) {
      console.error('Failed to fetch dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="loading">Loading dashboard...</div>;
  }

  return (
    <div className="dashboard">
      <h1 className="dashboard-title">Dashboard</h1>
      
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-value">{stats?.totalIssues || 0}</div>
          <div className="stat-label">Total Issues</div>
        </div>
        <div className="stat-card">
          <div className="stat-value">{stats?.openIssues || 0}</div>
          <div className="stat-label">Open Issues</div>
        </div>
        <div className="stat-card">
          <div className="stat-value">{stats?.inProgressIssues || 0}</div>
          <div className="stat-label">In Progress</div>
        </div>
        <div className="stat-card">
          <div className="stat-value">{stats?.closedIssues || 0}</div>
          <div className="stat-label">Closed Issues</div>
        </div>
      </div>

      <div className="dashboard-actions">
        <Link to="/issues/new" className="btn btn-primary">Create New Issue</Link>
        <Link to="/issues" className="btn btn-secondary">View All Issues</Link>
        <Link to="/analytics" className="btn btn-secondary">View Analytics</Link>
      </div>

      <div className="card">
        <h2 className="card-title">Recent Issues</h2>
        {recentIssues.length === 0 ? (
          <p>No issues yet. Create your first issue!</p>
        ) : (
          <div className="issues-list">
            {recentIssues.map((issue) => (
              <Link to={`/issues/${issue.id}/edit`} key={issue.id} className="issue-item">
                <div className="issue-header">
                  <span className="issue-title">{issue.title}</span>
                  <span className={`badge badge-${issue.status.toLowerCase().replace('_', '-')}`}>
                    {issue.status}
                  </span>
                </div>
                <div className="issue-meta">
                  <span className={`badge badge-${issue.priority.toLowerCase()}`}>
                    {issue.priority}
                  </span>
                  <span className="issue-reporter">
                    Reported by {issue.reporter.fullName}
                  </span>
                </div>
              </Link>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

export default Dashboard;
