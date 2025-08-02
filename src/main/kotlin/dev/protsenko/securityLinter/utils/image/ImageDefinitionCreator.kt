package dev.protsenko.securityLinter.utils.image

import com.intellij.docker.dockerFile.parser.psi.DockerFileEnvRegularDeclaration
import com.intellij.docker.dockerFile.parser.psi.DockerFileFromCommand
import dev.protsenko.securityLinter.utils.removeQuotes
import dev.protsenko.securityLinter.utils.resolveVariable

object ImageDefinitionCreator {
    /**
     * Used for parsing image definition from DockerFileFromCommand
     */
    fun fromDockerFileFromCommand(fromCommand: DockerFileFromCommand): ImageDefinition? {
        val fromCommandText = fromCommand.text
        val nameChainList = fromCommand.nameChainList.map { it.text }
        val resolvedVariables =
            fromCommand.nameChainList
                .flatMap { name ->
                    name.variableRefSimpleList.mapNotNull { variableReference ->
                        val variableReferenceName = variableReference.referencedName?.text ?: return@mapNotNull null
                        val resolvedVariable =
                            variableReference.resolveVariable()
                                ?: (
                                    (variableReference.resolve() as DockerFileEnvRegularDeclaration)
                                        .regularValue
                                        ?.text
                                        ?.removeQuotes()
                                ) ?: return@mapNotNull null

                        variableReferenceName to resolvedVariable
                    }
                }.toMap()

        if (nameChainList.isEmpty()) return null

        // Looking for the first and last element of name
        val firstElement = nameChainList.first()
        val lastElement = nameChainList.last()
        if (firstElement == lastElement) return ImageDefinition(firstElement, null)

        // Looking for positions in the original line
        val fistPosition = fromCommandText.indexOf(firstElement)
        val lastPosition = fromCommandText.indexOf(lastElement) + lastElement.length

        // Retrieving image name with version
        val fullImageName = fromCommandText.substring(fistPosition, lastPosition)
        return fromString(fullImageName, resolvedVariables)
    }

    /**
     * Used for parsing image definition from String and enrichment with variables
     */
    fun fromString(
        fullImageName: String,
        resolvedVariables: Map<String, String>,
    ): ImageDefinition {
        val splitSymbol = if ("@" in fullImageName) "@" else ":"

        val imageNameWithVersion = fullImageName.split(splitSymbol)
        val (rawImageName, rawVersion) =
            when (imageNameWithVersion.size) {
                1 -> imageNameWithVersion.first() to null
                2 -> imageNameWithVersion.first() to imageNameWithVersion.last()
                else -> return ImageDefinition(fullImageName, null)
            }

        val imageName = resolvedVariables[rawImageName.substringAfter("$")] ?: rawImageName
        val version = rawVersion?.let { resolvedVariables[it.substringAfter("$")] ?: it }

        return ImageDefinition(imageName, version)
    }
}
