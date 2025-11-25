package o1.adventure

/**
 * This class represents riddles or puzzles for the player to solve.
 * Alternative answers can be defined by separating them with '|'
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


class CodeLock(code: String)
  extends Riddle("That door seems to be locked with a code.\nWhat might the code be?", code):
end CodeLock
