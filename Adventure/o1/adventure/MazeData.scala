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
D-#-D-X
| |   |
D-A-1 D-D
| |   |
D-B-CaD
"""

val roomHints = Map[Char, String](
  'A' -> "what", 'B' -> "is the meaning", 'C' -> "of life"
)

val corridorRiddles = Map[Char, Riddle](
  'a' -> Riddle("The door seems to be having a code lock.\nWhat might the code be?", "42")
)
