package o1.adventure

/**
 * Adventure is the game class holding together everything else
 */
class Adventure:

  val title = "Adventure"

  /** The game world */
  val maze = new Maze()

  /** The character that the player controls in the game. */
  val player = Player(maze.startingRoom, this)

  /** All agents in the world including the player */
  val agents: Vector[Agent] = player +: Vector.from(
      maze.roomsIterator.filter(_.spawnCirclingEnemy).map( room => new Slaybot(room, this) ) ++
      maze.roomsIterator.filter(_.spawnChasingEnemy).map( room => new Chasebot(room, this) ) ++
      maze.corridorsIterator.flatMap(_.gatekeeper)
    )

  var gameRunning = true

  def tick(dt: Double) =
    agents.filter(_.alive).foreach(_.tick(dt))
    maze.roomsIterator.foreach(    _.tick(dt))
    maze.corridorsIterator.foreach(_.tick(dt))
  end tick


  /** Determines if the adventure is complete, that is, if the player has won. */
  def isComplete = player.location.isFinish

  var timeOfCompletion = -1L

  /** Determines whether the player lost thereby ending the game. */
  def isOver = !player.alive

  /** Returns a message that is to be displayed to the player at the beginning of the game. */
  def welcomeMessage = "Your mission is to find your way out of this maze.\n" +
                       "Look for clues and items that will help you through obstacles.\n" +
                       "\n" +
                       "Type \"go left\" to get started.\n"

  def victoryMessage = "You made it! Now you're free!"

  def gameOverMessage = "GAME OVER! You died."

end Adventure

