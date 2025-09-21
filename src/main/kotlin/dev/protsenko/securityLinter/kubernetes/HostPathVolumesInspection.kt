package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.utils.YamlPath
import org.jetbrains.yaml.psi.YAMLDocument
import org.jetbrains.yaml.psi.YAMLMapping
import org.jetbrains.yaml.psi.YAMLSequence

class HostPathVolumesInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor {
        return object : BaseKubernetesVisitor() {
            override fun analyze(
                specPrefix: String,
                document: YAMLDocument,
            ) {
                val specVolumes =
                    YamlPath.findByYamlPath("${specPrefix}spec.volumes", document) as? YAMLSequence ?: return

                for (volume in specVolumes.items) {
                    val volumeValue = volume.value as? YAMLMapping ?: continue
                    volumeValue.getKeyValueByKey("hostPath") ?: continue

                    val descriptor =
                        HtmlProblemDescriptor(
                            volumeValue,
                            SecurityPluginBundle.message("kube005.documentation"),
                            SecurityPluginBundle.message("kube005.problem-text"),
                            ProblemHighlightType.ERROR,
                            emptyArray(),
                        )
                    holder.registerProblem(descriptor)
                }
            }
        }
    }
}
