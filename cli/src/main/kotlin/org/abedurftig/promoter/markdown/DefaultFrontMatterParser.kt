package org.abedurftig.promoter.markdown

import java.util.Scanner

class DefaultFrontMatterParser(
    private val delimiter: String = "---"
) : FrontMatterParser {

    override fun readFrontMatterAttributes(markdown: String): Map<String, Set<String>> {

        val result = mutableMapOf<String, Set<String>>()

        val scanner = Scanner(markdown)
        var index = 0
        var currentKey = ""
        var currentValues = mutableSetOf<String>()
        var keepParsing = true

        while (scanner.hasNextLine() && keepParsing) {
            val line = scanner.nextLine()
            if (index == 0 && line == delimiter) {
                continue
            }
            if (isLineComplete(line)) {
                if (currentKey.isNotEmpty() && currentValues.isNotEmpty()) {
                    result[currentKey] = currentValues
                    currentKey = ""
                    currentValues = mutableSetOf()
                }
                result[getKey(line)!!] = setOf(getValue(line)!!)
            } else if (getKey(line) != null && getValue(line) == null) {
                currentKey = getKey(line)!!
            } else if (!line.contains(delimiter) && line.trim().startsWith('-')) {
                val possibleValue = line.replace("-", "").trim()
                if (possibleValue.isNotBlank()) {
                    currentValues.add(possibleValue)
                }
            } else if (line.contains(delimiter)) {
                if (currentKey.isNotEmpty() && currentValues.isNotEmpty()) {
                    result[currentKey] = currentValues
                }
                keepParsing = false
            } else {
                throw IllegalStateException("Cannot handle the current line: $line")
            }
            index++
        }
        scanner.close()
        return result
    }

    private fun isLineComplete(line: String): Boolean {
        return lineContainsKey(line) && lineContainsValue(line)
    }

    private fun lineContainsValue(line: String): Boolean =
        getValue(line) != null

    private fun getValue(line: String): String? {
        val keyValueSeparatorIndex = line.indexOf(':')
        return if (keyValueSeparatorIndex == -1)
            null
        else {
            val possibleValue = line.substring(keyValueSeparatorIndex + 1, line.length).trim()
            return if (possibleValue.isBlank()) null else possibleValue
        }
    }

    private fun lineContainsKey(line: String): Boolean =
        getKey(line) != null

    private fun getKey(line: String): String? {
        val keyValueSeparatorIndex = line.indexOf(':')
        return if (keyValueSeparatorIndex == -1)
            null
        else {
            val possibleKey = line.substring(0, keyValueSeparatorIndex).trim()
            return if (possibleKey.isBlank()) null else possibleKey
        }

    }
}
