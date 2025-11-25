package o1.adventure

/**
 * A `Player` object represents a player character controlled by the real-life user of the program.
 */
class Player(startingRoom: Room, val game: Adventure) extends Agent(startingRoom):

  override protected def imageFile: String = "Adventure/sprites/bat.png"

  override val speed = 90.0

  /** The question i.e. riddle that was last presented */
  private var lastQuestion: Option[Riddle] = None

  // only needed for cheating
  private var answerToLastQuestion = ""

  private val commandMap = Map("go" -> go, "cheatcode" -> cheat, "use" -> useItem)

  /**
   * Parse and execute actions based on given user prompt.
   * Returns some feedback for the user.
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
          case Some(gatekeeper: Gatekeeper) =>
            gatekeeper.tryToAnswer(cmd, this.location)
          case _ =>
            // if the riddle, for some reason, has no questioner then it can be forgotten
            this.lastQuestion = None
            ""
        }
      )

    // invalid command
    else
      s"Unknown command \"$cmd\""
  end parseCommand


  override def onArrival(room: Room): Unit =
    super.onArrival(room)
    // pick the key if one is found
    if room.items.nonEmpty then
      room.pickItem("key").foreach( item => this.possessedItems += item)
  end onArrival


  /**
   * Go into the next room in specified direction if possible
   */
  def go(direction: String): String =
    this.focus = Dir.fromString(direction)
    this.location.corridors.get(this.focus) match {
      case Some(corridor) =>
        if corridor.blocked then
          this.lastQuestion = corridor.riddle
          this.answerToLastQuestion = this.lastQuestion.map(_.answers.head).getOrElse("")
          corridor.message
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


  /**
   * Some cheat codes
   */
  def cheat(command: String): String =
    var answer = ""
    command match {
      case "showall" =>
        game.maze.roomsIterator.foreach( _.reveal() )
      case "unlock" =>
        this.location.corridors.values.foreach( _.unlock() )
      case "answer" =>
        answer = "The answer is \"" + answerToLastQuestion + "\""
      case "killenemies" =>
        game.agents.filter(_.isHostile).foreach( _.kill() )
      case _ =>
    }
    answer
  end cheat


  /**
   * Use an item to do something
   */
  def useItem(itemName: String): String =
    this.inventory.get(itemName) match {
      case Some(key: Key) =>
        this.location.corridors.get(this.focus) match {
          case Some(corridorToUnlock) =>
            corridorToUnlock.unlockWithKey(key) match {
              case Some(failMessage) =>
                failMessage
              case None =>
                // keys can be used only once
                this.possessedItems -= key
                ""
            }
          case _ =>
            "What are you trying to unlock?"
        }

      case _ =>
        s"You don't have a $itemName"
    }
  end useItem

end Player

