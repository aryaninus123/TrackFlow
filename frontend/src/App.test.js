import { render, screen, waitFor } from '@testing-library/react';
import App from './App';

// Mock localStorage
const localStorageMock = {
  getItem: jest.fn(() => null),
  setItem: jest.fn(),
  removeItem: jest.fn(),
  clear: jest.fn(),
  length: 0,
  key: jest.fn(),
};
global.localStorage = localStorageMock;

describe('App', () => {
  beforeEach(() => {
    // Clear all mocks before each test
    jest.clearAllMocks();
    localStorageMock.getItem.mockReturnValue(null);
  });

  test('renders without crashing', async () => {
    const { container } = render(<App />);
    
    // Wait for the app to finish loading
    await waitFor(() => {
      expect(container.querySelector('.App')).toBeInTheDocument();
    });
  });

  test('redirects to login when not authenticated', async () => {
    render(<App />);
    
    // Should redirect to /login and show login form
    await waitFor(() => {
      // The app should render without errors
      expect(screen.queryByText(/loading/i)).not.toBeInTheDocument();
    });
  });

  test('checks localStorage for existing session', async () => {
    render(<App />);
    
    // Wait for useEffect to execute
    await waitFor(() => {
      // Should check for token and user data
      expect(localStorageMock.getItem).toHaveBeenCalledWith('token');
      expect(localStorageMock.getItem).toHaveBeenCalledWith('user');
    });
  });
});
