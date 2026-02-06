import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { issueService, userService } from '../services/api';
import './IssueForm.css';

function IssueForm() {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEdit = !!id;

  const [formData, setFormData] = useState({
    title: '',
    description: '',
    status: 'OPEN',
    priority: 'MEDIUM',
    assigneeId: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [users, setUsers] = useState([]);

  useEffect(() => {
    fetchUsers();
    if (isEdit) {
      fetchIssue();
    }
  }, [id]);

  const fetchUsers = async () => {
    try {
      const response = await userService.getAll();
      setUsers(response.data);
    } catch (error) {
      console.error('Failed to fetch users:', error);
    }
  };

  const fetchIssue = async () => {
    try {
      const response = await issueService.getById(id);
      const issue = response.data;
      setFormData({
        title: issue.title,
        description: issue.description || '',
        status: issue.status,
        priority: issue.priority,
        assigneeId: issue.assignee?.id || '',
      });
    } catch (error) {
      console.error('Failed to fetch issue:', error);
      setError('Failed to load issue');
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const submitData = {
        ...formData,
        assigneeId: formData.assigneeId ? parseInt(formData.assigneeId) : null,
      };

      if (isEdit) {
        await issueService.update(id, submitData);
      } else {
        await issueService.create(submitData);
      }
      navigate('/issues');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save issue');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="issue-form-container">
      <div className="card">
        <h2 className="card-title">{isEdit ? 'Edit Issue' : 'Create New Issue'}</h2>
        {error && <div className="error-message">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Title *</label>
            <input
              type="text"
              name="title"
              value={formData.title}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Description</label>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleChange}
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Status *</label>
              <select name="status" value={formData.status} onChange={handleChange}>
                <option value="OPEN">Open</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="RESOLVED">Resolved</option>
                <option value="CLOSED">Closed</option>
                <option value="REOPENED">Reopened</option>
              </select>
            </div>

            <div className="form-group">
              <label>Priority *</label>
              <select name="priority" value={formData.priority} onChange={handleChange}>
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
                <option value="CRITICAL">Critical</option>
              </select>
            </div>
          </div>

          <div className="form-group">
            <label>Assignee (optional)</label>
            <select 
              name="assigneeId" 
              value={formData.assigneeId} 
              onChange={handleChange}
            >
              <option value="">Unassigned</option>
              {users.map(user => (
                <option key={user.id} value={user.id}>
                  {user.username} ({user.email})
                </option>
              ))}
            </select>
          </div>

          <div className="form-actions">
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Saving...' : isEdit ? 'Update Issue' : 'Create Issue'}
            </button>
            <button type="button" className="btn btn-secondary" onClick={() => navigate('/issues')}>
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default IssueForm;
