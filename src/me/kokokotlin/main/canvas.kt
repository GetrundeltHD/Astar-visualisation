package me.kokokotlin.main

import me.kokokotlin.main.graph.Node
import java.awt.*
import java.awt.Canvas
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.JFrame
import kotlin.math.roundToInt

const val NODE_SIZE = 75
const val INFINITY = 0x221E.toChar()

class Canvas : Canvas(), Runnable {

    /**
     *   this class is only used for visualisation purposes, contains mostly drawing code
     *   and implements the user interaction
     *   and has no connection to the a star algorithm so it's not documented
     */

    val thread = Thread(this)
    val window = JFrame("A*")

    init {
        window.setSize(1000, 1000)
        window.setLocationRelativeTo(null)
        window.isResizable = false
        window.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        window.add(this)

        thread.name = "drawing"

        window.isVisible = true

        addKeyListener(object : KeyListener {
            override fun keyTyped(e: KeyEvent?) {
            }

            override fun keyPressed(e: KeyEvent?) {

                if (e == null) return

                val keyCode = e.keyCode

                when (keyCode) {
                    KeyEvent.VK_S -> {
                        if (selectedNode == null || selectedNode == end) return
                        start = selectedNode as Node
                    }

                    KeyEvent.VK_E -> {
                        if (selectedNode == null || selectedNode == start) return
                        end = selectedNode as Node
                    }

                    KeyEvent.VK_BACK_SPACE -> {
                        if (nodes.size > 0)
                            nodes.removeAt(nodes.lastIndex)
                    }

                    KeyEvent.VK_A -> {
                        if(start != null && end != null) {

                            for(node in nodes) {
                                node.g = .0
                                node.f = .0
                                for(p in node.neighbors) {
                                    p.value.highlighted = false
                                }
                            }

                            a_star(start as Node, end as Node)
                        }

                    }
                }

            }

            override fun keyReleased(e: KeyEvent?) {
            }
        })

        addMouseListener(object : MouseListener {
            override fun mouseReleased(e: MouseEvent?) {
            }

            override fun mouseEntered(e: MouseEvent?) {
            }

            override fun mouseClicked(e: MouseEvent?) {
            }

            override fun mouseExited(e: MouseEvent?) {
            }

            override fun mousePressed(e: MouseEvent?) {

                if (e == null) return

                val x = e.x
                val y = e.y

                when (e.button) {
                    MouseEvent.BUTTON1 -> nodes.add(Node(x.toDouble() - NODE_SIZE / 2,
                            y.toDouble() - NODE_SIZE / 2,
                            mutableMapOf(),
                            null))
                    MouseEvent.BUTTON3 -> {

                        if (selectedNode == null) {
                            for (node in nodes) {
                                if (node.hitbox.contains(Point(x, y))) {
                                    selectedNode = node
                                    return
                                    return
                                }
                            }

                            selectedNode = null
                        } else {
                            for (node in nodes) {
                                if (node.hitbox.contains(Point(x, y))) {

                                    if (selectedNode == node) {
                                        selectedNode = null
                                        return
                                    }

                                    val n = selectedNode as Node
                                    n.addNeighbor(node)
                                    node.addNeighbor(n)
                                    selectedNode = null
                                    return
                                }
                            }

                            selectedNode = null

                        }
                    }
                }
            }
        })

        createBufferStrategy(2)
        thread.start()
        window.requestFocus()
    }

    override fun run() {

        while (true) {
            draw()
            Thread.sleep(10)
        }

    }

    fun draw() {

        val graphics = bufferStrategy.drawGraphics
        val graphics2d = graphics as Graphics2D
        graphics2d.addRenderingHints(RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON))

        graphics.font = graphics.font.deriveFont(20f)
        graphics.font = graphics.font.deriveFont(Font.BOLD)
        graphics.color = Color.BLACK
        graphics.fillRect(0, 0, width, height)

        for (node in nodes) {

            for (p in node.neighbors) {
                graphics.color = Color.WHITE

                val path = p.value

                val start = path.start
                val end = path.end

                graphics.stroke = BasicStroke(5f)
                if (path.highlighted) graphics.color = Color.BLUE
                else graphics.color = Color.WHITE

                graphics.drawLine(start.x, start.y, end.x, end.y)

                val offSetX = (path.start.x + path.end.x) / 2
                val offSetY = (path.start.y + path.end.y) / 2

                graphics.color = Color.RED
                graphics.drawString(String.format("%.2f", p.value.weight),
                        offSetX,
                        offSetY)
            }
        }

        graphics.color = Color.WHITE
        graphics.font = graphics.font.deriveFont(16f)
        for (node in nodes) {

            graphics.color = Color.WHITE
            graphics.fillOval(node.x.toInt(), node.y.toInt(), NODE_SIZE, NODE_SIZE)

            graphics.color = Color.RED
            graphics.drawString("f: ${if(node.f == .0) INFINITY.toString() else String.format("%.0f", node.f)}",
                    node.x.roundToInt() + NODE_SIZE / 8,
                    node.y.roundToInt() + NODE_SIZE / 2)

            graphics.drawString("g: ${if(node.g == .0) INFINITY.toString() else String.format("%.0f", node.g)}",
                    node.x.roundToInt() + NODE_SIZE / 8,
                    node.y.roundToInt() + NODE_SIZE / 2 + 15)

        }

        graphics.font = graphics.font.deriveFont(20f)

        graphics.color = Color.YELLOW
        graphics.stroke = BasicStroke(5f)
        if (selectedNode != null) {
            val n = selectedNode as Node
            graphics.drawOval(n.x.toInt(), n.y.toInt(), NODE_SIZE, NODE_SIZE)
        }

        if (start != null) {
            val s = start as Node
            graphics.color = Color.RED
            graphics.drawString("Start: x(${s.x}) y(${s.y})", 10, 30)
            graphics.stroke = BasicStroke(5f)
            graphics.color = Color.GREEN
            graphics.drawOval(s.x.roundToInt(), s.y.roundToInt(), NODE_SIZE, NODE_SIZE)
        } else {
            graphics.drawString("Start: null", 10, 30)
        }

        if (end != null) {
            val s = end as Node
            graphics.color = Color.RED
            graphics.drawString("End: x(${s.x}) y(${s.y})", 10, 60)
            graphics.stroke = BasicStroke(5f)
            graphics.color = Color(150, 150, 255)
            graphics.drawOval(s.x.roundToInt(), s.y.roundToInt(), NODE_SIZE, NODE_SIZE)
        } else {
            graphics.drawString("End: null", 10, 60)
        }

        graphics.dispose()
        bufferStrategy.show()

    }

}