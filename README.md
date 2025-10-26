# Maven All-In-One Checker

A JavaFX application for email validation and subscription checking.

## Features

### Email Cleaning Module
- Import email lists from text/CSV files
- Remove duplicates and invalid emails
- Filter role-based, admin, bot, and disposable emails
- Validate email format and domain existence
- Export cleaned email lists

### Checker Module (NEW)
- **SubscriptionCheckerInterface**: Interface for implementing email subscription checkers
- **SeleniumSubscriptionChecker**: Selenium-based implementation for web-based email verification
- **CheckerService**: Service layer for managing email checking operations
- Automated browser-based email validation
- Support for batch email checking
- Result tracking with valid/invalid status

## Architecture

### Services Layer

#### EmailCleaningService
Handles email list cleaning and validation operations:
- Format validation
- Domain validation
- Duplicate removal
- Role-based filtering

#### CheckerService
Manages email subscription checking:
- Single email verification
- Batch email processing
- Integration with SubscriptionCheckerInterface implementations

#### SubscriptionCheckerInterface
Defines the contract for email subscription checkers:
```java
public interface SubscriptionCheckerInterface {
    boolean checkSubscription(String email);
    List<Boolean> checkSubscriptions(List<String> emails);
    void initialize();
    void cleanup();
}
```

### Implementations

#### SeleniumSubscriptionChecker
Uses Selenium WebDriver to interact with web-based email verification services:
- Headless browser support
- Automatic WebDriver management
- Configurable checker URL
- Thread-safe operations

## Requirements

- Java 17 or higher
- Maven 3.6+
- Chrome browser (for Selenium checker)

## Building

```bash
mvn clean compile
```

## Testing

Run all tests:
```bash
mvn test
```

Run specific test classes:
```bash
mvn test -Dtest=CheckerServiceImplTest
mvn test -Dtest=CheckerServiceIntegrationTest
```

### Test Coverage

- **Unit Tests**: Test individual components in isolation using mocks
  - `CheckerServiceImplTest`: Tests the CheckerService implementation
  - `CheckerResultTest`: Tests the CheckerResult data model

- **Integration Tests**: Test component interactions
  - `CheckerServiceIntegrationTest`: Tests the full checker service workflow
  - `SeleniumSubscriptionCheckerIntegrationTest`: Tests Selenium-based checking (disabled in CI/CD)

**Note**: Selenium integration tests that require browser drivers are disabled in CI/CD environments and can be enabled for local testing.

## Running the Application

```bash
mvn javafx:run
```

## Usage

### Using the Checker Module Programmatically

```java
// Create a checker service
CheckerService checkerService = new CheckerServiceImpl();

// Initialize the service
checkerService.initialize();

// Check a single email
CheckerResult result = checkerService.checkEmail("test@example.com");
System.out.println("Email: " + result.getEmail());
System.out.println("Valid: " + result.isValid());
System.out.println("Status: " + result.getStatus());

// Check multiple emails
List<String> emails = Arrays.asList(
    "user1@example.com",
    "user2@example.com"
);
List<CheckerResult> results = checkerService.checkEmails(emails);

// Get only valid emails
List<String> validEmails = CheckerResult.getValidEmails(results);

// Clean up
checkerService.cleanup();
```

### Custom Checker Implementation

You can implement your own checker by implementing `SubscriptionCheckerInterface`:

```java
public class CustomChecker implements SubscriptionCheckerInterface {
    @Override
    public boolean checkSubscription(String email) {
        // Your custom logic here
        return true;
    }
    
    // Implement other required methods...
}

// Use it with CheckerService
CheckerService service = new CheckerServiceImpl(new CustomChecker());
```

## Dependencies

- JavaFX 21.0.2 (Controls, FXML)
- Selenium WebDriver 4.15.0
- WebDriverManager 5.6.2
- JUnit 5.10.1 (Testing)
- Mockito 5.7.0 (Testing)

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   ├── application/
│   │   │   └── Main.java
│   │   ├── controllers/
│   │   │   ├── CheckerPageController.java
│   │   │   ├── EmailPageController.java
│   │   │   └── ...
│   │   └── services/
│   │       ├── SubscriptionCheckerInterface.java
│   │       ├── SeleniumSubscriptionChecker.java
│   │       ├── CheckerService.java
│   │       ├── CheckerServiceImpl.java
│   │       ├── CheckerResult.java
│   │       ├── EmailCleaningService.java
│   │       └── EmailCleaningServiceImpl.java
│   └── resources/
│       ├── views/
│       └── Images/
└── test/
    └── java/
        └── services/
            ├── CheckerServiceImplTest.java
            ├── CheckerResultTest.java
            ├── CheckerServiceIntegrationTest.java
            └── SeleniumSubscriptionCheckerIntegrationTest.java
```

## License

This project is open source and available under the MIT License.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
