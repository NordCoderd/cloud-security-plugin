package dev.protsenko.securityLinter.docker.inspection.env

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.docker.dockerFile.DockerPsiFile
import com.intellij.docker.dockerFile.parser.psi.DockerFileEnvCommand
import com.intellij.docker.dockerFile.parser.psi.DockerFileVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiElementVisitor.EMPTY_VISITOR
import dev.protsenko.securityLinter.core.DockerFileConstants.POTENTIAL_SECRETS_NAME
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.core.quickFix.DeletePsiElementQuickFix

class DockerFileEnvInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor {
        if (holder.file !is DockerPsiFile) {
            return EMPTY_VISITOR
        }
        return object : DockerFileVisitor() {
            override fun visitEnvCommand(o: DockerFileEnvCommand) {
                o.envRegularDeclarationList.forEach {
                    val declaredName = it.declaredName.text.uppercase()
                    if (!POTENTIAL_SECRETS_NAME.contains(declaredName)) return@forEach
                    val descriptor =
                        HtmlProblemDescriptor(
                            o,
                            SecurityPluginBundle.message("dfs009.documentation"),
                            SecurityPluginBundle.message("dfs009.possible-secrets-in-env", declaredName),
                            ProblemHighlightType.ERROR,
                            arrayOf(DeletePsiElementQuickFix(SecurityPluginBundle.message("dfs009.remove-env-with-secret"))),
                        )

                    holder.registerProblem(descriptor)
                }
            }
        }
    }
}
