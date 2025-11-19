package o1.adventure

/**
 * This class represents riddles or puzzles for the player to solve
 */
class Riddle(
              private val _question: String,
              val answer: String,
              val withGateKeeper: Boolean = false,
              var questioner: Option[Corridor|Agent] = None
            ):

  def question =
    if withGateKeeper then
s"""Gatekeeper:
"To get through you need to answer my question correctly.
 $_question"
"""
    else
      _question
