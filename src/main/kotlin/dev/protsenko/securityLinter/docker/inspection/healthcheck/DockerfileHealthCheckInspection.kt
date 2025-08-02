package dev.protsenko.securityLinter.docker.inspection.healthcheck

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.docker.dockerFile.DockerPsiFile
import com.intellij.docker.dockerFile.parser.psi.DockerFileFromCommand
import com.intellij.docker.dockerFile.parser.psi.DockerFileHealthCheckCommand
import com.intellij.docker.dockerFile.parser.psi.DockerFileVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiElementVisitor.EMPTY_VISITOR
import com.intellij.psi.PsiFile
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.core.quickFix.DeletePsiElementQuickFix

class DockerfileHealthCheckInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor {
        if (holder.file !is DockerPsiFile) {
            return EMPTY_VISITOR
        }
        return object : DockerFileVisitor() {
            override fun visitFile(file: PsiFile) {
                if (file !is DockerPsiFile) {
                    return
                }
                val fromCommands = file.findChildrenByClass(DockerFileFromCommand::class.java)
                val buildStages = fromCommands.associateBy { it.textOffset }
                val lastStage = buildStages.keys.maxOrNull() ?: return

                val healthChecks = file.findChildrenByClass(DockerFileHealthCheckCommand::class.java)

                val lastInstructions =
                    healthChecks.filter {
                        it.textOffset > lastStage
                    }
                if (lastInstructions.size > 1) {
                    for (instruction in lastInstructions.dropLast(1)) {
                        val descriptor =
                            HtmlProblemDescriptor(
                                instruction,
                                SecurityPluginBundle.message("dfs012.documentation"),
                                SecurityPluginBundle.message("dfs012.only-one-healthcheck"),
                                ProblemHighlightType.ERROR,
                                arrayOf(DeletePsiElementQuickFix(SecurityPluginBundle.message("dfs012.remove-redundant-healthcheck"))),
                            )

                        holder.registerProblem(descriptor)
                    }
                }
            }
        }
    }
}
