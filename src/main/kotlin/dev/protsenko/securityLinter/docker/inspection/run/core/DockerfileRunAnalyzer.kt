package dev.protsenko.securityLinter.docker.inspection.run.core

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement

interface DockerfileRunAnalyzer {
    fun handle(
        runCommand: String,
        psiElement: PsiElement,
        holder: ProblemsHolder,
    )
}
