package o1.adventure

import scala.util.Random

/**
 * Gatekeepers guard one corridor each and ask player to solve riddles to pass through that corridor.
 * If player fails to answer the Gatekeeper blocks more corridors.
 * Gatekeepers choose their riddles from a collection by random.
 */
class Gatekeeper(corridor: Corridor) extends Agent(corridor.roomA):

  override protected def imageFile: String = "Adventure/sprites/guardian.png"

  private val allRiddles = Vector(
    // www.solveordie.com and other sources
    Riddle("What is always coming but never arrives?", "tomorrow"),
    Riddle("I have 13 hearts but no lungs or stomach. What am I?", "a deck of cards|deck of cards|carddeck"),
    Riddle("What animal walks on four legs in the morning, two legs during the day, and three legs in the evening?", "human|man"),
    Riddle("What is the meaning of life, the universe and everything?", "42"),
  )

  this.cx = corridor.cx
  this.cy = corridor.cy
  val riddle = allRiddles(Random.between(0, allRiddles.length))
  riddle.questioner = Some(this)

  def message =
    this.cheer = true
    "- Gatekeeper:\n" +
    "\"To get through you need to answer my question correctly:\n\n" +
    s"${riddle.question}\""

  def tryToAnswer(answer: String, playerCurrentRoom: Room): String =
    if riddle.testAnswer(answer) then
      this.cheer = false
      this.corridor.unlock()
      "Correct. You may go."
    else
      // as a punishment, block one of the other corridors in player's current room
      val otherCorridors = playerCurrentRoom.corridors.values.filter(_ != this.corridor).toVector
      Random.shuffle(otherCorridors).headOption.foreach( _.lock() )
      "Wrong answer."

end Gatekeeper

// Used in maze definition to mark corridors that have Gatekeepers
object Gatekeeper