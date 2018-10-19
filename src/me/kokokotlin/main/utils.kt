package me.kokokotlin.main

import me.kokokotlin.main.graph.Node
import kotlin.math.sqrt


// calculates the distance between 2 nodes using pythagoras theorem
fun dist(n1: Node, n2: Node) = sqrt(Math.pow((n1.x - n2.x), 2.0) + Math.pow((n1.y - n2.y), 2.0)) / SCALING_FACTOR