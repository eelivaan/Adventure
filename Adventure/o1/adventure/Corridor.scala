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
      var blocked: Boolean = true,
      private var hidden: Boolean = true
    ):

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
  val length = if (orientation == Horizontal) then dx.abs - rw else dy.abs - rh

  /**
   * Render this corridor into given graphics context
   */
  def render(g: Graphics2D) =
    val w = if orientation == Horizontal then this.length else this.width
    val h = if orientation == Horizontal then this.width else this.length
    g.setColor(cc.floorColor)
    g.fillRect(cx-w/2, cy-h/2, w,h)

    if this.blocked then
      g.setColor(cc.doorColor)
      if orientation == Horizontal then
        g.fillRect(cx-10, cy-h/2, 20,h)
      else
        g.fillRect(cx-w/2, cy-10, w,20)

    if this.hidden then
      g.setColor(cc.backgroundColor)
      g.fillRect(cx-w/2, cy-h/2, w,h)

  end render

  /**
   * Reveal this corridor to the player
   */
  def reveal(): Unit =
    this.hidden = false

end Corridor
