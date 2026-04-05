# Kotlin AI Chess
Это обычные шахматы которые были написаны целиком ии
## Project Structure
тут просто да.
The project is structured into multiple modules to promote a clean architecture and separation of concerns:

-   **`core`**: Contains the core game logic, including chess rules, board state management, and AI algorithms. This module is platform-agnostic.
    -   `domain`: Defines the core business entities, use cases, and interfaces.
    -   `data`: Handles data sources and repositories for the core logic.
-   **`desktop`**: Implements the desktop-specific presentation layer and any desktop-specific data handling (e.g., UI rendering, input).
    -   `presentation`: Manages the user interface and interaction logic.
    -   `data`: Handles local data storage or platform-specific data sources.

## Заметка
К сожалению из-за ограничений гитхаба мне пришлось вырезать модели :(
