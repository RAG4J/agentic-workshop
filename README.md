# Thymeleaf Agent

A multi-module Spring Boot application that demonstrates various AI agent implementations with a web interface. The project showcases different approaches to building conversational agents, from plain Java implementations using OpenAI directly to Spring AI-based implementations and Embabel agent platform integration.

## Project Overview

The project focuses on conference talk management as a domain example, allowing users to interact with agents to search and retrieve information about conference talks and speakers through a web-based chat interface.

## Architecture

### Module Structure

The project follows a clean architecture pattern with multiple agent implementations:

- **`core-agent`**: Contains core interfaces and data models (`Agent`, `Conversation`, `Message`, `Sender`)
- **`java-agent`**: Plain Java implementation with ReAct-style reasoning, memory, and tool execution capabilities
- **`springai-agent`**: Spring AI-based implementation with built-in LLM integration and function calling
- **`embabel-agent`**: Embabel platform integration for advanced agent capabilities
- **`web-app`**: Spring Boot web application with Thymeleaf templates providing the user interface

### Key Features

- **Multiple Agent Implementations**: Switch between different agent backends without changing the web interface
- **Spring Profiles**: Easy configuration switching between agent implementations
- **Tool System**: Function calling capabilities for searching conference talks
- **Memory Management**: Conversation history and context management
- **Web Interface**: Clean, responsive chat interface using Thymeleaf templates
- **Multi-Agent Support**: Router agents that can delegate to specialized agents

## Getting Started

### Prerequisites

- Java 21 or later
- Maven 3.6 or later
- OpenAI API access (or compatible proxy)

### Building the Project

```bash
# Build all modules
./mvnw clean compile

# Run tests for all modules
./mvnw test

# Package the application
./mvnw clean package

# Install all modules (recommended for first setup)
./mvnw clean install
```

### Running the Application

#### Default Configuration (Spring AI)

```bash
./mvnw spring-boot:run -pl web-app
```

The application will start on `http://localhost:8080` using the Spring AI agent implementation.

#### Using Different Agent Implementations

You can switch between different agent implementations using Spring profiles:

##### Plain Java Agent (ReAct Pattern)
```bash
SPRING_PROFILES_ACTIVE=plain ./mvnw spring-boot:run -pl web-app
```

##### Spring AI Agent (Default)
```bash
SPRING_PROFILES_ACTIVE=springai ./mvnw spring-boot:run -pl web-app
```

##### Multi-Agent Router (Plain Java)
```bash
SPRING_PROFILES_ACTIVE=plain-multi ./mvnw spring-boot:run -pl web-app
```

##### Multi-Agent Router (Spring AI)
```bash
SPRING_PROFILES_ACTIVE=springai-multi ./mvnw spring-boot:run -pl web-app
```

##### Embabel Agent Platform
```bash
SPRING_PROFILES_ACTIVE=embabel ./mvnw spring-boot:run -pl web-app
```

## Spring Profiles

The application supports several Spring profiles to switch between different agent implementations:

| Profile | Description | Agent Implementation |
|---------|-------------|---------------------|
| `plain` | Plain Java agent with ReAct pattern | `PlainJavaAgent` with OpenAI integration |
| `springai` | Spring AI single agent | `TalksAgent` using Spring AI ChatClient |
| `springai-multi` | Spring AI multi-agent router | `RouterAgent` with specialized sub-agents |
| `plain-multi` | Plain Java multi-agent system | `PlainJavaAgent` with agent-as-tool pattern |
| `embabel` | Embabel platform integration | `EmbabelAgent` with platform capabilities |

### Profile Details

#### Plain Agent (`plain`)
- Uses direct OpenAI API calls
- Implements ReAct (Reasoning + Acting) pattern
- Manual tool execution and conversation memory
- Configurable max reasoning steps

#### Spring AI Agent (`springai`)  
- Uses Spring AI's ChatClient
- Built-in conversation memory management
- Automatic function calling support
- Streamlined configuration

#### Multi-Agent Configurations
- **`springai-multi`**: Router agent that delegates to specialized agents (Conference Talks, Science Fiction)
- **`plain-multi`**: Uses agent-as-tool pattern for multi-agent coordination

#### Embabel Agent (`embabel`)
- Integration with Embabel agent platform
- Advanced agent capabilities and tooling
- Enhanced logging and debugging features

## Configuration Options

### Application Properties

Key configuration options in `application.properties`:

