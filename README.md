# Thymeleaf Agent - Multimodule Project

This project has been restructured as a Maven multimodule project with the following structure:

## Modules

### 1. `java-agent` Module
- **Package**: `org.rag4j.agent`
- **Description**: Contains the core agent functionality including reasoning and memory components
- **Main Components**:
  - `Agent.java` - Main agent logic
  - `Conversation.java` - Conversation data model
  - `Sender.java` - Enum for message senders
  - `memory/` package - Memory management (WindowedConversationMemory, Memory interface)
  - `reasoning/` package - AI reasoning logic (OpenAIReasoning, Reasoning interface, SystemPrompt)

### 2. `web-app` Module
- **Package**: `org.rag4j.webapp`
- **Description**: Spring Boot web application providing the user interface
- **Main Components**:
  - `ThymeleafAgentApplication.java` - Spring Boot main class
  - `ChatController.java` - Handles chat interactions
  - `HomeController.java` - Home page controller
  - `TokenController.java` - Token management controller
  - `GlobalExceptionHandler.java` - Global exception handling
  - `resources/` - Contains Thymeleaf templates and application properties

### 3. Parent POM
- **Artifact ID**: `thymeleaf-agent-parent`
- **Description**: Parent POM managing dependencies and build configuration for all modules
- **Features**:
  - Dependency management for common libraries
  - Plugin management for consistent build configuration
  - Version management for internal and external dependencies

## Dependencies

### Inter-module Dependencies
- `web-app` depends on `java-agent` for agent functionality

### External Dependencies
- Spring Boot (Web, Thymeleaf, Validation, Test)
- OpenAI Java Client
- Mockito for testing

## Building the Project

```bash
# Build all modules
mvn clean compile

# Run tests
mvn test

# Package all modules
mvn package

# Run the web application
java -jar web-app/target/web-app-0.0.1-SNAPSHOT.jar
# or
cd web-app && mvn spring-boot:run
```

## Development

### Adding New Features
- **Agent-related features**: Add to `java-agent` module
- **Web-related features**: Add to `web-app` module
- **Shared dependencies**: Add to parent POM's `dependencyManagement` section

### Testing
- Each module has its own test suite
- Tests run automatically for all modules with `mvn test`
- Integration tests in `web-app` module test the full application stack

## Module Structure

```
thymeleaf-agent/
├── pom.xml (parent)
├── java-agent/
│   ├── pom.xml
│   └── src/
│       ├── main/java/org/rag4j/agent/
│       └── test/java/org/rag4j/agent/
└── web-app/
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/org/rag4j/webapp/
        │   └── resources/
        └── test/java/org/rag4j/webapp/
```

This modular structure provides better separation of concerns, making the codebase more maintainable and allowing for independent development of different application layers.
