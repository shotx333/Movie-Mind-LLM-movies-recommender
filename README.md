# Movie Mind - AI-Powered Movie Recommendations

Movie Mind is an AI-powered movie recommendation system that uses advanced large language models to suggest movies based on user descriptions. The system leverages multiple AI providers (Claude, ChatGPT, and Mistral AI) to generate personalized recommendations.

## Features

- **Multi-Provider AI Integration**: Seamlessly integrates with three leading AI models:
  - Claude (Anthropic)
  - ChatGPT (OpenAI)
  - Mistral AI
- **Natural Language Input**: Describe the movie you're looking for in plain language
- **Detailed Movie Recommendations**: Each recommendation includes:
  - Title and release year
  - Director and genre
  - Plot summary
  - Match score with reasoning
  - IMDB rating and direct link
- **User-Friendly Interface**: Clean, responsive design with suggestion examples
- **Persistent Results**: Recommendations are saved locally and restored between sessions

## Architecture

The application follows a modern client-server architecture:

- **Backend**: Spring Boot REST API that communicates with AI providers
- **Frontend**: Angular-based single-page application
- **API Integration**: Spring AI for standardized communication with LLM providers

## Tech Stack

### Backend
- **Framework**: Spring Boot 3.4.3
- **Language**: Java 17
- **AI Integration**: Spring AI 1.0.0-M6
- **Dependencies**: 
  - Spring Web
  - Spring Validation
  - Spring AI (Anthropic, OpenAI, MistralAI starters)
  - Lombok

### Frontend
- **Framework**: Angular 19.1.0
- **Language**: TypeScript 5.7.2
- **Styling**: SCSS with custom components
- **Dependencies**:
  - RxJS for reactive programming
  - Angular Forms for input validation

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Node.js 16.x or higher
- npm 8.x or higher
- API keys for at least one of the supported AI providers:
  - Anthropic (Claude)
  - OpenAI (ChatGPT)
  - Mistral AI

### Backend Setup

1. Clone the repository
2. Navigate to the Backend directory
3. Set up the required environment variables:
   ```
   ANTHROPIC_API_KEY=your_api_key
   OPENAI_API_KEY=your_api_key
   MISTRAL_API_KEY=your_api_key
   ```
   (At least one API key is required)
4. Build and run the application:
   ```bash
   ./gradlew bootRun
   ```
   The backend will start on port 8080.

### Frontend Setup

1. Navigate to the Frontend directory
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm start
   ```
   The frontend will be available at http://localhost:4200

## Usage

1. Open your browser and navigate to http://localhost:4200
2. Enter a description of the movie you're looking for in the text area
3. Optionally, adjust the number of results and select your preferred AI provider
4. Click "Find Movies" to get recommendations
5. Browse the recommended movies, which include plot summaries, match scores, and IMDB links
6. Results are automatically saved locally and will persist between sessions

## API Endpoints

The backend exposes the following REST endpoints:

- **POST /api/movies/recommend**
  - Retrieves movie recommendations based on description
  - Request body:
    ```json
    {
      "description": "A psychological thriller with unreliable narrator",
      "maxResults": 3,
      "provider": "anthropic"
    }
    ```

- **GET /api/movies/providers**
  - Returns a list of available AI providers
  - Response:
    ```json
    [
      {"id": "anthropic", "name": "Claude"},
      {"id": "openai", "name": "ChatGPT"},
      {"id": "mistralai", "name": "Mistral AI"}
    ]
    ```

## Configuration

The application can be configured through the following properties:

### Backend (application.properties)

```properties
# Server Configuration
server.port=8080

# Anthropic Configuration
spring.ai.anthropic.api-key=${ANTHROPIC_API_KEY:}
spring.ai.anthropic.model=claude-3-haiku-20240307
spring.ai.anthropic.options.temperature=0.7
spring.ai.anthropic.options.max-tokens=5000

# OpenAI Configuration
spring.ai.openai.api-key=${OPENAI_API_KEY:}
spring.ai.openai.model=gpt-4o
spring.ai.openai.options.temperature=0.7
spring.ai.openai.options.max-tokens=2000

# Mistral AI Configuration
spring.ai.mistralai.api-key=${MISTRAL_API_KEY:}
spring.ai.mistralai.model=mistral-large-latest
spring.ai.mistralai.options.temperature=0.7
spring.ai.mistralai.options.max-tokens=2000

# Default LLM provider
app.llm.default-provider=anthropic
```

## Future Improvements

- Add movie poster images to recommendations
- Implement user accounts for saved preferences and history
- Add filters for genres, eras, and languages
- Expand to include TV show recommendations
- Add streaming service availability information

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgements

- Spring AI for simplifying AI integration
- Angular team for the frontend framework
- Anthropic, OpenAI, and Mistral AI for their powerful language models
