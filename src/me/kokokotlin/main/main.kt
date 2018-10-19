package me.kokokotlin.main

import me.kokokotlin.main.graph.Node
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.HashSet
import kotlin.math.roundToInt


/**
 *
 * @author: kokokotlin
 *
 *  simple visualisation of a star performed on a graph
 *
 *  youtube channel: https://www.youtube.com/channel/UC7nacURgPqTgcR074eABZJg
 *
 */

// saves all nodes
val nodes: MutableList<Node> = CopyOnWriteArrayList()

// the node that is currently selected by the user
var selectedNode: Node? = null

// selected starting and end point of the graph
var start: Node? = null
var end: Node? = null

// scaling factor of the distances of the graph -> for simplification that the values aren't that big
const val SCALING_FACTOR = 20.0

fun main(args: Array<String>) {
    Canvas() // -> starts the program by creating an instance of the Canvas class where all the logic lives
}

// function that implements the a star pathfinding algorithm
// it finds the shortest path between the start and end node of a given graph
// more on: https://en.wikipedia.org/wiki/A*_search_algorithm
fun a_star(start: Node, end: Node) {

    // priority queue where all the known nodes will be saved
    // nodes are known if a path to them is known
    // this known path must not be the shortest so the priority queue sorts
    // the nodes by the sum of the length needed of the way the you must travel
    // to get to them and the estimated path from the node to the end node
    // that makes sure that only nodes are process to which to shortest path is known
    val openList: PriorityQueue<Node> = PriorityQueue(10) { n1, n2 ->
        Integer.compare(n1.f.roundToInt(), n2.f.roundToInt())
    }

    // the closedList saves the nodes that are already processed
    val closedList: MutableSet<Node> = HashSet(10)

    // at first you put the start node in the open list
    openList.add(start)

    // make sure that there are nodes to process in the open List or there is no solution to the graph,
    // because start and end aren't connected
    while (!openList.isEmpty()) {

            // get the first node of the queue
            val node = openList.poll()

            // if it's the specified end node -> stop the algorithm because the shortest path is now known
            if (node == end) {
                break
            }

            // get all neighbors from the current node
            val nodes = node.neighbors.keys

            for (n in nodes) {

                // if the current neighbor is already in the openList -> you should only update it's parent
                // pointer and it's f - value but since the priorityQueue has no update method you have to remove
                // it and add it again to make sure it's put in the right location
                if(n in openList) {
                    openList.remove(n)
                }

                // if the the neighbor is in the closed list -> stop processing it,
                // because the shortest path is already known
                if (n !in closedList) {
                    // else -> update the parent pointer to the currently processed node
                    n.parent = node
                    // calculate the f - value
                    n.calcF(end)
                    // and put the node in the openList
                    openList.add(n)
                }
            }
            // at the end put the currently processed node in the closedList
            // since the shortest path to it is known and there is no need
            // to process it again
            closedList.add(node)
    }


    // after the algorithm -> trace back the parent pointers from start to end
    // and highlight all path segments that belong to the shortest path
    // if you need the path afterwards -> pay attention because the found path is
    // backwards from end to start because we trace back the parent pointers
    var node = end
    while (node.parent != null) {
        node.neighbors[node.parent as Node]!!.highlighted = true
        node.parent!!.neighbors[node]!!.highlighted = true
        node = node.parent as Node
    }

}