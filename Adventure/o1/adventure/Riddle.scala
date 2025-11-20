package o1.adventure

/**
 * This class represents riddles or puzzles for the player to solve
 */
class Riddle(
              val question: String,
              answer: String,
              var questioner: Option[Corridor|Agent] = None
            ):
  val answers = answer.toLowerCase.split('|')

  def testAnswer(answer: String) =
    this.answers.contains(answer.toLowerCase)
end Riddle
