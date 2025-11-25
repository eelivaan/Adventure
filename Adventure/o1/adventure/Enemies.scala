package o1.adventure

import scala.math.{sin,cos,random}


/**
 * Slaybots circle around in their room and kill anyone they collide with.
 */
class Slaybot(startingRoom: Room, val game: Adventure) extends Agent(startingRoom):

  override protected def imageFile: String = "Adventure/sprites/slaybot.png"

  override def isHostile: Boolean = true

  val offset = random() * 6.28

  override def tick(dt: Double): Unit =
    super.tick(dt)

    // roam around the room
    val t = offset + System.currentTimeMillis() / 700.0
    val r = this.location.width / 2 - this.size / 2
    this.cx = this.location.cx + (cos(t) * r).toInt
    this.cy = this.location.cy + (sin(t) * r).toInt

    // game over if this thing gets the player
    if this.touches(game.player) then
      game.player.kill()
      cheer = true
    else
      // these kill other enemies as well
      game.agents.filter(a => a.isHostile && a != this).find( this.touches(_) ).foreach(
        victim => victim.kill()
      )
  end tick

end Slaybot


/**
 * Chasebots wait in one room until they see the player in which case they start a chase.
 */
class Chasebot(startingRoom: Room, val game: Adventure) extends Agent(startingRoom):

  override protected def imageFile: String = "Adventure/sprites/slaybot.png"

  override def isHostile: Boolean = true

  override def tick(dt: Double): Unit =
    super.tick(dt)

    // if the player is found on any neighbouring room, start a pursuit
    this.location.corridors.values.foreach( corridor =>
      if corridor.otherRoom(this.location) == game.player.location then
        this.moveThrough(corridor)
    )

    // game over if this thing gets the player
    if this.touches(game.player) then
      game.player.kill()
      cheer = true
  end tick

end Chasebot
