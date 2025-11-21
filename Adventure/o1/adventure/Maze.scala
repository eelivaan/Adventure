package o1.adventure

import scala.collection.mutable.{ListBuffer, ArrayBuffer}
import scala.util.Random
import java.awt.image.BufferedImage

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
  val arrowSprites = Map[Dir, Option[BufferedImage]](
    Dir.Right -> loadSprite("Adventure/sprites/arrow_east.png"),
    Dir.Up -> loadSprite("Adventure/sprites/arrow_north.png"),
    Dir.Down -> loadSprite("Adventure/sprites/arrow_south.png"),
    Dir.Left -> loadSprite("Adventure/sprites/arrow_west.png"),
    Dir.Invalid -> None
  )

  def fromString(str: String): Dir =
    try
      Dir.valueOf(str.head.toUpper.toString + str.tail.toLowerCase)
    catch {
      case _: Throwable => Invalid
    }


/**
 * Maze is the game world that consists of Rooms and Corridors that connect them.
 * The centers of the Rooms align on a grid.
 */
class Maze:

  private var rooms = ArrayBuffer[ArrayBuffer[Option[Room]]]()
  def roomsIterator = rooms.flatten.flatten.toVector

  private val corridors = ListBuffer[Corridor]()
  def corridorsIterator = corridors.toVector

  private val rng = Random(1234)

  var startingRoom: Room = null

  init()
  startingRoom.reveal()

  // create the maze based on pattern string
  def init() =
    var strLines = mazePattern.linesIterator.toVector.map(_.stripTrailing).filter(_.nonEmpty)
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

    //println(roomsDescription.mkString("\n"))
    //println()
    //println(corridorsDescription.mkString("\n"))

    val gridWidth = roomsDescription.head.length
    val gridHeight = roomsDescription.length

    // create 2D grid of rooms
    rooms = ArrayBuffer.from(
      for iy <- (0 until gridHeight) yield ArrayBuffer.from(
          for ix <- (0 until gridWidth) yield
            val (cx,cy) = (ix*200,iy*200)
            roomsDescription(iy)(ix) match {
              case ch if !ch.isWhitespace =>
                val size = rng.between(120,160)
                val newRoom = new Room(cx,cy, size,size, hidden = true, isFinish = ch=='X')
                if ch == '*' then  // set as starting room
                  startingRoom = newRoom
                newRoom.hint = roomHints.getOrElse(ch, "")
                newRoom.spawnBoundEnemy = (ch == '#')
                newRoom.spawnChasingEnemy = (ch == '!')
                if roomsWithKeys.contains(ch) then
                  newRoom.addItem(new Key())
                Some(newRoom)
              // no room
              case _ =>
                None
            }
        ))

    // create corridors
    for (row,iy) <- corridorsDescription.zipWithIndex do
      for (ch,ix) <- row.zipWithIndex do
        if !ch.isWhitespace then
          // even rows have horizontal corridors
          if iy % 2 == 0 then
            (rooms(iy/2)(ix), rooms(iy/2)(ix+1)) match {
              case (Some(roomA), Some(roomB)) =>
                corridors += new Corridor(roomA, roomB, corridorBlockings.get(ch))
              case _ =>
            }
          // odd rows have vertical corridors
          else
            (rooms(iy/2)(ix), rooms(iy/2+1)(ix)) match {
              case (Some(roomA), Some(roomB)) =>
                corridors += new Corridor(roomA, roomB, corridorBlockings.get(ch))
              case _ =>
            }
          end if
        end if

    // starting room is needed even if it wasn't defined in pattern
    if startingRoom == null then
      println("Starting room is not defined!")
      startingRoom = new Room(0,0,100,100)
  end init

end Maze
