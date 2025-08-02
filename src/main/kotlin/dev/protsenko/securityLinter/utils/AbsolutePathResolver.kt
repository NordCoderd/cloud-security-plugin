package dev.protsenko.securityLinter.utils

object AbsolutePathResolver {
    val REGEX_PATTERN =
        Regex("""^["']?(/([A-Za-z0-9\-_+]+(/[A-Za-z0-9\-_+]+)*)?|[A-Za-z0-9\-_+]:\\.*|\$\{?[A-Za-z0-9\-_+]+}?(/.*)?)["']?$""")

    fun isAbsolutePath(path: String) = REGEX_PATTERN.matches(path)
}
