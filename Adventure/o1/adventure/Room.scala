package o1.adventure

import scala.collection.mutable.Map
import scala.swing.Graphics2D

/**
 * A Room is a basic building block of the game world.
 * It has 1 to 4 references to Corridors that lead to other rooms.
 * Rooms are initially hidden and only revealed as the player progresses.
 * Despite the technical complexity, Rooms are allowed to be of different sizes to make the world more interesting.
 */
class Room(
      val cx: Int, val cy: Int,
      val width: Int, val height: Int,
      private var hidden: Boolean = true,
      val isFinish: Boolean = false
    ):

  /**
   * Map of corridors leading out from this room
   */
  val corridors = Map[Dir, Corridor]()

  /**
   * Render this room into given graphics context
   */
  def render(g: Graphics2D) =
    g.setColor(if this.isFinish then cc.finishColor else cc.floorColor)
    g.fillRoundRect(cx-width/2, cy-height/2, width,height, 20,20)

    if this.hidden then
      g.setColor(cc.backgroundColor)
      g.fillRect(cx-width/2, cy-height/2, width,height)
  end render

  /**
   * Reveal this (yet hidden) room to the player
   */
  def reveal(): Unit =
    this.hidden = false
    this.corridors.values.foreach(_.reveal())

end Room

