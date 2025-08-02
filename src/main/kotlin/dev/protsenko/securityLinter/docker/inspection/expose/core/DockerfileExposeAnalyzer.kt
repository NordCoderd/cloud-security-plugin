package dev.protsenko.securityLinter.docker.inspection.expose.core

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement

interface DockerfileExposeAnalyzer {
    fun handle(
        ports: List<Int>,
        psiElement: PsiElement,
        holder: ProblemsHolder,
    )
}
