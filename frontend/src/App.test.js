import { render, screen } from '@testing-library/react';
import App from './App';

// Mock localStorage
const localStorageMock = {
  getItem: jest.fn(),
  setItem: jest.fn(),
  removeItem: jest.fn(),
  clear: jest.fn(),
};
global.localStorage = localStorageMock;

test('renders without crashing', () => {
  render(<App />);
  // Basic smoke test - just verify the app renders
  expect(document.body).toBeInTheDocument();
});
