package run.packer

import scala.None
import collection.SortedSet
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File
import java.awt.Color


class BadPlan extends PackingPlan {

    private var currentWidth, currentHeight = 0
    private var pointRows, pointCols = collection.mutable.Map[Int, SortedSet[Point]]()
    private var boxes = List[Box]()
    private val startPoint = AnchorPoint((0,0), (None,None))

    val (maxWidth, maxHeight) = (1024, 1024)
    def atlasWidth = nextDim(currentWidth, maxWidth)
    def atlasHeight = nextDim(currentHeight, maxHeight)
    private def nextDim(x:Int, maximum:Int):Int = {
      var p = 1
      def v = math.pow(2,p)
      while(v < x && v < maximum) p += 1
      v.toInt
    }
    addPoint(startPoint)

    trait Point {
      val position : (Int, Int)
      val neighbors : (Option[Box], Option[Box])
      val (x, y) = position
      val (neighborX, neighborY) = neighbors
      def row = pointRows(y)
      def col = pointCols(x)
      def rowPairs = row.grouped(2)
      def colPairs = col.grouped(2)
    }

    case class ControlPoint(position:(Int,Int), neighbors:(Option[Box], Option[Box])) extends Point {

    }

    case class AnchorPoint(position:(Int,Int), neighbors:(Option[Box], Option[Box])) extends Point {
      def size = {
        val nextInRow = row.dropWhile(_ != this).drop(1).headOption
        val nextInCol = col.dropWhile(_ != this).drop(1).headOption
        nextInRow map { p => if(p.isInstanceOf[AnchorPoint]) throw new Exception("AnchorPoint found when expecting ControlPoint")}
        nextInCol map { p => if(p.isInstanceOf[AnchorPoint]) throw new Exception("AnchorPoint found when expecting ControlPoint")}
        (nextInRow.map(_.x - x), nextInCol.map(_.y - y))
      }
      def fitScore(image:BufferedImage) = {
        val (imWidth, imHeight) = (image.getWidth, image.getHeight)
        val W = currentWidth - x
        val H = currentHeight - y
        val score = this.size match {
          case (Some(w), Some(h)) =>
            if(imWidth > w || imHeight > h) -1
            else 0
          case (Some(w), None) =>
            if(imWidth > w) -1
            else {
              if(imHeight > H) imHeight - H
              else 0
            }
          case (None, Some(h)) =>
            if(imHeight > h) -1
            else {
              if(imWidth > W) imWidth - W
              else 0
            }
          case (None,None) =>
            var s = 0
            if(imWidth > W) s += imWidth - W
            if(imHeight > H) s += imHeight - H
            s
        }
        score
      }
    }

    case class Box(position:(Int,Int), image:BufferedImage) {

      val (width, height) = (image.getWidth, image.getHeight)
      val (x0, y0) = position
      val (x1, y1) = (x0 + width, y0 + height)

//      override def toString = "Box((%s,%s)->(%s,%s))".format(x0,y0,x1,y1,width,height)
      override def toString = "Box(%s,%s)".format(width,height)
    }

    def sortedPoints = {
      for {
        (_, row) <- pointRows.toSeq.sortBy(_._1)
        point <- row
      } yield point
    }

    def addImage(image:BufferedImage) = {
      println("adding image ----------------------")
      var shortStop = false
      val scores = (for {
        point <- sortedPoints
        if !shortStop
      } yield {
        point match {
          case anchor:AnchorPoint =>
            if (anchor == startPoint) {
              shortStop = true
              Some((0, anchor))
            }
            else {
              val score = anchor.fitScore(image)
              if(score < 0) None
              else {
                if(score == 0) shortStop = true
                Some((score, anchor))
              }
            }
          case _:ControlPoint => None
        }
      }).flatten

      if(scores.isEmpty) {
        false
      }
      else {
        println(scores)
        val anchor = scores.minBy(_._1)._2
        anchorImage(image, anchor)
        println("@ anchor: " + anchor.position)
        true
      }
    }

