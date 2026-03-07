package dev.protsenko.securityLinter.docker.inspection.from

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.docker.dockerFile.DockerPsiFile
import com.intellij.docker.dockerFile.parser.psi.DockerFileFromCommand
import com.intellij.docker.dockerFile.parser.psi.DockerFileVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiElementVisitor.EMPTY_VISITOR
import com.intellij.psi.PsiFile
import dev.protsenko.securityLinter.core.HtmlProblemDescriptor
import dev.protsenko.securityLinter.core.SecurityPluginBundle

class InvalidDefaultArgInFromInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor {
        if (holder.file !is DockerPsiFile) return EMPTY_VISITOR

        return object : DockerFileVisitor() {
            override fun visitFile(file: PsiFile) {
                if (file !is DockerPsiFile) return

                val fromCommands = file.findChildrenByClass(DockerFileFromCommand::class.java).sortedBy { it.textOffset }
                val fileText = file.text

                fromCommands.forEach { fromCommand ->
                    val imageReference = extractImageReference(fromCommand.text) ?: return@forEach
                    val args = collectArgDefaultsBefore(fileText, fromCommand.textOffset)
                    if (args.isEmpty()) return@forEach

                    val hasRelevantArgUsage =
                        VARIABLE_PATTERN
                            .findAll(imageReference)
                            .mapNotNull { extractVariableName(it) }
                            .any(args::containsKey)
                    if (!hasRelevantArgUsage) return@forEach

                    val referenceWithoutBuildArgs = substituteWithoutBuildArgs(imageReference, args)
                    if (isValidImageReference(referenceWithoutBuildArgs)) return@forEach

                    val descriptor =
                        HtmlProblemDescriptor(
                            fromCommand,
                            SecurityPluginBundle.message("dfs036.documentation"),
                            SecurityPluginBundle.message("dfs036.problem-text"),
                            ProblemHighlightType.WARNING,
                        )
                    holder.registerProblem(descriptor)
                }
            }
        }
    }

    private fun extractImageReference(fromText: String): String? =
        FROM_IMAGE_PATTERN.find(fromText.trim())?.groupValues?.getOrNull(1)

    private fun collectArgDefaultsBefore(
        fileText: String,
        fromOffset: Int,
    ): Map<String, String?> {
        val textBeforeFrom = fileText.substring(0, fromOffset)
        val defaults = linkedMapOf<String, String?>()
        ARG_PATTERN.findAll(textBeforeFrom).forEach { match ->
            val argName = match.groupValues[1]
            val argDefault = match.groups[2]?.value
            defaults[argName] = argDefault
        }

        return defaults
    }

    private fun substituteWithoutBuildArgs(
        imageReference: String,
        args: Map<String, String?>,
    ): String =
        VARIABLE_PATTERN.replace(imageReference) { match ->
            val variableName = extractVariableName(match) ?: return@replace match.value
            if (!args.containsKey(variableName)) return@replace match.value

            val value = args[variableName]
            val operator = match.groups[2]?.value
            val fallback = match.groups[3]?.value.orEmpty()

            when (operator) {
                null -> value.orEmpty()
                ":-" -> if (value.isNullOrEmpty()) fallback else value
                "-" -> if (value == null) fallback else value
                ":+" -> if (value.isNullOrEmpty()) "" else fallback
                "+" -> if (value == null) "" else fallback
                ":?", "?" -> MISSING_VALUE_SENTINEL
                else -> value.orEmpty()
            }
        }

    private fun extractVariableName(match: MatchResult): String? {
        val bracedVariableName = match.groups[1]?.value
        if (!bracedVariableName.isNullOrBlank()) return bracedVariableName

        val simpleVariableName = match.groups[4]?.value
        if (!simpleVariableName.isNullOrBlank()) return simpleVariableName

        return null
    }

    private fun isValidImageReference(reference: String): Boolean {
        if (reference.isBlank()) return false
        if (reference.contains(MISSING_VALUE_SENTINEL)) return false
        if (reference.any(Char::isWhitespace)) return false
        if (reference.contains('$')) return false
        if (reference.startsWith('/') || reference.startsWith(':') || reference.startsWith('@')) return false
        if (reference.contains("//") || reference.contains("::") || reference.contains("/:") ||
            reference.endsWith(':') || reference.endsWith('@')
        ) {
            return false
        }

        return IMAGE_REFERENCE_PATTERN.matches(reference)
    }

    companion object {
        private val FROM_IMAGE_PATTERN = Regex("""(?i)^FROM\s+(?:--platform(?:\s*=\s*|\s+)\S+\s+)?([^\s]+)""")
        private val ARG_PATTERN = Regex("""(?im)^\s*ARG\s+([A-Za-z_][A-Za-z0-9_]*)(?:\s*=\s*([^\s]+))?\s*$""")
        private val VARIABLE_PATTERN = Regex("""\$\{([A-Za-z_][A-Za-z0-9_]*)(?:(:?[-+?])(.*?))?}|\$([A-Za-z_][A-Za-z0-9_]*)""")
        private val IMAGE_REFERENCE_PATTERN =
            Regex("""^[A-Za-z0-9._/-]+(?::[A-Za-z0-9_][A-Za-z0-9_.-]{0,127})?(?:@[A-Za-z][A-Za-z0-9]*:[A-Fa-f0-9]+)?$""")

        private const val MISSING_VALUE_SENTINEL = "__MISSING_ARG_VALUE__"
    }
}
