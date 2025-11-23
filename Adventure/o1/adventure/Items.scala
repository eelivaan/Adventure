package o1.adventure

import scala.swing.Graphics2D
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


/** Try to load sprite image from given file */
def loadSprite(filename: String): Option[BufferedImage] =
  try
    Some(ImageIO.read(new File(filename)))
  catch
    case _ =>
      Console.err.println(s"""Error reading image file "$filename"""")
      None


/**
 *  Items are things that agents can carry and use to do something.
 *  Initially an item is located in some Room waiting for the player to pick it up.
 */
class Item(val name: String):

  def imageFile = ""

  val sprite = loadSprite(this.imageFile)

  val size = 25

  override def toString = this.name

  def render(g: Graphics2D, x: Int, y: Int) =
    this.sprite match {
      case Some(sprite) =>
        g.drawImage(sprite, x, y, size, size, null)
      case None =>
        g.setColor(new Color(50,50,50))
        g.fillOval(x, y, size, size)
    }

end Item


class Key extends Item("key"):
  override def imageFile: String = "Adventure/sprites/key.png"
end Key

// needed only for maze definition
object Key