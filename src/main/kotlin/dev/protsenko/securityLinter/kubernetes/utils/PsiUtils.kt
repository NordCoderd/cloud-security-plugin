package dev.protsenko.securityLinter.kubernetes.utils

import org.jetbrains.yaml.psi.YAMLQuotedText

fun YAMLQuotedText.unquoted(): String =
    if (this.isSingleQuote) {
        this.text.trim('\'')
    } else {
        this.text.trim('"')
    }
