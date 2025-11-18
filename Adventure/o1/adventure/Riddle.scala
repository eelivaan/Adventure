package o1.adventure

/**
 * This class represents riddles or puzzles for the player to solve
 */
class Riddle(
              val question: String,
              val answer: String,
              var questioner: Option[Corridor|Agent] = None
            )
