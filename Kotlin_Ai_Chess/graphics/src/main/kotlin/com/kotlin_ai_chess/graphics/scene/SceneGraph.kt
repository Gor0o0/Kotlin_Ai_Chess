package com.kotlin_ai_chess.graphics.scene

import com.kotlin_ai_chess.shared.math.Vector3

interface Renderable {
    fun render()
}

class Scene {
    private val nodes = mutableListOf<Node>()

    fun addNode(node: Node) {
        nodes.add(node)
    }

    fun removeNode(node: Node) {
        nodes.remove(node)
    }

    fun renderAll() {
        nodes.forEach { it.render() }
    }
}

class Node(
    val name: String,
    var transform: Transform = Transform(),
    val children: MutableList<Node> = mutableListOf()
) : Renderable {

    var model: GltfModel? = null

    override fun render() {
        // Здесь будет логика отрисовки через графический API
        model?.let {
            // println("Rendering model: ${it.path} at ${transform.position}")
        }
        children.forEach { it.render() }
    }
}

data class GltfModel(
    val path: String,
    var isLoaded: Boolean = false,
    val isGlb: Boolean = true
)

data class Transform(
    var position: Vector3 = Vector3(0f, 0f, 0f),
    var rotation: Vector3 = Vector3(0f, 0f, 0f),
    var scale: Vector3 = Vector3(1f, 1f, 1f)
)
