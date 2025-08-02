package dev.protsenko.securityLinter.utils

import com.intellij.docker.dockerFile.parser.psi.DockerFileParametersInJsonForm

fun String.removeQuotes(): String = this.replace("\"", "").replace("'", "")

fun String.extension(): String = this.substringAfterLast(".", "")

fun DockerFileParametersInJsonForm.toStringDockerCommand(prefix: String) =
    this.children.joinToString(separator = " ", prefix = prefix) { it.text.removeQuotes() }
