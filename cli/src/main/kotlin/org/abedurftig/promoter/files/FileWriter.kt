package org.abedurftig.promoter.files

import java.nio.file.Files
import java.nio.file.Paths

object FileWriter {

    fun writeToFile(filePath: String, content: String) {
        Files.writeString(
            Paths.get(filePath),
            content
        )
    }
}
