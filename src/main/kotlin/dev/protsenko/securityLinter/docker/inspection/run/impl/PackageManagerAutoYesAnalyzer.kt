package dev.protsenko.securityLinter.docker.inspection.run.impl

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.docker.checker.PackageManagerAutoYesValidator
import dev.protsenko.securityLinter.docker.inspection.run.core.DockerfileRunAnalyzer

class PackageManagerAutoYesAnalyzer : DockerfileRunAnalyzer {
    override fun handle(
        runCommand: String,
        psiElement: PsiElement,
        holder: ProblemsHolder,
    ) {
        if (!PackageManagerAutoYesValidator.isValid(runCommand)) {
            val descriptor =
                HtmlProblemDescriptor(
                    psiElement,
                    SecurityPluginBundle.message("dfs020.documentation"),
                    SecurityPluginBundle.message("dfs020.use-package-manager-auto-confirm"),
                    ProblemHighlightType.WARNING,
                )

            holder.registerProblem(descriptor)
        }
    }
}
