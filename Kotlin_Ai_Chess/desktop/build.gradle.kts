
plugins {
    kotlin("jvm")
    application // Apply the application plugin for executable JARs
}

val lwjglVersion = "3.3.3"
val lwjglNatives = "natives-windows" // Можно настроить для других ОС

dependencies {
    // Depend on our modules
    implementation(project(":core"))
    implementation(project(":engine"))
    implementation(project(":ai"))
    implementation(project(":graphics"))
    implementation(project(":shared"))

    // LWJGL core
    implementation("org.lwjgl:lwjgl:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-assimp:$lwjglVersion") // Для загрузки моделей

    // LWJGL natives
    runtimeOnly("org.lwjgl:lwjgl:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-glfw:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-opengl:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-assimp:$lwjglVersion:$lwjglNatives")

    // Math library for OpenGL
    implementation("org.joml:joml:1.10.5")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("com.kotlin_ai_chess.desktop.MainKt") // Set the main class for the desktop application
}