```properties
# Default and active profiles
spring.profiles.active=springai-multi
spring.profiles.default=plain

# Application settings
spring.application.name=conference-agent

# OpenAI Configuration
openai.proxy.url=https://your-openai-proxy.com
openai.proxy.token=your-api-token

# Spring AI OpenAI Settings
spring.ai.openai.api-key=${openai.proxy.token}
spring.ai.openai.model=gpt-4.1-mini
spring.ai.openai.chat-model=gpt-4.1-mini
spring.ai.openai.embedding-model=text-embedding-3-small

# Embabel Configuration
embabel.models.default-llm=gpt-4.1-mini
embabel.agent-platform.ranking.llm=gpt-4.1-mini

# Agent-specific settings
agent.plain.reasoning.max-steps=5

# Logging
logging.level.root=INFO
```

### Environment Variables

You can override configuration using environment variables:

```bash
export OPENAI_PROXY_URL="https://your-proxy-url"
export OPENAI_PROXY_TOKEN="your-token"
export SPRING_PROFILES_ACTIVE="springai"
```

### OpenAI API Configuration

The application requires OpenAI API access. You can use:

1. **Direct OpenAI API**: Set `openai.proxy.url=https://api.openai.com` and provide your OpenAI API key
2. **Custom Proxy**: Use your own proxy service (as shown in the example configuration)
3. **Local Models**: Configure compatible endpoints for local model serving

## Web Interface

The application provides a clean web interface with the following pages:

- **Home** (`/`): Landing page with navigation
- **Chat** (`/chat`): Main chat interface for interacting with agents
- **Token** (`/token`): Token management and configuration
- **Error** (`/error`): Custom error handling

### Chat Interface Features

- Real-time conversation with AI agents
- Message history preservation
- Support for different agent personalities based on active profile
- Responsive design for mobile and desktop

## Development

### Running Tests

```bash
# Run all tests
./mvnw test

# Run tests for specific module
./mvnw test -pl core-agent
./mvnw test -pl web-app
./mvnw test -pl java-agent
./mvnw test -pl springai-agent
./mvnw test -pl embabel-agent

# Run with specific profile
SPRING_PROFILES_ACTIVE=plain ./mvnw test -pl web-app
```

### Development Commands

```bash
# Install dependencies without running tests
./mvnw clean compile -DskipTests

# Run specific test class
./mvnw test -Dtest=PlainJavaAgentTest -pl java-agent

# Clean and reinstall all dependencies
./mvnw clean install

# Skip tests and install
./mvnw clean install -DskipTests
```

### Adding New Agent Implementations

1. Create a new module following the existing pattern
2. Implement the `Agent` interface from `core-agent`
3. Add Spring configuration with appropriate `@Profile` annotation
4. Add the module as a dependency to `web-app`
5. Configure any required properties in `application.properties`

### Adding New Tools

1. Implement `AgenticTool` interface (for plain agents) or create Spring AI function beans
2. Define tool schema and execution logic
3. Register tools in the appropriate configuration class
4. Tools are automatically discovered by the agent implementations

## Technology Stack

- **Java 21**: Modern Java features and performance
- **Spring Boot 3.5.4**: Application framework and dependency injection
- **Spring AI**: AI integration and model abstractions  
- **Thymeleaf**: Server-side templating for web interface
- **OpenAI Java SDK**: Direct OpenAI API integration
- **Maven**: Build and dependency management
- **JUnit 5 & Mockito**: Testing framework
- **Embabel Agent Platform**: Advanced agent capabilities (optional)

## Logging and Debugging

The application provides detailed logging for agent reasoning steps and tool executions:

```properties
# Enable debug logging for agents
logging.level.org.rag4j.agent=DEBUG

# Enable Spring AI debug logging  
logging.level.org.springframework.ai=DEBUG

# Enable web request logging
logging.level.org.springframework.web=DEBUG
```

## Future Enhancements

Some planned improvements include:

- **Observability**: Add comprehensive monitoring and tracing
- **State Management**: User preferences and conversation state persistence
- **Enhanced Tools**: Additional search capabilities (e.g., by difficulty level)
- **Favorites System**: User-specific bookmarking of talks and speakers
- **Tool Output Integration**: Include tool results in conversation context
- **Loop Prevention**: Improved reasoning cycle management

## Contributing

1. Fork the repository
2. Create a feature branch
3. Follow existing code patterns and architecture
4. Add tests for new functionality
5. Update documentation as needed
6. Submit a pull request

## License

This project is licensed under the terms specified in the project configuration.

## Support

For questions or issues:
1. Check the existing documentation
2. Review test cases for usage examples
3. Examine configuration classes for setup patterns
4. Create an issue with detailed reproduction steps
