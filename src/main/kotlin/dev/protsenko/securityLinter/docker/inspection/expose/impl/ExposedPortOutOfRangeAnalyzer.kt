package dev.protsenko.securityLinter.docker.inspection.expose.impl

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.core.quickFix.DeletePsiElementQuickFix
import dev.protsenko.securityLinter.docker.inspection.expose.core.DockerfileExposeAnalyzer

class ExposedPortOutOfRangeAnalyzer : DockerfileExposeAnalyzer {
    override fun handle(
        ports: List<Int>,
        psiElement: PsiElement,
        holder: ProblemsHolder,
    ) {
        for (port in ports) {
            if (port < 0 || port > 65535) {
                val descriptor =
                    HtmlProblemDescriptor(
                        psiElement,
                        SecurityPluginBundle.message("dfs010.documentation"),
                        SecurityPluginBundle.message("dfs010.port-out-of-range", port.toString()),
                        ProblemHighlightType.ERROR,
                        arrayOf(DeletePsiElementQuickFix(SecurityPluginBundle.message("dfs010.remove-broken-port"))),
                    )

                holder.registerProblem(descriptor)
            }
        }
    }
}
