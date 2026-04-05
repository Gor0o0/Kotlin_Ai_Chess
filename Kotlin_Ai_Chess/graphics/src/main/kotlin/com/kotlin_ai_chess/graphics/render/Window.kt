package com.kotlin_ai_chess.graphics.render

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.NULL

class Window(val width: Int, val height: Int, val title: String) {
    var handle: Long = NULL

    fun init() {
        if (!glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }

        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

        handle = glfwCreateWindow(width, height, title, NULL, NULL)
        if (handle == NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        glfwMakeContextCurrent(handle)
        glfwSwapInterval(1) // VSync
        glfwShowWindow(handle)

        GL.createCapabilities()
        
        // Базовая настройка OpenGL
        glEnable(GL_DEPTH_TEST)
        glClearColor(0.1f, 0.1f, 0.2f, 1.0f)
    }

    fun shouldClose(): Boolean = glfwWindowShouldClose(handle)

    fun update() {
        glfwSwapBuffers(handle)
        glfwPollEvents()
    }

    fun clear() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    }

    fun cleanup() {
        glfwDestroyWindow(handle)
        glfwTerminate()
    }
}
