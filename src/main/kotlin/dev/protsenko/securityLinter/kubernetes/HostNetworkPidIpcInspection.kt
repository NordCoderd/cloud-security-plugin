package dev.protsenko.securityLinter.kubernetes

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.kubernetes.quickfix.ReplaceValueToFalseQuickFix
import dev.protsenko.securityLinter.kubernetes.utils.KubernetesConstants.evaluateSpecPrefix
import dev.protsenko.securityLinter.kubernetes.utils.KubernetesConstants.supportedKinds
import dev.protsenko.securityLinter.utils.YamlPath
import org.jetbrains.yaml.YAMLUtil
import org.jetbrains.yaml.psi.*

class HostNetworkPidIpcInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PsiElementVisitor() {
            override fun visitFile(file: PsiFile) {
                if (file !is YAMLFile) return

                val documents = file.documents

                for (document in documents) {
                    val kind = YAMLUtil.getQualifiedKeyInDocument(document, listOf("kind")) ?: return
                    val kindValue = kind.valueText
                    if (kindValue !in supportedKinds) return

                    val specPrefix = evaluateSpecPrefix(kindValue)
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

                    super.visitFile(file)
                }
            }
        }
    }


    companion object {
        private val specKeysToVerify = listOf("hostNetwork", "hostPID", "hostIPC")
    }

}