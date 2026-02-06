# Contributing to Issue Tracker

Thank you for considering contributing to Issue Tracker! This document provides guidelines and instructions for contributing.

## Code of Conduct

- Be respectful and inclusive
- Welcome newcomers and help them learn
- Focus on constructive feedback
- Keep discussions professional

## How to Contribute

### Reporting Bugs

Before creating a bug report:
1. Check if the issue already exists
2. Ensure you're using the latest version
3. Verify it's not a configuration issue

When creating a bug report, include:
- Clear title and description
- Steps to reproduce
- Expected vs actual behavior
- Environment details (OS, Java version, etc.)
- Relevant logs or screenshots

### Suggesting Features

When suggesting features:
1. Check if the feature is already planned
2. Provide clear use cases
3. Explain the expected behavior
4. Consider the impact on existing features

### Pull Requests

1. **Fork the repository**
```bash
git clone https://github.com/your-username/issue-tracker.git
cd issue-tracker
```

2. **Create a feature branch**
```bash
git checkout -b feature/your-feature-name
```

3. **Make your changes**
   - Follow the code style guidelines
   - Write clear commit messages
   - Add tests for new features
   - Update documentation as needed

4. **Run tests**
```bash
# Backend tests
mvn test

# Frontend tests
cd frontend && npm test
```

5. **Commit your changes**
```bash
git add .
git commit -m "Add feature: description of your feature"
```

6. **Push to your fork**
```bash
git push origin feature/your-feature-name
```

7. **Create a Pull Request**
   - Provide a clear title and description
   - Reference any related issues
   - Ensure CI checks pass
   - Respond to review feedback

## Development Setup

### Prerequisites
- Java 17+
- Node.js 18+
- Maven 3.9+
- Docker (optional but recommended)
- PostgreSQL 15 (or use Docker)

### Backend Development

```bash
# Install dependencies
mvn clean install

# Run application
mvn spring-boot:run

# Run tests
mvn test

# Build
mvn clean package
```

### Frontend Development

```bash
cd frontend

# Install dependencies
npm install

# Start dev server
npm start

# Run tests
npm test

# Build
npm run build
```

## Code Style Guidelines

### Java (Backend)

- Follow Java naming conventions
- Use meaningful variable and method names
- Keep methods focused and small (< 50 lines)
- Add JavaDoc for public APIs
- Use Lombok to reduce boilerplate
- Follow Spring Boot best practices

Example:
```java
@Service
@RequiredArgsConstructor
public class IssueService {
    private final IssueRepository issueRepository;
    
    /**
     * Creates a new issue and returns the saved entity.
     * 
     * @param request the issue request DTO
     * @return the created issue response
     * @throws RuntimeException if issue creation fails
     */
    @Transactional
    public IssueResponse createIssue(IssueRequest request) {
        // Implementation
    }
}
```

### JavaScript/React (Frontend)

- Use functional components with hooks
- Follow React best practices
- Keep components small and focused
- Use descriptive component and variable names
- Add PropTypes or TypeScript for type safety (future)

Example:
```javascript
function IssueList() {
  const [issues, setIssues] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchIssues();
  }, []);

  const fetchIssues = async () => {
    // Implementation
  };

  return (
    // JSX
  );
}
```

### Git Commit Messages

Follow the conventional commits format:

```
<type>(<scope>): <subject>

<body>

<footer>
```

Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Build process or auxiliary tool changes

Examples:
```
feat(api): add endpoint for bulk issue updates

Implemented new endpoint POST /api/issues/bulk to allow
updating multiple issues at once.

Closes #123
```

```
fix(auth): resolve JWT token expiration issue

Fixed a bug where JWT tokens were expiring prematurely
due to incorrect timestamp calculation.

Fixes #456
```

## Testing Guidelines

### Backend Tests

- Write unit tests for services and utilities
- Write integration tests for controllers
- Use meaningful test names
- Follow AAA pattern (Arrange, Act, Assert)
- Mock external dependencies

Example:
```java
@Test
void testCreateIssue_Success() {
    // Arrange
    IssueRequest request = new IssueRequest();
    request.setTitle("Test Issue");
    
    // Act
    IssueResponse response = issueService.createIssue(request);
    
    // Assert
    assertNotNull(response);
    assertEquals("Test Issue", response.getTitle());
}
```

### Frontend Tests

- Test components in isolation
- Test user interactions
- Test edge cases and error states
- Use React Testing Library

Example:
```javascript
test('renders issue list', async () => {
  render(<IssueList />);
  
  await waitFor(() => {
    expect(screen.getByText('Test Issue')).toBeInTheDocument();
  });
});
```

## Documentation

- Update README.md for major changes
- Add JSDoc/JavaDoc for public APIs
- Update API documentation for endpoint changes
- Include examples for new features
- Update DEPLOYMENT.md for infrastructure changes

## Review Process

Pull requests are reviewed for:
1. Code quality and style
2. Test coverage
3. Documentation
4. Performance impact
5. Security considerations
6. Breaking changes

Reviewers may request changes. Please:
- Respond to feedback promptly
- Make requested changes
- Ask questions if unclear
- Be open to suggestions

## Release Process

1. Version bump follows semantic versioning (MAJOR.MINOR.PATCH)
2. Update CHANGELOG.md
3. Tag release in git
4. Build and push Docker images
5. Deploy to production

## Getting Help

- Open an issue for questions
- Join our community discussions
- Check existing documentation
- Review closed issues and PRs

## Recognition

Contributors will be added to:
- CONTRIBUTORS.md file
- Release notes
- Project acknowledgments

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

Thank you for contributing to Issue Tracker! Your help makes this project better for everyone.
