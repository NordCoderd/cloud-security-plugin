package dev.protsenko.securityLinter.utils

import com.intellij.psi.PsiElement
import org.jetbrains.yaml.psi.YAMLDocument
import org.jetbrains.yaml.psi.YAMLMapping
import org.jetbrains.yaml.psi.YAMLSequence

object YamlPath {

    fun findByYamlPath(path: String, document: YAMLDocument): PsiElement? {
        val matchedElement: YAMLMapping = document.children[0] as? YAMLMapping ?: return null

        return findByYamlPath(path, matchedElement)
    }

    fun findByYamlPath(path: String, matchedElement: YAMLMapping): PsiElement? {
        val searchTokens = path.split(".")
        var startElement: PsiElement? = matchedElement

        var countMatches = 0

        for (token in searchTokens) {
            if (token.endsWith("]")) {
                val yamlKey = token.substringBefore("[")
                val sequence =
                    (startElement as? YAMLMapping)?.getKeyValueByKey(yamlKey)?.value as? YAMLSequence ?: break
                val index = token.substringAfter("[").substringBefore("]").toIntOrNull() ?: break

                startElement = sequence.items.getOrNull(index)?.value as? YAMLMapping
                countMatches++
            } else {
                startElement = (startElement as? YAMLMapping)?.getKeyValueByKey(token)?.value ?: break
                countMatches++
            }
        }

        if (countMatches == searchTokens.size) {
            return startElement
        }
        return null
    }
}