    def writeState(label:String, debug:Boolean = false) = {
      val buf = new BufferedImage(atlasWidth,atlasHeight,BufferedImage.TYPE_INT_ARGB)
      val g = buf.getGraphics
      for ((box) <- boxes) {
        g.drawImage(box.image, box.x0, box.y0, null)
      }
      if(debug) {
        for {
          point <- sortedPoints
        } {
          point match {
            case _:ControlPoint =>
              g.setColor(Color.white)
              g.fillRect(point.x, point.y, 8, 8)
            case anchor:AnchorPoint =>
              anchor.size match {
                case (Some(w), Some(h)) =>
                  g.setColor(Color.black)
                  g.fillRect(point.x, point.y, w, h)
                  g.setColor(Color.white)
                  g.drawRect(point.x, point.y, w, h)
                case (Some(w), None) =>
                  g.setColor(Color.pink)
                  g.fillRect(point.x, point.y, w, 16)
                case (None, Some(h)) =>
                  g.setColor(Color.pink)
                  g.fillRect(point.x, point.y, 16, h)
                case (None, None) =>
                  g.setColor(Color.pink)
                  g.fillRect(point.x, point.y, 16, 16)
              }
              g.setColor(Color.black)
              g.fillRect(point.x, point.y, 8, 8)
          }
        }
      }
      ImageIO.write(buf, "png", new File("src/main/resources/img/output/out-%s.png".format(label)))
    }

    def createAtlas() {
      val buf = new BufferedImage(atlasWidth,atlasHeight,BufferedImage.TYPE_INT_ARGB)
      val g = buf.getGraphics
      for ((box) <- boxes) {
        g.drawImage(box.image, box.x0, box.y0, null)
      }
      ImageIO.write(buf, "png", new File("src/main/resources/img/output/OUT.png"))
    }

    def anchorImage(image:BufferedImage, anchor:AnchorPoint) {
      val newBox = Box(anchor.position, image)
      anchor.neighborX match {
        case Some(box) =>
          (box.y1 compare newBox.y1) match {
            case 1 =>
              addPoint(AnchorPoint((newBox.x0, newBox.y1), (Some(box), Some(newBox))))
            case -1 =>
              addPoint(ControlPoint((box.x1, box.y1), (Some(box), Some(newBox))))
              pointCols
            case 0 =>
          }
        case None => addPoint(AnchorPoint((newBox.x0, newBox.y1), (None, Some(newBox))))
      }
      anchor.neighborY match {
        case Some(box) =>
          (box.x1 compare newBox.x1) match {
            case 1 =>
              addPoint(AnchorPoint((newBox.x1, newBox.y0), (Some(box), Some(newBox))))
            case -1 =>
              addPoint(ControlPoint((box.x1, box.y1), (Some(box), Some(newBox))))
            case 0 =>
          }
        case None => addPoint(AnchorPoint((newBox.x1, newBox.y0), (Some(newBox), None)))
      }
      addBox(newBox)
      removePoint(anchor)
      writeState(boxes.size.toString, true)
    }

    def addPoint(p:Point) {
      val rowOrdering = new Ordering[Point] {
        def compare(a:Point, b:Point) = {
          a.x compare b.x
        }
      }
      val colOrdering = new Ordering[Point] {
        def compare(a:Point, b:Point) = {
          a.y compare b.y
        }
      }
      def emptyRows = SortedSet()(rowOrdering)
      def emptyCols = SortedSet()(colOrdering)

      if(!pointRows.isDefinedAt(p.y)) pointRows += (p.y) -> emptyRows
      if(!pointCols.isDefinedAt(p.x)) pointCols += (p.x) -> emptyCols

      val row = pointRows(p.y)
      val col = pointCols(p.x)
      pointRows(p.y) = row.filter(_.x != p.x)
      pointCols(p.x) = col.filter(_.y != p.y)
//      col.filter(_.y == p.y).map( pointCols(p.x) -= _ )

      pointRows(p.y) += p
      pointCols(p.x) += p

    }

    def removePoint(p:Point) {
      pointRows(p.y) -= p
      pointCols(p.x) -= p
    }

    private def addBox(box:Box) {
      boxes ::= box
      if(box.x1 > currentWidth) currentWidth = box.x1
      if(box.y1 > currentHeight) currentHeight = box.y1
    }
  }