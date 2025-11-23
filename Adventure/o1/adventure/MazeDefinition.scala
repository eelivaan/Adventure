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
  val decorColor = new Color(20,60,70,40)
  val default = new Color(50,50,50)

  def withAlpha(c: Color, alpha: Double) =
    new Color(c.getRed, c.getGreen, c.getBlue, (alpha*255).toInt)


val mazePattern = """
  0-Ng0-X
    k
  Pk0 $-0-F-0-#-0g0 $
    | | | | | |   | |
!-0-E-#-0 ! $g0-* #-O
      b     | |   | |
    D-C-B-A-#-0kO-# 0 X
    c         k     | #
    X         G-H-L-Md0
"""

val roomHints = Map[Char, String](
  'A' -> "1+1", 'B' -> "2^3", 'C' -> "3!", 'D' -> "4/5",
  'E' -> "<<", 'F' -> "!!",
  'G' -> "A=10", 'H' -> "D=13", 'L' -> "F=15", 'M' -> "BCCE=?",
  'N' -> ">>", 'P' -> ":P"
)

val corridorBlockings = Map(
  'a' -> CodeLock("42"),
  'b' -> CodeLock("286"),
  'c' -> CodeLock("2864|2865"),
  'd' -> CodeLock("11121214"),
  'g' -> Gatekeeper,
  'k' -> Key,
)
