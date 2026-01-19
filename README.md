# Description
This is an arcade style shoot 'em up game written in Java using JavaFX. 

## Additional Library Needed to Run This Program
JavaFX: https://openjfx.io/

## Controls
Movement: left/right arrow keys <br> Fire: spacebar

## Power Ups
Salt: increases fire rate <br> Pepper: spawns extra pizza slices which shoot alongside the player's sprite

## Classes
- BackgroundManager: handles infinite scrolling background
- CollisionManager: handles interactions between player sprite, enemy sprites, projectiles, & power ups. It also updates the score and life bar
- CollisionUtils: handles collision detection
- Enemy: handles enemy sprite visuals, movement, spawning, and firing
- EnemyProjectile: handles animation and visual of enemy projectiles
- GameManager: controls overall game logic, keeping track of player and every object on screen and handles updating everything each frame
- GameState: keeps track of player life, score, and timing information
- LifeIcon: displays life icons
- Pepper: handles pepper power up visual, spawn location, and traversal pattern
- PizzaSprite: handles player sprite visual and flash animation on interactions
- Projectile: handles projectile visual and fire() method which displays an animated projectile
- Salt: handles salt power up visual, spawn location, and traversal pattern
- SoundManager: handles various audio clips used in different interactions
- UIManager: handles UI elements such as score, instructions, logo, life, and game over screen
- ExtraLife: Represents an extra life power-up. Spawns with a zigzag path and disappears when collected or off-screen
- Launcher: Entry point for launching the JavaFX application. Delegates directly to Main.main(args) to start the game
- Main: Entry point for the game. Sets up the stage, scene, game/UI layers, font, input handling, and initializes BackgroundManager, UIManager, and GameManager. Manages game start and restart logic

## Screenshot

<img width="754" height="1017" alt="Image" src="https://github.com/user-attachments/assets/0ac72475-05c2-4035-865f-ccfcd4198ad8" />

