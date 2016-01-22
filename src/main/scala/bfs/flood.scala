package flood

import scala.scalajs.js.JSApp
import scala.scalajs.js.timers.setTimeout
import org.scalajs.dom.document
import org.scalajs.jquery.jQuery
import org.scalajs.jquery.JQuery
import org.scalajs.jquery.JQueryEventObject

object FloodFill extends JSApp {

  var speed = 200
  val newColor = "#FF0000"

  def main(): Unit = {
    val numTilesPerRow = 38
    val tiles = generateTiles(numTilesPerRow, numTilesPerRow)

    jQuery(".tile").on("click", (e: JQueryEventObject) => {
      val $tileElem = jQuery(e.currentTarget)

      val (row, col) = getCoords($tileElem)
      val tile = tiles(row)(col)

      colorMatchingTiles(tiles, tile :: Nil)
    })

    jQuery(document).on("mousedown", () => {
      jQuery(".tile").on("mouseover", (e: JQueryEventObject) => {
        val $tileElem = jQuery(e.currentTarget)

        val (row, col) = getCoords($tileElem)
        val tile = tiles(row)(col)
        tile.changeColor(newColor)
      })
    })

    jQuery(document).on("mouseup", () => {
      jQuery(".tile").off("mouseover")
    })

    jQuery("#speed-switch").on("change", (e: JQueryEventObject) => {
      speed = jQuery(e.currentTarget).`val`().toString().toInt // converting to int first not supported...
    })

    jQuery("#clear-btn").on("click", () => { tiles.foreach(_.foreach(_.resetColor())) })
  }

  def colorMatchingTiles(allTiles: Array[Array[Tile]], tiles: List[Tile]): Unit = {
    val nextColor = newColor

    tiles match {
      case tile :: tail => {

        val prevColor = tile.color

        if (prevColor == nextColor)
          return
        else
          tile.changeColor(nextColor)

        val filteredTail = tail.filter((curTile: Tile) => curTile.color == prevColor) // remove the tiles that were colored by the previous call
        colorMatchingTiles(allTiles, filteredTail)

        val neighbors = getTileNeighbors(allTiles, tile)
        val sameColorNeighbors = neighbors.filter((curTile: Tile) => curTile.color == prevColor)

        setTimeout(speed) { colorMatchingTiles(allTiles, sameColorNeighbors) }
        
      } case Nil => None
    }

  }

  def getTileNeighbors(tiles: Array[Array[Tile]], tile: Tile): List[Tile] = {
    val (row, col) = tile.coords

    val neighborCoords = List(
      (row - 1, col),
      (row + 1, col),
      (row, col + 1),
      (row, col - 1)
    )

    def isWithinRange(c: (Int, Int)) = // makes sure the coords are within the array bounds
      c._1 >= 0 && c._2 >= 0 && c._1 < tiles.length && c._2 < tiles(0).length

    neighborCoords.filter(isWithinRange).map { case (i, j) => tiles(i)(j) }
  }

  def generateTiles(rows: Int, cols: Int) = {
    val tiles = Array.ofDim[Tile](rows, cols)

    for (i <- 0 until tiles.length) {
      val tileArray = tiles(i)

      for (j <- 0 until tileArray.length) {
        val $tileElem = jQuery(s"<div class='tile' data-coords='$i,$j'></div>")
        val tile = new Tile($tileElem, (i, j))

        tileArray(j) = tile
        jQuery("#grid").append($tileElem)
      }
    }

    tiles
  }

  def getCoords($tileElem: JQuery) = {
    val coords = $tileElem.data("coords").toString().split(",")
    val Array(row, col) = coords.map(_.toInt)
    (row, col)
  }

}
