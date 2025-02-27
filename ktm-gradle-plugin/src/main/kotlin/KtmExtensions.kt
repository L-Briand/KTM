import net.orandja.ktm.gradle.plugin.KtmPluginConfiguration
import org.gradle.api.Project

fun Project.ktm(block: KtmPluginConfiguration.() -> Unit) =
    extensions.configure("ktm", block)