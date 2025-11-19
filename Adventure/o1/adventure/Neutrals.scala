package o1.adventure

class Gatekeeper(corridor: Corridor, game: Adventure) extends Agent(corridor.roomA, game):

  override protected def imageFile: String = "Adventure/sprites/guardian.png"

  this.cx = corridor.cx
  this.cy = corridor.cy

end Gatekeeper
