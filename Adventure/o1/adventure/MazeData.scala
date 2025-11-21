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
  0-0g0-X
    k
    0 $-0-F-0-#-0g0 0
    | | | | | |   | |
!-0-E-0-0 ! $g0-* 0-O
      b     | |   | |
    D-C-B-A-0-0kO-# 0-X
    c
    X
"""

val roomHints = Map[Char, String](
  'A' -> "1+1", 'B' -> "2^3", 'C' -> "3!", 'D' -> "4/5",
  'E' -> "<<", 'F' -> "!!",
)

val codeLock = "The door seems to be having a code lock.\nWhat might the code be?"

val corridorBlockings = Map(
  'a' -> Riddle(codeLock, "42"),
  'b' -> Riddle(codeLock, "286"),
  'c' -> Riddle(codeLock, "2864|2865"),
  'g' -> Gatekeeper,
  'k' -> Key,
)

val roomsWithKeys = Vector('$')