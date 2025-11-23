package o1.adventure

import scala.util.Random

/**
 * Gatekeepers guard one corridor each and ask player to solve riddles to pass through that corridor.
 * If player fails to answer the Gatekeeper blocks more corridors as a punishment.
 * Gatekeepers choose their riddles from a collection by random.
 */
class Gatekeeper(corridor: Corridor) extends Agent(corridor.roomA):

  override protected def imageFile: String = "Adventure/sprites/guardian.png"

  private val allRiddles = Vector(
    // www.solveordie.com/clean-riddles and other sources
    Riddle("It is always coming but it never arrives.\n What is it?", "tomorrow"),
    Riddle("It has 13 hearts but no lungs or stomach.\n What is it?", "a deck of cards|deck of cards|carddeck"),
    Riddle("What animal walks on four legs in the morning,\n two legs during the day, and three legs in the evening?", "human|man"),
    Riddle("What is the meaning of life, the universe and everything?", "42"),
    Riddle("You bury me when I am alive, and dig me up when I die.\n What am I?", "a plant|plant"),
    Riddle("The more you take, the more you leave behind.\n What is it?", "footstep|footsteps")
  )

  this.cx = corridor.cx
  this.cy = corridor.cy
  val riddle = allRiddles(Random.between(0, allRiddles.length))
  riddle.questioner = Some(this)

  def message =
    this.cheer = true
    "- Gatekeeper:\n" +
    "\"To get through you need to solve this riddle of mine:\n\n" +
    s" ${riddle.question}\""

  def tryToAnswer(answer: String, playerCurrentRoom: Room): String =
    if riddle.testAnswer(answer) then
      this.cheer = false
      this.corridor.unlock()
      "Correct. You may go."
    else
      // as a punishment, block one of the other corridors in player's current room
      val otherCorridors = playerCurrentRoom.corridors.values.filter(corridor => corridor != this.corridor && !corridor.blocked).toVector
      Random.shuffle(otherCorridors).headOption.foreach( _.lock() )
      "Wrong answer."

end Gatekeeper

// Used in maze definition to mark corridors that have Gatekeepers
object Gatekeeper