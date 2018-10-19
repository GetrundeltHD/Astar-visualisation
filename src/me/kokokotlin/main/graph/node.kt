package me.kokokotlin.main.graph

import me.kokokotlin.main.NODE_SIZE
import me.kokokotlin.main.dist
import me.kokokotlin.main.start
import java.awt.Point
import java.awt.Polygon
import java.awt.Rectangle
import kotlin.math.abs
import kotlin.math.sqrt

class Node(val x: Double, val y: Double, val neighbors: MutableMap<Node, Path>,
           var parent: Node?) {

    /*
       class that represents a node of the graph for the a star algorithm
     */

    // sum of the cost from start to this node and the estimated cost from this node to the end
    var f = .0

    // cost from start to this node
    var g = .0

    val hitbox: Rectangle
        get() = Rectangle(x.toInt(), y.toInt(), NODE_SIZE, NODE_SIZE)

    // calculates the f - value for the current
    fun calcF(end: Node) {
        g = calcG()
        f = huristic(end) + g
    }

    // calculates the cost from the start to get to this node
    // by adding the cost to get to the parent + the cost to get from the parent to this node
    private fun calcG() = (neighbors[parent]?.weight
            ?: throw IllegalStateException("Node that should calculate g has no parent")) + (parent?.g
            ?: throw IllegalStateException("Node that should calculate g has no parent"))

    // calculates the huristic by using bee line
    private fun huristic(end: Node) = dist(end, this)

    fun addNeighbor(n: Node) {
        if (y < n.y)
            neighbors[n] = Path(this, n, dist(n, this), false)
        else
            neighbors[n] = Path(n, this, dist(n, this), false)
    }
}

data class Path(val n1: Node, val n2: Node, var weight: Double, var highlighted: Boolean) {

    /*
        represents a path between 2 nodes of a graph
     */

    init {
        highlighted = false
    }

    val start: Point
        get() = Point(n1.x.toInt() + NODE_SIZE / 2, n1.y.toInt() + NODE_SIZE / 2)


    val end: Point
        get() = Point(n2.x.toInt() + NODE_SIZE / 2, n2.y.toInt() + NODE_SIZE / 2)

    fun getHitbox(): Polygon {
        val p = Polygon()

        val dx = start.x / end.x
        val dy = start.x / end.y

        val xOff = if (abs(dx - dy) < 1) 0 else 5

        p.addPoint(start.x - xOff, start.y - 5)
        p.addPoint(start.x + xOff, start.y + 5)
        p.addPoint(end.x + xOff, end.y + 5)
        p.addPoint(end.x - xOff, end.y - 5)

        return p
    }


}