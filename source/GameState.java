/*
* GameState
* Stores the core state of the game, including:
* - Player lives
* - Current score
* - High score
* - Timestamp of the last damage taken (used for damage cooldowns)
* */
public class GameState {
    public int life = 3;
    public int score = 0;
    public int highScore = 0;
    public long timeSinceLastTookDamage = 0;
}
