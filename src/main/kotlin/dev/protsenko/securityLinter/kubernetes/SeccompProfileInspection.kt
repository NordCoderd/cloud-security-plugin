package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.kubernetes.quickfix.ReplaceValueToRuntimeDefaultQuickFix
import dev.protsenko.securityLinter.utils.YamlPath
import org.jetbrains.yaml.psi.YAMLDocument
import org.jetbrains.yaml.psi.YAMLScalar

class SeccompProfileInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor =
        object : BaseKubernetesVisitor() {
            override fun analyze(
                specPrefix: String,
                document: YAMLDocument,
            ) {
                val seccompProfileType =
                    YamlPath.findByYamlPath(
                        "$specPrefix$SECCOMP_PROFILE_TYPE",
                        document,
                    ) as? YAMLScalar

                highlightIfProblem(seccompProfileType)

                val containers = containers(specPrefix, document)
                for (container in containers) {
                    val seccompProfileType =
                        YamlPath.findByYamlPath(
                            SECCOMP_PROFILE_PATH,
                            container,
                        ) as? YAMLScalar ?: continue

                    highlightIfProblem(seccompProfileType)
                }
            }

            private fun highlightIfProblem(seccompProfileType: YAMLScalar?) {
                if (seccompProfileType != null && seccompProfileType.textValue !in allowedProfiles) {
                    val descriptor =
                        HtmlProblemDescriptor(
                            seccompProfileType,
                            SecurityPluginBundle.message("kube010.documentation"),
                            SecurityPluginBundle.message("kube010.problem-text"),
                            ProblemHighlightType.ERROR,
                            arrayOf(
                                ReplaceValueToRuntimeDefaultQuickFix(
                                    SecurityPluginBundle.message("kube010.qf.fix-value"),
                                ),
                            ),
                        )

                    holder.registerProblem(descriptor)
                }
            }
        }
}

private const val SECCOMP_PROFILE_PATH = "securityContext.seccompProfile.type"
private const val SECCOMP_PROFILE_TYPE = "spec.$SECCOMP_PROFILE_PATH"
private val allowedProfiles = setOf("RuntimeDefault", "Localhost")
