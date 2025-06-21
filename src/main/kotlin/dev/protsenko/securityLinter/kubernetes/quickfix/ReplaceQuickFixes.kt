package dev.protsenko.securityLinter.kubernetes.quickfix

import com.intellij.codeInspection.util.IntentionFamilyName

class ReplaceValueTo1000QuickFix(@IntentionFamilyName private val message: String) : ReplaceValueWithEnumQuickFix(message) {
    override val enumValue: ReplacedValueEnum = ReplacedValueEnum.ONE_THOUSAND
}

class ReplaceValueToFalseQuickFix(@IntentionFamilyName private val message: String) : ReplaceValueWithEnumQuickFix(message)  {
    override val enumValue: ReplacedValueEnum = ReplacedValueEnum.FALSE
}
class ReplaceValueToTrueQuickFix(@IntentionFamilyName private val message: String) : ReplaceValueWithEnumQuickFix(message)  {
    override val enumValue: ReplacedValueEnum = ReplacedValueEnum.TRUE
}
