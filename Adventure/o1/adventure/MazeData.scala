package o1.adventure

import java.awt.Color
import scala.collection.immutable.Map


// some color constants
object cc:
  val floorColor = new Color(100,100,150)
  val doorColor = new Color(50,50,50)
  val backgroundColor = new Color(0,0,0)
  val finishColor = new Color(100,150,100)
  val hintColor = new Color(10,10,10)


val mazePattern = """
X-0-! 0-#-0cX
  | | | |   |
0-0-0 0-A-1 O-O
  b   | |   |
G-F-E-D-B-CaO
"""

val roomsWithBoundEnemies = Vector('#')

val roomsWithFreeEnemies = Vector('!')

val roomHints = Map[Char, String](
  'A' -> "what", 'B' -> "is the meaning of", 'C' -> "life and everything",
                                             'D' -> "1+1", 'E' -> "2^3", 'F' -> "3!",
  'G' -> "4/5"
)

val codeLock = "The door seems to be having a code lock.\nWhat might the code be?"

val corridorRiddles = Map[Char, Riddle](
  'a' -> Riddle(codeLock, "42"),
  'b' -> Riddle(codeLock, "286"),
  'c' -> Riddle("What is always coming but never really here?", "tomorrow", withGateKeeper = true),
)
