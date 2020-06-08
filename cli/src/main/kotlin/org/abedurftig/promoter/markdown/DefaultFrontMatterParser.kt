package org.abedurftig.promoter.markdown

class DefaultFrontMatterParser(
    private val delimiter: String = "---"
) : FrontMatterParser {

    override fun readFrontMatterAttributes(frontMatter: String): Map<String, Set<String>> {

        val result = mutableMapOf<String, Set<String>>()
        val lines = frontMatter.split(System.lineSeparator())
        if (lines.isNotEmpty() && lines.first() == delimiter) {
            var currentKey = ""
            var currentValues = mutableSetOf<String>()
            for (line in lines.slice(IntRange(1, lines.size - 1))) {
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
                   return result
               } else {
                   IllegalStateException("Cannot handle the current line: $line")
               }
            }
        }
        return result
    }

    private fun parseLines(lines: Set<String>): Pair<String, Set<String>> {
        return "" to emptySet()
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
