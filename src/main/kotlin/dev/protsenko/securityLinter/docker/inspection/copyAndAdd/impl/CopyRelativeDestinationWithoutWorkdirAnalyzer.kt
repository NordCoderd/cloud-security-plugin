package dev.protsenko.securityLinter.docker.inspection.copyAndAdd.impl

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.docker.dockerFile.DockerPsiFile
import com.intellij.docker.dockerFile.parser.psi.DockerFileAddOrCopyCommand
import com.intellij.docker.dockerFile.parser.psi.DockerFileFromCommand
import com.intellij.docker.dockerFile.parser.psi.DockerFileWorkdirCommand
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.docker.inspection.copyAndAdd.core.DockerfileCopyOrAddAnalyzer
import dev.protsenko.securityLinter.utils.removeQuotes

class CopyRelativeDestinationWithoutWorkdirAnalyzer : DockerfileCopyOrAddAnalyzer {
    override fun handle(
        element: DockerFileAddOrCopyCommand,
        holder: ProblemsHolder,
    ) {
        if (element.addKeyword != null) return

        val destination = extractDestinationPath(element) ?: return
        if (isAbsolutePath(destination)) return

        val currentFile = element.parent as? DockerPsiFile ?: return
        val lastFromOffset =
            currentFile
                .findChildrenByClass(DockerFileFromCommand::class.java)
                .lastOrNull { it.textOffset < element.textOffset }
                ?.textOffset ?: Int.MIN_VALUE

        val hasWorkdirInCurrentStage =
            currentFile
                .findChildrenByClass(DockerFileWorkdirCommand::class.java)
                .any { workdir ->
                    workdir.textOffset > lastFromOffset && workdir.textOffset < element.textOffset
                }

        if (hasWorkdirInCurrentStage) return

        val descriptor =
            HtmlProblemDescriptor(
                element,
                SecurityPluginBundle.message("dfs034.documentation"),
                SecurityPluginBundle.message("dfs034.problem-text"),
                ProblemHighlightType.WARNING,
            )

        holder.registerProblem(descriptor)
    }

    private fun extractDestinationPath(element: DockerFileAddOrCopyCommand): String? {
        val jsonArguments =
            element.parametersInJsonForm
                ?.children
                ?.map { it.text.removeQuotes() }
                ?.filter { argument ->
                    argument.isNotBlank() && argument !in JSON_SYNTAX_TOKENS
                }

        if (!jsonArguments.isNullOrEmpty()) {
            if (jsonArguments.size != COPY_WITH_ONE_SOURCE_ARGS_COUNT) return null
            return jsonArguments.last()
        }

        val fileOrUrlList = element.fileOrUrlList
        if (fileOrUrlList.isEmpty() || fileOrUrlList.size != COPY_WITH_ONE_SOURCE_ARGS_COUNT) return null

        return fileOrUrlList.last().text.removeQuotes()
    }

    private fun isAbsolutePath(path: String): Boolean {
        if (path.startsWith(UNIX_ABSOLUTE_PREFIX)) return true
        if (WINDOWS_ABSOLUTE_PATTERN.matches(path)) return true
        return ENV_ABSOLUTE_PATTERN.matches(path)
    }

    companion object {
        private const val COPY_WITH_ONE_SOURCE_ARGS_COUNT = 2
        private const val UNIX_ABSOLUTE_PREFIX = "/"
        private val JSON_SYNTAX_TOKENS = setOf("[", "]", ",")
        private val WINDOWS_ABSOLUTE_PATTERN = Regex("""^[A-Za-z]:\\.*$""")
        private val ENV_ABSOLUTE_PATTERN = Regex("""^\$\{?[A-Za-z0-9_]+}?(/.*)?$""")
    }
}
