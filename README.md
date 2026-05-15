# LLM Assistant Application

This application provides a simple command-line interface to interact with AI language models, supporting both OpenAI and Google Gemini models.

## Features

- Support for both OpenAI and Google Gemini AI models
- Extensible design with AIAssistant interface for easy addition of new AI providers
- Factory pattern for creating the appropriate assistant based on configuration
- Configurable model selection with efficient properties caching
- Robust error handling with retry mechanism
- Structured logging with configurable log levels

## Setup

1. Clone the repository
2. Create a `config.properties` file in one of these locations:
   - In the project root directory (recommended for development)
   - In the `src/main/resources` directory (for packaged applications)
   - In your user's working directory
3. Configure your API keys in the `config.properties` file:
   - For OpenAI: Get an API key from [OpenAI Platform](https://platform.openai.com/)
   - For Gemini: Get an API key from [Google AI Studio](https://ai.google.dev/)
4. Build the project using Gradle:
   ```
   ./gradlew build
   ```
5. Run the application:
   ```
   ./gradlew run
   ```

## Configuration

The `config.properties` file may contain the following settings:

- `OPENAI_API_KEY=...`
- `GEMINI_API_KEY=...`
- `AI_LLM=...` <span style="color: gray;">// Possible values: OPENAI, GEMINI; defaults to OPENAI</span>
- `LOG_LEVEL=...` <span style="color: gray;">// Possible values: OFF, ERROR, WARN, INFO, DEBUG, TRACE; defaults to OFF</span>