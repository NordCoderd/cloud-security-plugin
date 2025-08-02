package dev.protsenko.securityLinter.docker.inspection.copyAndAdd.impl

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.docker.dockerFile.parser.psi.DockerFileAddOrCopyCommand
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle
import dev.protsenko.securityLinter.docker.inspection.copyAndAdd.core.DockerfileCopyOrAddAnalyzer
import dev.protsenko.securityLinter.utils.removeQuotes

class UseSlashForCopyArgsAnalyzer : DockerfileCopyOrAddAnalyzer {
    override fun handle(
        element: DockerFileAddOrCopyCommand,
        holder: ProblemsHolder,
    ) {
        val parametersInJsonForm = element.parametersInJsonForm
        val fileOrUrlList = element.fileOrUrlList

        val lastCopyArgument =
            if (parametersInJsonForm != null) {
                val copyArguments = parametersInJsonForm.children
                if (copyArguments.size <= 2) return
                copyArguments.last()
            } else if (!fileOrUrlList.isEmpty()) {
                if (fileOrUrlList.size <= 2) return
                fileOrUrlList.last()
            } else {
                null
            } ?: return

        val lastElement = lastCopyArgument.text?.removeQuotes() ?: return
        if (!lastElement.endsWith("/") && !lastElement.endsWith("\\")) {
            val descriptor =
                HtmlProblemDescriptor(
                    element,
                    SecurityPluginBundle.message("dfs008.documentation"),
                    SecurityPluginBundle.message("dfs008.use-slash-for-copy-args"),
                    ProblemHighlightType.ERROR,
                    emptyArray(),
                )

            holder.registerProblem(descriptor)
        }
    }
}
