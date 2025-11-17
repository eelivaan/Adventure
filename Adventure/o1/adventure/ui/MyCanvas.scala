package o1.adventure.ui

import o1.adventure.Adventure
import scala.swing.*
import scala.language.adhocExtensions  // enable extension of swing classes (is on by default?)

/**
 * Canvas does the drawing of all 2D graphics.
 */
class MyCanvas extends BoxPanel(Orientation.Vertical):

  var gameRef: Option[Adventure] = None

  // paintComponent will be called when GUI needs to be drawn or repaint() is called explicitly
  override def paintComponent(g: Graphics2D): Unit =
    super.paintComponent(g)

    // draw the maze and stuff
    gameRef.foreach( game =>
      val (tx,ty) = game.maze.renderOrigin
      g.translate(tx,ty)

      for room <- game.maze.rooms.flatten.flatten do
        room.render(g)

      for corridor <- game.maze.corridors do
        corridor.render(g)

      game.player.render(g)

      g.translate(-tx,-ty)
    )
  end paintComponent

end MyCanvas
