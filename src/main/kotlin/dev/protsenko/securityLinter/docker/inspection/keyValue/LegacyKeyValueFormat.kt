package dev.protsenko.securityLinter.docker.inspection.keyValue

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.docker.dockerFile.DockerPsiFile
import com.intellij.docker.dockerFile.parser.psi.DockerFileArgCommand
import com.intellij.docker.dockerFile.parser.psi.DockerFileEnvCommand
import com.intellij.docker.dockerFile.parser.psi.DockerFileVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiElementVisitor.EMPTY_VISITOR
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle

class LegacyKeyValueFormat : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor {
        if (holder.file !is DockerPsiFile) return EMPTY_VISITOR

        return object : DockerFileVisitor() {
            override fun visitArgCommand(o: DockerFileArgCommand) {
                if (!hasLegacyKeyValueFormat(o.text, ARG_KEYWORD)) return
                registerProblem(holder, o)
            }

            override fun visitEnvCommand(o: DockerFileEnvCommand) {
                if (!hasLegacyKeyValueFormat(o.text, ENV_KEYWORD)) return
                registerProblem(holder, o)
            }
        }
    }

    private fun registerProblem(
        holder: ProblemsHolder,
        command: PsiElement,
    ) {
        val descriptor =
            HtmlProblemDescriptor(
                command,
                SecurityPluginBundle.message("dfs037.documentation"),
                SecurityPluginBundle.message("dfs037.problem-text"),
                ProblemHighlightType.WARNING,
            )
        holder.registerProblem(descriptor)
    }

    private fun hasLegacyKeyValueFormat(
        commandText: String,
        keyword: String,
    ): Boolean {
        val arguments = extractArguments(commandText, keyword) ?: return false
        if (arguments.isEmpty()) return false

        var index = 0
        while (index < arguments.length && !arguments[index].isWhitespace()) {
            if (arguments[index] == '=') return false
            index++
        }

        while (index < arguments.length && arguments[index].isWhitespace()) {
            index++
        }

        return index < arguments.length
    }

    private fun extractArguments(
        commandText: String,
        keyword: String,
    ): String? {
        val trimmed = commandText.trimStart()
        if (!trimmed.startsWith(keyword, ignoreCase = true)) return null

        return trimmed.substring(keyword.length).trimStart()
    }

    companion object {
        private const val ARG_KEYWORD = "ARG"
        private const val ENV_KEYWORD = "ENV"
    }
}
