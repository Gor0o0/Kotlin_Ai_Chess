package com.kotlin_ai_chess.graphics.render

import org.lwjgl.assimp.*
import org.lwjgl.assimp.Assimp.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*
import java.nio.IntBuffer

class ModelLoader {
    
    data class Mesh(
        val vaoId: Int,
        val vertexCount: Int,
        val vboIds: List<Int>
    )

    fun loadModel(filePath: String): List<Mesh> {
        val scene = aiImportFile(filePath, aiProcess_Triangulate or aiProcess_FlipUVs)
            ?: throw Exception("Error loading model: " + aiGetErrorString())

        val meshes = mutableListOf<Mesh>()
        val numMeshes = scene.mNumMeshes()
        val sceneMeshes = scene.mMeshes()

        for (i in 0 until numMeshes) {
            val aiMesh = AIMesh.create(sceneMeshes!![i])
            meshes.add(processMesh(aiMesh))
        }

        aiReleaseImport(scene)
        return meshes
    }

    private fun processMesh(aiMesh: AIMesh): Mesh {
        val vertices = mutableListOf<Float>()
        val textures = mutableListOf<Float>()
        val normals = mutableListOf<Float>()
        val indices = mutableListOf<Int>()

        // Обработка вершин
        val aiVertices = aiMesh.mVertices()
        while (aiVertices.hasRemaining()) {
            val aiVertex = aiVertices.get()
            vertices.add(aiVertex.x())
            vertices.add(aiVertex.y())
            vertices.add(aiVertex.z())
        }

        // Обработка нормалей
        val aiNormals = aiMesh.mNormals()
        if (aiNormals != null) {
            while (aiNormals.hasRemaining()) {
                val aiNormal = aiNormals.get()
                normals.add(aiNormal.x())
                normals.add(aiNormal.y())
                normals.add(aiNormal.z())
            }
        }

        // Обработка индексов (полигонов)
        val numFaces = aiMesh.mNumFaces()
        val aiFaces = aiMesh.mFaces()
        for (i in 0 until numFaces) {
            val aiFace = aiFaces.get(i)
            val buffer = aiFace.mIndices()
            while (buffer.hasRemaining()) {
                indices.add(buffer.get())
            }
        }

        return createMesh(vertices.toFloatArray(), indices.toIntArray(), normals.toFloatArray())
    }

    private fun createMesh(positions: FloatArray, indices: IntArray, normals: FloatArray): Mesh {
        val vaoId = glGenVertexArrays()
        glBindVertexArray(vaoId)

        val vboIds = mutableListOf<Int>()

        // Position VBO
        val posVboId = glGenBuffers()
        vboIds.add(posVboId)
        glBindBuffer(GL_ARRAY_BUFFER, posVboId)
        glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)
        glEnableVertexAttribArray(0)

        // Index VBO
        val idxVboId = glGenBuffers()
        vboIds.add(idxVboId)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboId)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)

        // Normals VBO
        val normVboId = glGenBuffers()
        vboIds.add(normVboId)
        glBindBuffer(GL_ARRAY_BUFFER, normVboId)
        glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW)
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0)
        glEnableVertexAttribArray(1)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)

        return Mesh(vaoId, indices.size, vboIds)
    }
    
    fun cleanup(meshes: List<Mesh>) {
        for (mesh in meshes) {
            glDisableVertexAttribArray(0)
            glDisableVertexAttribArray(1)

            glBindBuffer(GL_ARRAY_BUFFER, 0)
            for (vboId in mesh.vboIds) {
                glDeleteBuffers(vboId)
            }

            glBindVertexArray(0)
            glDeleteVertexArrays(mesh.vaoId)
        }
    }
}
