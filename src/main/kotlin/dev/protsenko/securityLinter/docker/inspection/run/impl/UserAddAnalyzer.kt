package dev.protsenko.securityLinter.docker.inspection.run.impl

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.docker.checker.UserAddValidator
import dev.protsenko.securityLinter.docker.inspection.run.core.DockerfileRunAnalyzer

class UserAddAnalyzer : DockerfileRunAnalyzer {
    override fun handle(
        runCommand: String,
        psiElement: PsiElement,
        holder: ProblemsHolder,
    ) {
        if (!UserAddValidator.isValid(runCommand)) {
            val descriptor =
                HtmlProblemDescriptor(
                    psiElement,
                    SecurityPluginBundle.message("dfs029.documentation"),
                    SecurityPluginBundle.message("dfs029.useradd-missing-l-flag-high-uid"),
                    ProblemHighlightType.WARNING,
                    emptyArray(),
                )

            holder.registerProblem(descriptor)
        }
    }
}
