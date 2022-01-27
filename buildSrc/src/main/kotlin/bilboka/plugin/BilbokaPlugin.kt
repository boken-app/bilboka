package bilboka.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

open class BilbokaPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("Configuring dependencies for project ${project.name}...")
        project.configureDependencies()
        println("Configuring plugins for project ${project.name}...")
        project.configurePlugins()
    }
}
