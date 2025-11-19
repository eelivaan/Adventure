package o1.adventure

import scala.swing.Graphics2D
import scala.swing.Orientation.{Horizontal, Vertical}

/**
 * A Corridor connects two Rooms and allows the player two move between them.
 * The Corridor may be blocked and the player has to perform some tasks to open it.
 */
class Corridor(
      val roomA: Room,
      val roomB: Room,
      private var lockingRiddle: Option[Riddle] = None,
      private var hidden: Boolean = true
    ):

  lockingRiddle.foreach(_.questioner = Some(this))
  private var isLocked = lockingRiddle.isDefined

  def blocked = this.isLocked

  def riddle = lockingRiddle

  def spawnGateKeeper = this.lockingRiddle.exists(_.withGateKeeper)

  // the corridor automatically fits itself between the rooms
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
  val length = (if (orientation == Horizontal) then dx.abs - rw else dy.abs - rh) + 10  // small offset to account for antialiasing

  private var doorAnimationKey = if this.blocked then 1.0 else 0.0
  private var doorAnimation = 0.0

  /**
   * Render this corridor into given graphics context
   */
  def render(g: Graphics2D, pass: Int) =
    var w = if orientation == Horizontal then this.length else this.width
    var h = if orientation == Horizontal then this.width else this.length

    if pass == 1 then
      g.setColor(cc.floorColor)
      g.fillRect(cx-w/2, cy-h/2, w,h)

      if !spawnGateKeeper && doorAnimationKey > 0.0 then
        g.setColor(cc.doorColor)
        if orientation == Horizontal then
          g.fillRect(cx-10, cy-h/2, 20,(h * doorAnimationKey).toInt)
        else
          g.fillRect(cx-w/2, cy-10, (w * doorAnimationKey).toInt,20)

    else if pass == 2 && this.hidden then
      // draw a bit larger rectangle to cover
      w += 5; h += 5
      g.setColor(cc.backgroundColor)
      g.fillRect(cx-w/2, cy-h/2, w,h)
  end render

  def tick(dt: Double) =
    this.doorAnimationKey = (doorAnimationKey + doorAnimation.sign * 0.3 * dt).min(1.0).max(0.0)

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
   * Try to unlock the passage should it be blocked.
   */
  def unlock(answer: String): String =
    this.lockingRiddle match {
      case Some(riddle) =>
        if answer == riddle.answer then
          this.isLocked = false
          this.doorAnimation = -1 // start opening
          "Correct."
        else
          "Wrong answer."
      case None =>
        ""
    }

end Corridor
