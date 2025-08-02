package dev.protsenko.securityLinter.docker.inspection.run.impl

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.docker.checker.AptGetNoInstallRecommendsValidator
import dev.protsenko.securityLinter.docker.inspection.run.core.DockerfileRunAnalyzer

class AptGetNoInstallRecommendsAnalyzer : DockerfileRunAnalyzer {
    override fun handle(
        runCommand: String,
        psiElement: PsiElement,
        holder: ProblemsHolder,
    ) {
        if (!AptGetNoInstallRecommendsValidator.isValid(runCommand)) {
            val descriptor =
                HtmlProblemDescriptor(
                    psiElement,
                    SecurityPluginBundle.message("dfs014.documentation"),
                    SecurityPluginBundle.message("dfs014.use-no-install-recommends"),
                    ProblemHighlightType.WARNING,
                    emptyArray(),
                )

            holder.registerProblem(descriptor)
        }
    }
}
