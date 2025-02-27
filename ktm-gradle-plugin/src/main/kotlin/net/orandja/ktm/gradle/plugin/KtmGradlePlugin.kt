package net.orandja.ktm.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class KtmGradlePlugin : KotlinCompilerPluginSupportPlugin {
    private lateinit var configuration: KtmPluginConfiguration
    private lateinit var logger: Logger

    override fun apply(target: Project) {
        logger = target.logger
        configuration = target.extensions.create("ktm", KtmPluginConfiguration::class.java)
        super.apply(target)
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> =
        kotlinCompilation.target.project.providers.provider {
            listOf(
                SubpluginOption("loggingLevel", configuration.loggingLevel.name)
            )
        }

    override fun getCompilerPluginId(): String = "net.orandja.ktm.ktm-compiler-plugin"

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = "net.orandja.ktm",
        artifactId = "ktm-compiler-plugin",
        version = "2.0.0"
    )

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = configuration.enableFor(kotlinCompilation)
}