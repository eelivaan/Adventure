package o1.adventure.ui

import o1.adventure.Adventure

import java.awt.{RenderingHints, Font}
import scala.swing.*
import scala.language.adhocExtensions  // enable extension of swing classes (is off by default?)

/**
 * Canvas does the drawing of all 2D graphics.
 */
class Canvas extends BoxPanel(Orientation.Vertical):

  val hintFont = new Font("Consolas", Font.ITALIC, 15)
  var gameRef: Option[Adventure] = None
  var renderOrigin = (100,100)
  var textToHighlight = ""

  /**
   * Move render origin so that player is right in the center of the screen
   */
  def centerPlayerOnScreen() =
    gameRef.foreach( game =>
      this.renderOrigin = (this.size.width/2  - game.player.cx,
                           this.size.height/2 - game.player.cy)
    )

  /**
   * Move render origin so that the player stays on the screen
   */
  def keepPlayerOnScreen(dt: Double) =
    gameRef.foreach( game =>
      // screen center coordinates
      val (cx,cy) = (this.size.width/2, this.size.height/2)
      val (ox,oy) = this.renderOrigin
      // player coordinates on the screen
      val (px,py) = (ox + game.player.cx, oy + game.player.cy)

      def diffOutside(value: Int, range: Int) = value.sign * (value.abs - range).max(0)
      val (dx,dy) = (diffOutside(cx - px, cx/2), diffOutside(cy - py, cy/2))

      this.renderOrigin = ((ox + dx.sign * game.player.speed * dt).toInt,
                           (oy + dy.sign * game.player.speed * dt).toInt)
    )

  // paintComponent will be called when GUI needs to be drawn or repaint() is called explicitly
  override def paintComponent(g: Graphics2D): Unit =
    super.paintComponent(g)

    // enable antialiasing and bilinear image sampling for visual fidelity
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)

    // draw the maze and stuff
    gameRef.foreach( game =>
      g.setFont(hintFont)
      val (tx,ty) = this.renderOrigin
      g.translate(tx,ty)

      // render floors and props
      game.maze.roomsIterator.foreach(_.render(g))
      game.maze.corridorsIterator.foreach(_.render(g))

      // render agents
      for agent <- game.agents do
        agent.render(g)

      // render hiding covers
      game.maze.roomsIterator.foreach(_.renderCover(g))
      game.maze.corridorsIterator.foreach(_.renderCover(g))

      g.translate(-tx,-ty)

      // draw semi-transparent background for messages so that they can be seen more clearly
      g.setColor(new Color(0,0,0, 150))
      val (cw,ch) = (8, 17)
      for (line,iy) <- textToHighlight.linesIterator.zipWithIndex do
        g.fillRect(10, 10+iy*ch, line.length*cw,ch)
    )
  end paintComponent

end Canvas
