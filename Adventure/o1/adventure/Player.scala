package o1.adventure

/** A `Player` object represents a player character controlled by the real-life user
  * of the program.
  *
  * A player object’s state is mutable: the player’s location and possessions can change,
  * for instance.
  *
  */
class Player(startingRoom: Room) extends Agent(startingRoom):

  override protected def imageFile: String = "Adventure/bat.png"

  private val commandMap = Map("go" -> go, "open" -> unlockRoom)

  /**  */
  private val lastQuestion: Option[String] = None

  /**
   * Parse and execute actions based on given user prompt.
   * Returns
   */
  def parseCommand(cmd: String): String =
    val verb = cmd.trim.takeWhile(!_.isWhitespace).toLowerCase
    val modifier = cmd.drop(verb.length).trim.toLowerCase
    this.commandMap.get(verb).map( func => func(modifier) ).getOrElse("Invalid command")

  /**
   * Go into the next room in specified direction if possible
   */
  def go(direction: String): String =
    if !this.isMoving then
      this.location.corridors.get(Dir.fromString(direction)) match {
        case Some(corridor) =>
          this.moveThrough(corridor) match {
            case Some(room) =>
              room.reveal()
              ""
            case None =>
              "That corridor is blocked!"
          }
        case None =>
          s"Can't go $direction"
      }
    else
      "Wait until you stop"
  end go

  def unlockRoom(str: String): String =
    this.location.corridors.values.foreach(_.blocked = false)
    ""

end Player

