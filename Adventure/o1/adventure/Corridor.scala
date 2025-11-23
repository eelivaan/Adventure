package o1.adventure

import scala.swing.Graphics2D
import scala.swing.Orientation.{Horizontal, Vertical}

/**
 * A Corridor connects two Rooms and allows the player and other agents to move between them.
 * The Corridor may be blocked and the player has to perform some tasks to open it.
 */
class Corridor(
                val roomA: Room,
                val roomB: Room,
                lockingMethod: Option[Riddle|Gatekeeper.type|Key.type] = None,
                private var hidden: Boolean = true
              ):

  // fit this corridor between the rooms
  val dx = roomB.cx - roomA.cx
  val dy = roomB.cy - roomA.cy
  val orientation =
    if dx.abs > dy.abs then
      val dirFromA = if dx > 0 then Dir.Right else Dir.Left
      roomA.corridors += dirFromA -> this
      roomB.corridors += dirFromA.opposite -> this
      Horizontal
    else
      val dirFromA = if dy > 0 then Dir.Down else Dir.Up
      roomA.corridors += dirFromA -> this
      roomB.corridors += dirFromA.opposite -> this
      Vertical

  val rw = roomA.width/2 + roomB.width/2
  val rh = roomA.height/2 + roomB.height/2
  // coordinates for the center
  val cx = roomA.cx + dx.sign * (roomA.width/2 + (dx.abs - rw) / 2)
  val cy = roomA.cy + dy.sign * (roomA.height/2 + (dy.abs - rh) / 2)

  // width of the corridor (in x or y direction depending on orientation)
  val width = 50
  val length = (if (orientation == Horizontal) then dx.abs - rw else dy.abs - rh) + 6  // small offset to account for antialiasing

  var lockingRiddle: Option[Riddle] = None
  var gatekeeper: Option[Gatekeeper] = None
  var keyNeeded = false

  lockingMethod match {
    case Some(riddle: Riddle) =>
      riddle.questioner = Some(this)
      this.lockingRiddle = Some(riddle)
    case Some(x: Gatekeeper.type) =>
      this.gatekeeper = Some(new Gatekeeper(this))
    case Some(x: Key.type) =>
      this.keyNeeded = true
    case _ =>
  }

  private var isLocked = lockingRiddle.isDefined || gatekeeper.isDefined || keyNeeded

  // door is closed if animationKey = 1.0
  private var doorAnimationKey = if this.lockingRiddle.isDefined || this.keyNeeded then 1.0 else 0.0
  private var doorAnimation = 0.0

  var coverAlpha = if this.hidden then 1.0 else 0.0

  val lockSprite = loadSprite("Adventure/sprites/lock.png")
  val questionSprite = loadSprite("Adventure/sprites/question.png")

  /**
   * Render this corridor into given graphics context
   */
  def render(g: Graphics2D) =
    val w = if orientation == Horizontal then this.length else this.width
    val h = if orientation == Horizontal then this.width else this.length

    g.setColor(cc.floorColor)
    g.fillRect(cx-w/2, cy-h/2, w,h)

    if doorAnimationKey > 0.0 then
      g.setColor(cc.doorColor)
      if orientation == Horizontal then
        g.fillRect(cx-15, cy-h/2, 30,(h * doorAnimationKey).toInt)
      else
        g.fillRect(cx-w/2, cy-15, (w * doorAnimationKey).toInt,30)

    if this.isLocked then
      if this.keyNeeded then
        lockSprite.foreach( g.drawImage(_, cx-10,cy-10, 20,20, null) )
      else if this.lockingRiddle.isDefined then
        questionSprite.foreach( g.drawImage(_, cx-10,cy-10, 20,20, null) )
  end render


  /** Draw black rectangle on top to hide the corridor */
  def renderCover(g: Graphics2D) =
    if this.coverAlpha > 0.0 then
      val w = if orientation == Horizontal then this.length-10 else this.width+5
      val h = if orientation == Horizontal then this.width+5 else this.length-10
      g.setColor(cc.withAlpha(cc.backgroundColor, this.coverAlpha))
      g.fillRect(cx-w/2, cy-h/2, w,h)
  end renderCover


  def tick(dt: Double) =
    // door opening animation
    this.doorAnimationKey = (doorAnimationKey + doorAnimation.sign * 0.4 * dt).min(1.0).max(0.0)
  end tick


  /** Whether this corridor is passable or not */
  def blocked = this.isLocked

  /**
   * Get the riddle needed for unlocking this corridor.
   */
  def riddle: Option[Riddle] =
    this.gatekeeper match {
      case Some(gatekeeper) =>
        Some(gatekeeper.riddle)
      case None =>
        this.lockingRiddle
    }

  def message: String =
    gatekeeper.map(_.message).getOrElse(
      lockingRiddle.map(_.question).getOrElse(
        if keyNeeded then
          "That door seems to be locked.\nYou could try using a key."
        else
          "That corridor is blocked!"))

  /**
   * Returns the room on the other end of this corridor
   */
  def otherRoom(queryRoom: Room) =
    if queryRoom == roomA then roomB else roomA

  /**
   * Reveal this corridor to the player
   */
  def reveal(): Unit =
    this.hidden = false

  /**
   * Try to unlock the corridor should it be blocked.
   */
  def unlock(answer: String): String =
    this.lockingRiddle match {
      case Some(riddle) =>
        if riddle.testAnswer(answer) then
          this.unlock()
          "Correct."
        else
          "Wrong answer."
      case None =>
        ""
    }

  def unlockWithKey(key: Key): Option[String] =
    if this.keyNeeded && this.isLocked then
      this.unlock()
      None
    else if this.lockingRiddle.isDefined && this.isLocked then
      Some("What you're doing with a key on a code lock?")
    else
      Some("There is no use for a key here.")

  // unlock directly without any conditioning
  def unlock(): Unit =
    this.isLocked = false
    this.doorAnimation = -1 // start door opening animation

  def lock(): Unit =
    this.isLocked = true
    this.doorAnimation = 1 // start door closing animation

end Corridor
