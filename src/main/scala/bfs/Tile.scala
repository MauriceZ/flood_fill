package flood

import org.scalajs.jquery.JQuery

class Tile($elem: JQuery, tileCoords: (Int, Int)) {
  var color = ""
  val $domElem = $elem
  val coords = tileCoords

  def changeColor(newColor: String) {
    color = newColor
    $domElem.css("background-color", newColor)
  }

  def resetColor() {
    color = ""
    $domElem.css("background-color", "")
  }
}
