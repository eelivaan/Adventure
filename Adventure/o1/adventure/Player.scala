package o1.adventure

/** A `Player` object represents a player character controlled by the real-life user
  * of the program.
  *
  * A player object’s state is mutable: the player’s location and possessions can change,
  * for instance.
  *
  */
class Player(startingRoom: Room, game: Adventure) extends Agent(startingRoom, game):

  override protected def imageFile: String = "Adventure/bat.png"

  private val commandMap = Map("go" -> go, "cheatcode" -> cheat)

  /** The question i.e. riddle that was last asked */
  private var lastQuestion: Option[Riddle] = None

  /**
   * Parse and execute actions based on given user prompt.
   * Returns
   */
  def parseCommand(cmd: String): String =
    val verb = cmd.trim.takeWhile(!_.isWhitespace).toLowerCase
    // execute command
    if this.commandMap.contains(verb) then
      this.lastQuestion = None  // forget about the question if one existed
      val modifier = cmd.drop(verb.length).trim.toLowerCase
      this.commandMap.get(verb).map( func => func(modifier) ).getOrElse("")
    // answer a question
    else if lastQuestion.isDefined then
      this.lastQuestion.fold("")( riddle =>
        riddle.questioner match {
          case Some(corridor: Corridor) =>
            corridor.unlock(cmd)
          case _ =>
            ""
        }
      )
    // invalid command
    else
      s"Invalid command \"$cmd\""

  /**
   * Go into the next room in specified direction if possible
   */
  def go(direction: String): String =
    if this.isMoving then
      "Wait until you stop"
    else
      this.location.corridors.get(Dir.fromString(direction)) match {
        case Some(corridor) =>
          if corridor.blocked then
            this.lastQuestion = corridor.riddle
            corridor.riddle.fold("")(_.question)
          else
            this.moveThrough(corridor) match {
              case Some(room) =>
                room.reveal()
                ""
              case None =>
                "That corridor is blocked!"
            }
        case None =>
          s"You can't go $direction"
      }
  end go

  def cheat(command: String): String =
    var answer = ""
    command match {
      case "revealall" =>
        game.maze.roomsIterator.foreach(_.reveal())
      case "unlock" =>
        this.location.corridors.values.foreach( corridor =>
          corridor.unlock(corridor.riddle.map(_.answer).getOrElse(""))
        )
      case _ =>
    }
    answer

end Player

