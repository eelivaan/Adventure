package o1.adventure

import scala.math.{sin,cos}

class Slaybot(startingRoom: Room,
              val game: Adventure,
              val huntsPlayer: Boolean) extends Agent(startingRoom):

  override protected def imageFile: String = "Adventure/sprites/slaybot.png"

  override def isHostile: Boolean = true

  override def tick(dt: Double): Unit =
    super.tick(dt)

    if this.huntsPlayer then
      // if the player is found on any neigbouring room, start a pursuit
      this.location.corridors.values.find(_.otherRoom(this.location) == game.player.location)
      match {
        case Some(corridor) =>
          this.moveThrough(corridor)
        case None =>
      }
    else
      // roam around the room
      val t = System.currentTimeMillis() / 1000.0
      val r = this.location.width / 2 - this.size / 2
      this.cx = this.location.cx + (cos(t) * r).toInt
      this.cy = this.location.cy + (sin(t) * r).toInt
      // these kill other enemies
      game.agents.filter(a => a.isHostile && a != this).find( this.touches(_) ).foreach(
        victim => victim.kill()
      )

    // game over if this thing gets the player
    if this.touches(game.player) then
      game.player.kill()
      cheer = true
  end tick

end Slaybot
