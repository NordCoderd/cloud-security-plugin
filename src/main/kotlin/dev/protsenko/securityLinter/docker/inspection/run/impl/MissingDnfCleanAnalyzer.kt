package dev.protsenko.securityLinter.docker.inspection.run.impl

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.docker.checker.DnfCleanAllValidator
import dev.protsenko.securityLinter.docker.inspection.run.core.DockerfileRunAnalyzer

class MissingDnfCleanAnalyzer : DockerfileRunAnalyzer {
    override fun handle(
        runCommand: String,
        psiElement: PsiElement,
        holder: ProblemsHolder,
    ) {
        if (!DnfCleanAllValidator.isValid(runCommand)) {
            val descriptor =
                HtmlProblemDescriptor(
                    psiElement,
                    SecurityPluginBundle.message("dfs019.documentation"),
                    SecurityPluginBundle.message("dfs019.purge-dnf-package-cache"),
                    ProblemHighlightType.WARNING,
                )

            holder.registerProblem(descriptor)
        }
    }
}
