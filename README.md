# Pacman Game

Welcome to Pacman! This is a simple implementation of the classic arcade game Pacman using Jetpack Compose, Kotlin and Android Framework.

## GamePlay
![Pacman Gameplay](https://github.com/coditive/Pacman/blob/8fa3ed94661609a9258d091758a893908046787e/upload/pacman.gif)

## Architecture

![Dependency Diagram](https://github.com/coditive/Pacman/blob/a4ebd6cfb0add5398a55cf57e921c0089ef07c5f/upload/dependecy-diag.png)

### Overview

The Pacman game is built using the Model-View-ViewModel (MVVM) architecture pattern along with Flow to handle state changes and deliver them to Compose screens.
This architectural approach provides a clear separation of concerns, allowing for better organization and maintainability of the codebase.


### Components

1. **Game Engine**: Responsible for managing the game loop, updating the game state, and handling user input.

2. **Map Generator**: Generates the game map dynamically, ensuring that each level provides a unique gameplay experience.

3. **Pacman**: Controls the movement and behavior of the Pacman character. Handles collision detection with walls and pellets.

4. **Ghosts**: Controls the behavior of the ghost enemies. Includes different AI strategies for chasing and evading Pacman.

5. **Scoreboard**: Tracks the player's score, lives remaining, and other game statistics.

6. **User Interface**: Renders the game graphics and provides user controls for starting, pausing, and restarting the game.

### Libraries Used

- Jetpack Compose
- Timber


