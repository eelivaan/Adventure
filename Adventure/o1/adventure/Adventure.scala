package o1.adventure

/**
 * Adventure is the game class holding together everything else
 */
class Adventure:

  /** the name of the game */
  val title = "The Maze"

  /** The game world */
  val maze = new Maze()

  /** The character that the player controls in the game. */
  val player = Player(maze.startingRoom, this)

  /** All agents in the world including the player */
  val agents: Vector[Agent] = player +: maze.roomsIterator.filter(_.spawnEnemy).map(room => new Slaybot(room, this))

  var gameRunning = true


  /** Determines if the adventure is complete, that is, if the player has won. */
  def isComplete = player.location.isFinish

  /** Determines whether the player has won, lost, or quit, thereby ending the game. */
  def isOver = !player.alive

  /** Returns a message that is to be displayed to the player at the beginning of the game. */
  def welcomeMessage = "Your mission is to find your way out of this maze.\n" +
                       "Find clues and items that will help you get through obstacles.\n" +
                       "\n" +
                       "Type \"go left\" to get started.\n"

  def victoryMessage = "You made it! Now you're free!"

  def lostMessage = "GAME OVER! You died.\n\nTry \"restart\""

end Adventure

