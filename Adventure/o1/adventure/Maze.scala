package o1.adventure

import java.awt.Color
import scala.collection.mutable.{ListBuffer, ArrayBuffer}

// some color constants
object cc:
  val floorColor = new Color(100,100,150)
  val doorColor = new Color(50,50,50)
  val backgroundColor = new Color(0,0,0)
  val finishColor = new Color(100,150,100)


/**
 * Enumeration for all possible directions for the player to move
 */
enum Dir:
  case Right, Up, Down, Left, Invalid

  def opposite =
    this match {
      case Dir.Right => Dir.Left
      case Dir.Up => Dir.Down
      case Dir.Down => Dir.Up
      case Dir.Left => Dir.Right
      case Invalid => Invalid
    }

object Dir:
  def fromString(str: String): Dir =
    try
      Dir.valueOf(str.head.toUpper.toString + str.tail.toLowerCase)
    catch {
      case _: Throwable => Invalid
    }

val mazeDef = """
A-A-A-X
| |   |
A X-S A
| |   |
A-A-A-A
"""

/**
 * Maze is the game world that consists of Rooms and Corridors that connect them.
 * The centers of the Rooms have to align on each column and row although
 * the spacing of those can be uneven to fit Rooms of different sizes.
 */
class Maze:

  var rooms = ArrayBuffer[ArrayBuffer[Option[Room]]]()
  val corridors = ListBuffer[Corridor]()
  var startingRoom: Room = null
  var renderOrigin = (100,100)
  val startHidden = false

  init()
  startingRoom.reveal()

  // create the maze based on pattern string
  def init() =
    var strLines = mazeDef.linesIterator.toVector.map(_.trim).filter(_.nonEmpty)
    val lineMaxLen = strLines.map(_.length).max
    strLines = strLines.map(_.padTo(lineMaxLen, ' '))  // ensure each line has the same width

    // separate room type and corridor type information
    val roomsDescription = ListBuffer[ListBuffer[Char]]()
    val corridorsDescription = ListBuffer[ListBuffer[Char]]()
    for (line,rowIndex) <- strLines.zipWithIndex do
      if rowIndex % 2 == 0 then
        roomsDescription.append(ListBuffer[Char]())
      corridorsDescription.append(ListBuffer[Char]())
      for (ch,columnIndex) <- line.zipWithIndex do
        if rowIndex % 2 == 0 && columnIndex % 2 == 0 then
          roomsDescription.last += ch
        if columnIndex % 2 != rowIndex % 2 then  // corridor locations alternate with rows
          corridorsDescription.last += ch

    println(roomsDescription.mkString("\n"))
    println()
    println(corridorsDescription.mkString("\n"))

    val gridWidth = roomsDescription.head.length
    val gridHeight = roomsDescription.length
    // create 2D grid of rooms
    rooms = ArrayBuffer.from(
      for iy <- (0 until gridHeight) yield
        ArrayBuffer.from(
          for ix <- (0 until gridWidth) yield
            roomsDescription(iy)(ix) match {
              case ch: ('A'|'S'|'X') =>
                val newRoom = new Room(ix*150,iy*150, 100,100, hidden = startHidden, isFinish = ch=='X')
                if ch == 'S' then  // set as starting room
                  startingRoom = newRoom
                Some(newRoom)
              case _ =>
                None
            }
        ))
    // create corridors
    for (row,iy) <- corridorsDescription.zipWithIndex do
      for (ch,ix) <- row.zipWithIndex do
        ch match {
          case '-' =>
            (rooms(iy/2)(ix), rooms(iy/2)(ix+1)) match {
              case (Some(roomA), Some(roomB)) =>
                corridors += new Corridor(roomA, roomB, blocked = true, hidden = startHidden)
              case _ =>
            }
          case '|' =>
            (rooms(iy/2)(ix), rooms(iy/2+1)(ix)) match {
              case (Some(roomA), Some(roomB)) =>
                corridors += new Corridor(roomA, roomB, blocked = true, hidden = startHidden)
              case _ =>
            }
          case _ =>
        }

    // starting room is needed even if it wasn't defined in pattern
    if startingRoom == null then
      println("Starting room is not defined!")
      startingRoom = rooms.head.head.get
  end init

end Maze
