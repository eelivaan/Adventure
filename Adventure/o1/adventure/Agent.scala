package o1.adventure

import java.awt.Color
import java.awt.image.BufferedImage
import scala.collection.mutable.Map
import javax.imageio.ImageIO
import java.io.File
import scala.swing.Graphics2D
import scala.Console
import scala.math.sin

/**
 * Agents are entitites controlled by either a player or an AI.
 * They move within the game world and may possess items that they have picked.
 */
trait Agent(startingRoom: Room, val game: Adventure):

  private var currentRoom = startingRoom
  // the room that we are on our way to
  private var targetRoom: Option[Room] = None
  private var animationKey = 0.0

  var (cx,cy) = (startingRoom.cx, startingRoom.cy)
  val size = 30
  val speed = 80.0 // pixels per second
  var cheer = false
  private var isAlive = true
  def isHostile = false

  private val possessedItems = Map[String, Item]()

  /** Returns the agent’s current location. */
  def location = this.currentRoom

  /** Returns the agent's current possessions */
  def inventory = this.possessedItems

  /** Whether the agent is moving */
  def isMoving = this.targetRoom.isDefined

  def touches(other: Agent) = (this.cx - other.cx).abs.max((this.cy - other.cy).abs) < (this.size/2 + other.size/2)

  def alive = this.isAlive

  def kill() =
    this.isAlive = false

  /**
   * Try to move through the corridor into room on the other end.
   * Returns the other Room or None if the corridor is unpassable
   */
  def moveThrough(corridor: Corridor): Option[Room] =
    if !corridor.blocked then
      this.targetRoom = Some(if currentRoom == corridor.roomA then corridor.roomB else corridor.roomA)
      this.targetRoom
    else
      None

  // try to load a fancy image to be this agent's visual representation
  protected def imageFile: String
  private val icon: Option[BufferedImage] =
    try
      Some(ImageIO.read(new File(this.imageFile)))
    catch
      case _ =>
        Console.err.println(s"""Error reading file "$imageFile"""")
        None

  /**
   * Render this agent into given graphics context
   */
  def render(g: Graphics2D): Unit =
    this.icon match {
      case Some(value) if alive =>
        val dy = if cheer then sin(System.currentTimeMillis() / 80.0) * 5 else 0
        g.drawImage(value, cx-size/2, cy-size/2+dy.toInt, size,size, null)
      case _ =>
        g.setColor(new Color(100,0,0))
        g.fillOval(cx-size/2, cy-size/2, size,size)
    }

  /**
   * Update coordinates etc. as time passes
   */
  def tick(dt: Double): Unit =
    this.targetRoom match {
      case Some(targetRoom) =>
        // linear interpolation of position from previous room to next room
        val dx = targetRoom.cx - currentRoom.cx
        val dy = targetRoom.cy - currentRoom.cy
        this.cx = (currentRoom.cx + dx * animationKey).toInt
        this.cy = (currentRoom.cy + dy * animationKey).toInt
        animationKey += dt * speed / dx.abs.max(dy.abs)
        if animationKey > 1.0 then
          this.cx = targetRoom.cx
          this.cy = targetRoom.cy
          this.currentRoom = targetRoom
          this.targetRoom = None
          animationKey = 0.0
      case None =>
        ()
    }

end Agent

