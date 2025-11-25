package o1.adventure

import scala.collection.mutable.Map
import scala.swing.Graphics2D
import scala.util.Random

/**
 * A Room is the basic building block of the game world.
 * It has 1 to 4 references to Corridors that lead to other rooms.
 * Rooms are initially hidden and only revealed as the player progresses.
 */
class Room(
      val cx: Int, val cy: Int,
      val width: Int, val height: Int,
      private var hidden: Boolean = true,
      val isFinish: Boolean = false,
      val rng: Random = Random()
    ):

  /** Map of corridors leading out from this room */
  val corridors = Map[Dir, Corridor]()

  /** Items in this room */
  val items = Map[String, Item]()

  def addItem(newItem: Item) =
    items += newItem.name -> newItem

  def pickItem(itemName: String): Option[Item] =
    items.remove(itemName)

  def neighbouringRooms: Map[Dir, Room] =
    this.corridors.map( (dir,corridor) => dir -> corridor.otherRoom(this) )

  var hint = ""
  var spawnCirclingEnemy = false
  var spawnChasingEnemy = false

  /*private val decorations = Array.fill(10)(
    (this.cx + rng.between(-width/2,width/2-20),
     this.cy + rng.between(-height/2,height/2-20),
     rng.between(9,20))
  )*/

  private var coverAlpha = if hidden then 1.0 else 0.0

  /**
   * Render this room into given graphics context
   */
  def render(g: Graphics2D) =
    g.setColor(if this.isFinish then cc.finishColor else cc.floorColor)
    g.fillRoundRect(cx-width/2, cy-height/2, width,height, 20,20)

    //g.setColor(cc.decorColor)
    //for (x,y,size) <- this.decorations do
    //  g.fillOval(x, y, size,size)

    items.values.foreach( item => item.render(g, this.cx, this.cy) )

    if this.hint.nonEmpty then
      g.setColor(cc.hintColor)
      g.drawString(this.hint, (cx-width/2.2).toInt, (cy+height/2.5).toInt)
  end render


  /** Draw black rectangle on top to hide the room */
  def renderCover(g: Graphics2D) =
    if this.coverAlpha > 0.0 then
      val (width,height) = (this.width+6, this.height+6)
      g.setColor(cc.withAlpha(cc.backgroundColor, this.coverAlpha))
      g.fillRect(cx-width/2, cy-height/2, width,height)
  end renderCover


  /** Advance animations */
  def tick(dt: Double) =
    if !this.hidden && this.coverAlpha > 0.0 then
      // fade out the covering
      this.coverAlpha -= 0.6 * dt
      this.corridors.values.foreach(corridor =>
        corridor.coverAlpha = corridor.coverAlpha.min(this.coverAlpha)
      )
  end tick


  /**
   * Reveal this (yet hidden) room to the player.
   * Also reveals any connected corridors.
   */
  def reveal(): Unit =
    if this.hidden then
      this.hidden = false
      this.corridors.values.foreach( corridor =>
        corridor.reveal()
        // rooms with circling enemies need to be shown earlier
        val otherRoom = corridor.otherRoom(this)
        if otherRoom.spawnCirclingEnemy then
          otherRoom.reveal()
      )
  end reveal

end Room

