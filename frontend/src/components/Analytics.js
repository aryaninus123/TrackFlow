import React, { useState, useEffect } from 'react';
import { analyticsService } from '../services/api';
import './Analytics.css';

function Analytics() {
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAnalytics();
  }, []);

  const fetchAnalytics = async () => {
    try {
      const response = await analyticsService.getAnalytics();
      setAnalytics(response.data);
    } catch (error) {
      console.error('Failed to fetch analytics:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="loading">Loading analytics...</div>;
  }

  if (!analytics) {
    return <div className="card">Failed to load analytics</div>;
  }

  return (
    <div className="analytics">
      <h1 className="analytics-title">Analytics Dashboard</h1>

      <div className="card">
        <h2 className="card-title">Overview</h2>
        <div className="analytics-grid">
          <div className="analytics-item">
            <div className="analytics-value">{analytics.totalIssues}</div>
            <div className="analytics-label">Total Issues</div>
          </div>
          <div className="analytics-item">
            <div className="analytics-value">
              {analytics.averageResolutionTimeHours?.toFixed(1) || 'N/A'}
            </div>
            <div className="analytics-label">Avg Resolution Time (hours)</div>
          </div>
        </div>
      </div>

      <div className="analytics-row">
        <div className="card">
          <h2 className="card-title">Status Distribution</h2>
          <div className="distribution-list">
            {Object.entries(analytics.statusDistribution || {}).map(([status, count]) => (
              <div key={status} className="distribution-item">
                <span className="distribution-label">
                  <span className={`badge badge-${status.toLowerCase().replace('_', '-')}`}>
                    {status}
                  </span>
                </span>
                <div className="distribution-bar-container">
                  <div
                    className="distribution-bar"
                    style={{
                      width: `${(count / analytics.totalIssues) * 100}%`,
                    }}
                  />
                  <span className="distribution-count">{count}</span>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="card">
          <h2 className="card-title">Priority Distribution</h2>
          <div className="distribution-list">
            {Object.entries(analytics.priorityDistribution || {}).map(([priority, count]) => (
              <div key={priority} className="distribution-item">
                <span className="distribution-label">
                  <span className={`badge badge-${priority.toLowerCase()}`}>
                    {priority}
                  </span>
                </span>
                <div className="distribution-bar-container">
                  <div
                    className="distribution-bar"
                    style={{
                      width: `${(count / analytics.totalIssues) * 100}%`,
                    }}
                  />
                  <span className="distribution-count">{count}</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

export default Analytics;
