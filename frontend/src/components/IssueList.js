import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { issueService } from '../services/api';
import './IssueList.css';

function IssueList() {
  const [issues, setIssues] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('all');

  useEffect(() => {
    fetchIssues();
  }, [filter]);

  const fetchIssues = async () => {
    setLoading(true);
    try {
      let response;
      if (filter === 'all') {
        response = await issueService.getAll();
      } else if (filter === 'my') {
        response = await issueService.getMyIssues();
      } else if (filter === 'assigned') {
        response = await issueService.getAssignedIssues();
      }
      setIssues(response.data);
    } catch (error) {
      console.error('Failed to fetch issues:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this issue?')) {
      try {
        await issueService.delete(id);
        fetchIssues();
      } catch (error) {
        console.error('Failed to delete issue:', error);
      }
    }
  };

  return (
    <div className="issue-list">
      <div className="list-header">
        <h1>Issues</h1>
        <Link to="/issues/new" className="btn btn-primary">Create Issue</Link>
      </div>

      <div className="filter-buttons">
        <button
          className={`filter-btn ${filter === 'all' ? 'active' : ''}`}
          onClick={() => setFilter('all')}
        >
          All Issues
        </button>
        <button
          className={`filter-btn ${filter === 'my' ? 'active' : ''}`}
          onClick={() => setFilter('my')}
        >
          My Issues
        </button>
        <button
          className={`filter-btn ${filter === 'assigned' ? 'active' : ''}`}
          onClick={() => setFilter('assigned')}
        >
          Assigned to Me
        </button>
      </div>

      {loading ? (
        <div className="loading">Loading issues...</div>
      ) : issues.length === 0 ? (
        <div className="card">
          <p>No issues found.</p>
        </div>
      ) : (
        <div className="card">
          <div className="table-responsive">
            <table className="issues-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Title</th>
                  <th>Status</th>
                  <th>Priority</th>
                  <th>Reporter</th>
                  <th>Assignee</th>
                  <th>Created</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {issues.map((issue) => (
                  <tr key={issue.id}>
                    <td>#{issue.id}</td>
                    <td className="issue-title-cell">{issue.title}</td>
                    <td>
                      <span className={`badge badge-${issue.status.toLowerCase().replace('_', '-')}`}>
                        {issue.status}
                      </span>
                    </td>
                    <td>
                      <span className={`badge badge-${issue.priority.toLowerCase()}`}>
                        {issue.priority}
                      </span>
                    </td>
                    <td>{issue.reporter.fullName}</td>
                    <td>{issue.assignee?.fullName || 'Unassigned'}</td>
                    <td>{new Date(issue.createdAt).toLocaleDateString()}</td>
                    <td className="actions-cell">
                      <Link to={`/issues/${issue.id}/edit`} className="btn-small btn-secondary">
                        Edit
                      </Link>
                      <button
                        onClick={() => handleDelete(issue.id)}
                        className="btn-small btn-danger"
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}

export default IssueList;
