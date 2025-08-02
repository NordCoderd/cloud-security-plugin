package dev.protsenko.securityLinter.docker.checker.core

interface RunCommandValidator {
    /**
     * Every command must start with RUN and contain a shell script (Linux commands).
     * Before implementing, please follow these guidelines:
     * - The name of the implemented object should end with "Validator".
     * - Your validator logic should not create additional objects (e.g., lists).
     * - Your validator must handle new lines and special symbols (e.g., &&).
     * - Use only one regular expression to check the command.
     * - Write tests using JUnit 3, covering both valid and invalid cases.
     * - Include test cases that adhere to these rules.
     * - Provide Javadoc comments for your regular expressions and for this method.
     */
    fun isValid(command: String): Boolean
}
