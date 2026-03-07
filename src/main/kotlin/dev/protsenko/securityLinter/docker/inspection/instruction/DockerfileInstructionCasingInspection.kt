package dev.protsenko.securityLinter.docker.inspection.instruction

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.docker.dockerFile.DockerPsiFile
import com.intellij.docker.dockerFile.parser.psi.DockerFileVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiElementVisitor.EMPTY_VISITOR
import com.intellij.psi.PsiFile
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle

class DockerfileInstructionCasingInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor {
        if (holder.file !is DockerPsiFile) return EMPTY_VISITOR

        return object : DockerFileVisitor() {
            override fun visitFile(file: PsiFile) {
                if (file !is DockerPsiFile) return

                val matches = COMMAND_REGEX.findAll(file.text)
                matches.forEach { match ->
                    val commandName = match.groupValues[1]
                    if (!commandName.isMixedCase()) return@forEach

                    val commandElement = file.findElementAt(match.range.first) ?: return@forEach
                    val descriptor =
                        HtmlProblemDescriptor(
                            commandElement,
                            SecurityPluginBundle.message("dfs035.documentation"),
                            SecurityPluginBundle.message("dfs035.problem-text", commandName),
                            ProblemHighlightType.WARNING,
                        )

                    holder.registerProblem(descriptor)
                }
            }
        }
    }

    private fun String.isMixedCase(): Boolean {
        val hasLowerCase = any { it.isLowerCase() }
        val hasUpperCase = any { it.isUpperCase() }
        return hasLowerCase && hasUpperCase
    }

    companion object {
        private val COMMAND_REGEX = Regex("""(?m)^\s*([A-Za-z][A-Za-z0-9_-]*)\b""")
    }
}
