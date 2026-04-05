package com.kotlin_ai_chess.graphics.render

import org.lwjgl.opengl.GL20.*

class ShaderManager {
    private var programId: Int = 0

    fun createShader(vertexCode: String, fragmentCode: String) {
        programId = glCreateProgram()
        if (programId == 0) {
            throw Exception("Could not create Shader program")
        }

        val vertexShaderId = compileShader(vertexCode, GL_VERTEX_SHADER)
        val fragmentShaderId = compileShader(fragmentCode, GL_FRAGMENT_SHADER)

        glAttachShader(programId, vertexShaderId)
        glAttachShader(programId, fragmentShaderId)

        glLinkProgram(programId)
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024))
        }

        glDetachShader(programId, vertexShaderId)
        glDetachShader(programId, fragmentShaderId)

        glValidateProgram(programId)
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024))
        }
    }

    private fun compileShader(shaderCode: String, shaderType: Int): Int {
        val shaderId = glCreateShader(shaderType)
        if (shaderId == 0) {
            throw Exception("Error creating shader. Type: $shaderType")
        }

        glShaderSource(shaderId, shaderCode)
        glCompileShader(shaderId)

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024))
        }

        return shaderId
    }

    fun bind() {
        glUseProgram(programId)
    }

    fun unbind() {
        glUseProgram(0)
    }

    fun cleanup() {
        unbind()
        if (programId != 0) {
            glDeleteProgram(programId)
        }
    }

    fun setUniform(name: String, value: Int) {
        val location = glGetUniformLocation(programId, name)
        glUniform1i(location, value)
    }

    fun setUniformVector3f(name: String, vector: org.joml.Vector3f) {
        val location = glGetUniformLocation(programId, name)
        glUniform3f(location, vector.x, vector.y, vector.z)
    }

    // Для матриц 4x4 (будет нужно для камеры и моделей)
    fun setUniformMatrix4fv(name: String, matrix: FloatArray) {
        val location = glGetUniformLocation(programId, name)
        glUniformMatrix4fv(location, false, matrix)
    }
}
