package dev.protsenko.securityLinter.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.intellij.psi.PsiElement
import org.jetbrains.yaml.psi.YAMLFile
import org.jetbrains.yaml.psi.YAMLMapping
import org.jetbrains.yaml.psi.YAMLSequence

object YamlUtils {

    fun findByYamlPath(path: String, file: YAMLFile): PsiElement? {
        val documents = file.documents
        if (documents.isEmpty()) return null

        val searchTokens = path.split(".")

        val targetDocument = documents.first()

        var matchedElement: PsiElement? = targetDocument.children[0] as? YAMLMapping ?: return null
        var countMatches = 0

        for (token in searchTokens) {
            if (token.endsWith("]")) {
                val yamlKey = token.substringBefore("[")
                val sequence =
                    (matchedElement as? YAMLMapping)?.getKeyValueByKey(yamlKey)?.value as? YAMLSequence ?: break
                val index = token.substringAfter("[").substringBefore("]").toIntOrNull() ?: break

                matchedElement = sequence.items.getOrNull(index)?.value as? YAMLMapping
                countMatches++
            } else {
                matchedElement = (matchedElement as? YAMLMapping)?.getKeyValueByKey(token)?.value ?: break
                countMatches++
            }
        }

        if (countMatches == searchTokens.size) {
            return matchedElement
        }
        return null
    }
}