package dev.protsenko.securityLinter.kubernetes.quickfix

enum class ReplacedValueEnum(
    val value: String,
) {
    TRUE("true"),
    FALSE("false"),
    ONE_THOUSAND("1000"),
    RUNTIME_DEFAULT("RuntimeDefault"),
    EMPTY_STRING("\"\""),
    DEFAULT("Default"),
}
