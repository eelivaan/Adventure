package o1.adventure

import scala.math.{sin,cos}

class Slaybot(startingRoom: Room, game: Adventure) extends Agent(startingRoom, game):

  override protected def imageFile: String = "Adventure/slaybot.png"

  override def isHostile: Boolean = true

  override def tick(dt: Double): Unit =
    super.tick(dt)
    // roam around the room
    val t = System.currentTimeMillis() / 1000.0
    val r = this.location.width / 2 - this.size / 2
    this.cx = this.location.cx + (cos(t) * r).toInt
    this.cy = this.location.cy + (sin(t) * r).toInt
    // game over if this thing eats the player
    if this.touches(game.player) then
      game.player.kill()
      cheer = true

end Slaybot
