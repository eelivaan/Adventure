package o1.adventure.ui

import scala.swing.*
import scala.swing.event.*
import javax.swing.border.*
import javax.swing.{Timer, UIManager}
import java.awt.{Dimension, Font}
import scala.collection.mutable.{ArrayBuffer, Queue}
import scala.io.Source
import scala.language.adhocExtensions
import o1.adventure.Adventure


/**
 * Main Application object
 */
object AdventureGUI extends SimpleSwingApplication:
  // neat font for the text areas
  val niceFont = new Font("Consolas", Font.PLAIN, 14)
  val bgcolor = new Color(0,0,0)
  val fgcolor = new Color(0,255,0)
  // enable modern GUI look
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)
  UIManager.put("Panel.background", bgcolor)

  var game = new Adventure()
  val messageBuffer = Queue[Char]()
  val commandHistory = ArrayBuffer[String]("go up", "go down", "go right", "go left")

  // Components:

  val textDispaly = new TextArea():
    editable = false
    wordWrap = true
    lineWrap = true
    background = new Color(0,0,0, 0)
    foreground = fgcolor
    opaque = false
    preferredSize = new Dimension(600,500)
    font = niceFont
    focusable = false

  val inputField = new TextField():
    background = bgcolor
    foreground = fgcolor
    preferredSize = new Dimension(600,30)
    caret.color = new Color(200,200,200)
    font = niceFont

  val helpBtn = new Button("Help"):
    //background = new Color(0,0,0)
    //foreground = fgcolor
    contentAreaFilled = true
    focusable = false
    reactions += { case e: ButtonClicked => showHelp() }

  val canvas = new Canvas():
    contents += textDispaly
    contents += new FlowPanel(inputField, helpBtn)
    border = new EmptyBorder(10,10,10,10)
    gameRef = Some(game)

  // main window definition
  def top = new MainFrame:

    // Events:

    this.listenTo(inputField.keys)
    this.reactions += {
      case keyEvent: KeyPressed =>
        keyEvent.key match {
          // submit command
          case Key.Enter =>
            val command = inputField.text.trim
            if command == "restart" then
              restartGame()
            else if game.gameRunning && command.nonEmpty then
              submitCommand(command)

          // scroll through previous commands
          case Key.Up | Key.Down =>
            commandHistory.lastIndexOf(inputField.text.trim) match {
              case i if i > 0 && keyEvent.key == Key.Up =>
                inputField.text = commandHistory(i-1)
              case i if i < commandHistory.length-1 && keyEvent.key == Key.Down =>
                inputField.text = commandHistory(i+1)
              case _ =>
                inputField.text = commandHistory.lastOption.getOrElse("")
            }

          // end the game
          case Key.Escape =>
            quit()

          case _ =>
        }
    }

    this.contents = canvas
    this.title = game.title
    this.pack()
    this.centerOnScreen()
    inputField.requestFocusInWindow()
    canvas.centerPlayerOnScreen()
  end top


  // timer for ticking 30 times per second
  val tickTimer = new Timer(33, Swing.ActionListener { _ => this.onTick() })
  var prevTimeStamp = System.currentTimeMillis()
  tickTimer.start()

  showMessage(game.welcomeMessage)


  private def restartGame() =
    game = new Adventure()
    canvas.gameRef = Some(game)
    canvas.centerPlayerOnScreen()
    showMessage("")
    inputField.text = ""


  private def onTick() =
    val curTime = System.currentTimeMillis()

    if game.gameRunning then
      val deltaTime = (curTime - prevTimeStamp) / 1000.0
      game.agents.foreach(_.tick(deltaTime))
      canvas.keepPlayerOnScreen(deltaTime)

      if game.isComplete then
        showMessage(game.victoryMessage)
        game.gameRunning = false
        game.player.cheer = true
      else if game.isOver then
        showMessage(game.lostMessage)
        game.gameRunning = false

    // type characters onto screen from the message buffer
    if this.messageBuffer.nonEmpty then
      textDispaly.text += this.messageBuffer.dequeue()

    canvas.repaint()
    prevTimeStamp = curTime
  end onTick


  def submitCommand(command: String) =
    commandHistory.filterInPlace(_ != command)
    commandHistory += command

    val response = game.player.parseCommand(command)
    showMessage(response)
    inputField.text = ""


  def showMessage(message: String) =
    if this.messageBuffer.isEmpty then
      textDispaly.text = ""
    this.messageBuffer ++= (message + "\n").toList


  def showHelp() =
    val helpWnd = new Frame():
      visible = true
      title = "Help"
      minimumSize = new Dimension(300,300)

      contents = new ScrollPane():
        contents = new TextArea():
          background = bgcolor
          foreground = new Color(100,200,255)
          font = niceFont
          editable = false
          border = new EmptyBorder(10,10,10,10)
          try
            val file = Source.fromFile("Adventure/help.txt")
            this.text = file.mkString
            file.close()
          catch
            case _ => Console.err.println("Failed to read help file")
  end showHelp


  // Enable this code to work even under the -language:strictEquality compiler option:
  private given CanEqual[Component, Component] = CanEqual.derived
  private given CanEqual[Key.Value, Key.Value] = CanEqual.derived

end AdventureGUI

