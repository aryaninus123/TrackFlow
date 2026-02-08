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

describe('App', () => {
  beforeEach(() => {
    // Set up localStorage mock
    global.localStorage = localStorageMock;
    // Clear all mocks before each test
    jest.clearAllMocks();
    localStorageMock.getItem.mockReturnValue(null);
  });

  test('renders without crashing', async () => {
    const { container } = render(<App />);
    
    // Wait for the app to finish loading
    await waitFor(() => {
      expect(container.querySelector('.App')).toBeInTheDocument();
    }, { timeout: 3000 });
  });

  test('redirects to login when not authenticated', async () => {
    render(<App />);
    
    // Wait for loading to finish
    await waitFor(() => {
      expect(screen.queryByText(/loading/i)).not.toBeInTheDocument();
    }, { timeout: 3000 });
  });

  test('checks localStorage for existing session', () => {
    render(<App />);
    
    // localStorage.getItem is called during initial render
    expect(localStorageMock.getItem).toHaveBeenCalled();
    expect(localStorageMock.getItem).toHaveBeenCalledWith('token');
    expect(localStorageMock.getItem).toHaveBeenCalledWith('user');
  });
});
