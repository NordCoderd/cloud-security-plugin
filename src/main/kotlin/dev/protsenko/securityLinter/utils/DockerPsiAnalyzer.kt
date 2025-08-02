package dev.protsenko.securityLinter.utils

import com.intellij.docker.dockerFile.parser.psi.DockerFileArgDeclaration
import com.intellij.docker.dockerFile.parser.psi.DockerFileVariableRefSimple
import com.intellij.docker.dockerFile.parser.psi.DockerPsiCommand

object DockerPsiAnalyzer {
    /**
     * By some reasons Docker plugin in some cases return PSI elements without children.
     * In that case we should retrieve value of docker command.
     * ex: EXPOSE 22 -> 22; USER root -> root
     * TODO: cache values by the element
     * returns: value in lowercase
     */
    fun splitCommand(dockerElement: DockerPsiCommand): List<String> = splitCommand(dockerElement.text)

    fun splitCommand(text: String): List<String> = text.trim().split(" ").map { it.trim() }
}

fun DockerFileVariableRefSimple.resolveVariable(): String? {
    val resolvedElement = this.resolve() as? DockerFileArgDeclaration ?: return null
    return resolvedElement.anyValue?.text
}
