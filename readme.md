# ChatGPT Clojure

A Clojure-based chatbot that integrates with different AI APIs, including OpenAI and Anthropic.

## Prerequisites

- Clojure (1.10.1 or higher)
- Leiningen

## Installation

1. Clone the repository:

   ```
   git clone https://github.com/your-username/av-clj-llm.git
   ```

2. Navigate to the project directory:

   ```
   cd av-clj-llm
   ```

3. Install the dependencies:

   ```
   lein deps
   ```

## Configuration

1. Create a `config.edn` file in the `resources` directory:

   ```
   mkdir -p resources
   touch resources/config.edn
   ```

2. Open `resources/config.edn` and provide your API keys and configurations:

   ```edn
   {:openai {:api-key "your_openai_api_key"
             :model-engine "gpt-3.5-turbo"
             :max-tokens 2048
             :temperature 0.6
             :top-p 1.0}
    :anthropic {:api-key "your_anthropic_api_key"
                :model-engine "claude-v1"
                :max-tokens 2048
                :temperature 0.6
                :top-p 1.0}}
   ```

   Replace `"your_openai_api_key"` and `"your_anthropic_api_key"` with your actual API keys.

## Usage

To start the chatbot, run the following command from the project directory:

```
lein run openai
```

or

```
lein run anthropic
```

This will start the chat loop using the specified API.

Once the chatbot is running, you can interact with it by typing your messages at the `User:` prompt. The chatbot will respond with its generated responses.

To exit the chatbot, type `quit` at the `User:` prompt.

## Testing

To run the unit tests, use the following command:

```
lein test
```

This will run all the tests defined in the `test` directory.

## Project Structure

The project is structured as follows:

```
av-clj-llm/
├── project.clj
├── resources/
│   └── config.edn
├── src/
│   └── chatgpt_clojure/
│       ├── api/
│       │   ├── anthropic.clj
│       │   ├── core.clj
│       │   └── openai.clj
│       ├── main.clj
│       └── utils.clj
└── test/
    └── chatgpt_clojure/
        ├── api/
        │   ├── anthropic_test.clj
        │   ├── core_test.clj
        │   └── openai_test.clj
        └── utils_test.clj
```

- `project.clj`: The Leiningen project configuration file.
- `resources/config.edn`: The configuration file for API keys and settings.
- `src/chatgpt_clojure/api/core.clj`: Defines the `ChatAPI` protocol.
- `src/chatgpt_clojure/api/openai.clj`: Implements the OpenAI chat API.
- `src/chatgpt_clojure/api/anthropic.clj`: Implements the Anthropic chat API.
- `src/chatgpt_clojure/main.clj`: The main entry point of the application.
- `src/chatgpt_clojure/utils.clj`: Utility functions used across the project.
- `test/chatgpt_clojure/api/core_test.clj`: Unit tests for the `ChatAPI` protocol.
- `test/chatgpt_clojure/api/openai_test.clj`: Unit tests for the OpenAI chat API.
- `test/chatgpt_clojure/api/anthropic_test.clj`: Unit tests for the Anthropic chat API.
- `test/chatgpt_clojure/utils_test.clj`: Unit tests for the utility functions.

## License

This project is licensed under the [Apache-2.0 license](LICENSE).
