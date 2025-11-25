package o1.adventure.ui

import scala.swing.*
import scala.swing.event.*
import javax.swing.border.*
import javax.swing.{Timer, UIManager}
import java.awt.{Dimension, Font}
import scala.collection.mutable.{ArrayBuffer, Queue}
import scala.io.Source
import scala.language.adhocExtensions   // enable extension of swing classes

import o1.adventure.Adventure


/**
 * Main Application object
 * Use this to run the game.
 */
object AdventureGUI extends SimpleSwingApplication:

  // neat font for the text areas
  val niceFont = new Font("Consolas", Font.PLAIN, 14)
  val bgcolor = new Color(0,0,0)
  val fgcolor = new Color(0,255,0)

  // enable modern GUI style
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)
  UIManager.put("Panel.background", bgcolor)

  var game = new Adventure()
  val messageBuffer = Queue[Char]()
  val commandBuffer = Queue[String]()
  val commandHistory = ArrayBuffer[String]("go up", "go down", "go right", "go left")

  // UI Components:

  val textDispaly = new TextArea():
    editable = false
    wordWrap = true
    lineWrap = true
    background = new Color(0,0,0, 0)
    foreground = fgcolor
    opaque = false
    preferredSize = new Dimension(600,600)
    font = niceFont
    focusable = false

  val inputField = new TextField():
    background = bgcolor
    foreground = fgcolor
    preferredSize = new Dimension(600,30)
    caret.color = new Color(200,200,200)
    font = niceFont

  val helpButton = new Button("Help"):
    contentAreaFilled = true
    focusable = false
    reactions += { case e: ButtonClicked => showHelp() }

  val canvas = new Canvas():
    contents += textDispaly
    contents += new FlowPanel(inputField, helpButton)
    border = new EmptyBorder(10,10,10,10)
    gameRef = Some(game)

  // main window definition:

  def top = new MainFrame:
    this.contents = canvas
    this.title = game.title
    this.pack()
    this.centerOnScreen()
    inputField.requestFocusInWindow()
    canvas.centerPlayerOnScreen()

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

          // retreaval hotkey
          case Key.Control if game.gameRunning =>
            game.player.retreat()

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

          // exit
          case Key.Escape =>
            quit()

          case _ =>
        }
    }
  end top

  // timer for ticking 30 times per second
  val tickTimer = new Timer(33, Swing.ActionListener { _ => this.onTick() })
  var prevFrameTime = System.currentTimeMillis()
  tickTimer.start()

  showMessage(game.welcomeMessage)


  private def restartGame() =
    game = new Adventure()
    canvas.gameRef = Some(game)
    canvas.centerPlayerOnScreen()
    messageBuffer.clear()
    commandBuffer.clear()
    showMessage("")
    inputField.text = ""
  end restartGame


  private def onTick() =
    val curTime = System.currentTimeMillis()

    if game.gameRunning then
      val deltaTime = (curTime - prevFrameTime) / 1000.0
      game.tick(deltaTime)
      canvas.keepPlayerOnScreen(deltaTime)

      if game.isComplete then
        // game is won
        showMessage(game.victoryMessage)
        game.gameRunning = false
        game.player.cheer = true
        game.timeOfCompletion = curTime

      else if game.isOver then
        // game is lost
        showMessage(game.gameOverMessage)
        game.gameRunning = false
        inputField.text = "restart"

      else if commandBuffer.nonEmpty then
        submitCommand(commandBuffer.dequeue())
    end if

    // type characters onto screen from the message buffer
    if this.messageBuffer.nonEmpty then
      textDispaly.text += this.messageBuffer.dequeue()

    // repaint graphics
    canvas.textToHighlight = textDispaly.text
    canvas.repaint()

    prevFrameTime = curTime
  end onTick


  private def submitCommand(command: String) =
    commandHistory.filterInPlace(_ != command)
    commandHistory += command
    if game.player.isMoving then
      commandBuffer += command
    else
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
        contents = new TextArea("(C) 2025 Elias Vesanen\n\n"):
          background = bgcolor
          foreground = new Color(100,200,255)
          font = niceFont
          editable = false
          border = new EmptyBorder(10,10,10,10)
          try
            val file = Source.fromFile("Adventure/help.txt")
            this.text += file.mkString
            file.close()
          catch
            case _ => Console.err.println("Failed to read help file")
  end showHelp


  // Enable this code to work even under the -language:strictEquality compiler option:
  private given CanEqual[Component, Component] = CanEqual.derived
  private given CanEqual[Key.Value, Key.Value] = CanEqual.derived

end AdventureGUI


/*
// for curiosity
@main def countCodeLines() =
  val files = Vector("ui/AdventureGUI", "ui/Canvas", "Adventure", "Agent", "Corridor", "Enemies", "Items", "Maze",
                     "MazeDefinition", "Neutrals", "Player", "Riddle", "Room")
  var total = 0
  for filename <- files do
    val file = Source.fromFile("Adventure/o1/adventure/" + filename + ".scala")
    val fileLines = file.getLines.length
    println(s"${filename.padTo(15,' ')}: $fileLines lines")
    total += fileLines
  println("\nTotal code lines: " + total)
*/
