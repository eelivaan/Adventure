package o1.adventure

import scala.swing.Graphics2D
import scala.math.{sin, min, max}
import scala.collection.mutable.Set

val splatterSprite = loadSprite("Adventure/sprites/splatter.png")

/**
 * Agents are entitites controlled by either a player or an AI.
 * They move within the game world and may possess items that they have picked.
 */
trait Agent(startingRoom: Room):

  private var currentRoom = startingRoom
  /** the room that we are moving to */
  private var targetRoom: Option[Room] = None
  private var (tgtX,tgtY) = (0,0)
  /** Direction of focus for opening doors etc. */
  var focus: Dir = Dir.Invalid
  var (cx,cy) = (startingRoom.cx, startingRoom.cy)
  val size = 30
  val speed = 80.0 // pixels per second
  var cheer = false
  private var isAlive = true
  protected val possessedItems = Set[Item]()

  def isHostile = false

  /** Returns the agent’s current location. */
  def location = this.currentRoom

  /** Returns the agent's current possessions */
  def inventory: Map[String, Item] = this.possessedItems.map( item => item.name -> item ).toMap

  def isMoving = this.targetRoom.isDefined

  def touches(other: Agent) =
    if other == this then false else
    (this.cx - other.cx).abs.max((this.cy - other.cy).abs) < (this.size/2 + other.size/2)

  def alive = this.isAlive

  def kill() =
    this.isAlive = false

  /**
   * Try to move through the corridor into room on the other end.
   * Returns the target room or None if the corridor is unpassable
   */
  def moveThrough(corridor: Corridor): Option[Room] =
    if !corridor.blocked && !this.isMoving then
      setTargetRoom(corridor.otherRoom(this.location))
    else
      None

  private def setTargetRoom(room: Room) =
    this.targetRoom = Some(room)
    this.tgtX = room.cx
    this.tgtY = room.cy
    this.targetRoom

  /** Invert any active movement */
  def retreat(): Unit =
    if this.isMoving then
      setTargetRoom(this.currentRoom)

  /** Called when the agent arrives in a new room */
  def onArrival(room: Room): Unit = ()

  protected def imageFile: String
  // try to load a fancy image to be this agent's visual representation
  private val sprite = loadSprite(this.imageFile)

  /**
   * Render this agent into given graphics context
   */
  def render(g: Graphics2D): Unit =
    if !this.alive then
      splatterSprite.foreach(sprite => g.drawImage(sprite, cx-20,cy-20, 40,40, null))
    else
      // body
      this.sprite match {
        case Some(value) if alive =>
          val dy = if cheer then sin(System.currentTimeMillis() / 80.0) * 5 else 0
          g.drawImage(value, cx-size/2, cy-size/2+dy.toInt, size,size, null)
        case _ =>
          g.setColor(cc.default)
          g.fillOval(cx-size/2, cy-size/2, size,size)
      }
      // possessions
      var dx = 0
      this.possessedItems.foreach( item =>
        item.render(g, this.cx+dx, this.cy+5)
        dx += 5
      )
      // focus arrow
      Dir.arrowSprites.get(this.focus).flatten match {
        case Some(sprite) =>
          g.drawImage(sprite, cx-30,cy-30, 60,60, null)
        case None =>
      }

  /**
   * Update coordinates etc. as time passes.
   * Only called for living agents.
   */
  def tick(dt: Double): Unit =
    if this.isMoving then
      // animated movement from previous room to next room
      val dx = this.tgtX - this.cx
      val dy = this.tgtY - this.cy
      val maxMovement = (this.speed * dt).toInt
      if maxMovement > max(dx.abs, dy.abs) then
        this.cx = this.tgtX
        this.cy = this.tgtY
        targetRoom.foreach( newRoom =>
          onArrival(newRoom)
          this.currentRoom = newRoom
        )
        this.targetRoom = None
      else
        this.cx += dx.sign * min(dx.abs, maxMovement)
        this.cy += dy.sign * min(dy.abs, maxMovement)
  end tick

end Agent

