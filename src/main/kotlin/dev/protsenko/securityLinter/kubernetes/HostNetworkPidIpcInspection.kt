package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.kubernetes.quickfix.ReplaceValueToFalseQuickFix
import dev.protsenko.securityLinter.utils.YamlPath
import org.jetbrains.yaml.psi.YAMLDocument
import org.jetbrains.yaml.psi.YAMLScalar

class HostNetworkPidIpcInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : BaseKubernetesVisitor() {
            override fun analyze(specPrefix: String, document: YAMLDocument) {
                for (keyToVerify in specKeysToVerify) {
                    val key = YamlPath.findByYamlPath("${specPrefix}spec.$keyToVerify", document) ?: continue

                    if (key !is YAMLScalar) continue
                    val booleanValue = key.textValue.toBooleanStrictOrNull()

                    if (booleanValue != false) {
                        val descriptor = HtmlProblemDescriptor(
                            key,
                            SecurityPluginBundle.message("kube003.documentation"),
                            SecurityPluginBundle.message("kube003.host-network-pid-ipc"),
                            ProblemHighlightType.ERROR, arrayOf(
                                ReplaceValueToFalseQuickFix(
                                    SecurityPluginBundle.message("kube003.qf.fix-value", keyToVerify)
                                )
                            )
                        )
                        holder.registerProblem(descriptor)
                    }
                    continue
                }
            }
        }
    }
}

private val specKeysToVerify = listOf("hostNetwork", "hostPID", "hostIPC")