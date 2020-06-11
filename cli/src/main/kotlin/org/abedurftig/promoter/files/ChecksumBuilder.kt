package org.abedurftig.promoter.files

import org.apache.commons.codec.digest.DigestUtils
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.NoSuchFileException
import java.nio.file.Paths

class ChecksumBuilder {

    fun calculateCheckSumFromDist(filePath: String): String {
        return try {
            Files.newInputStream(Paths.get(filePath))
                .use { inputStream -> DigestUtils.md5Hex(inputStream) }
        } catch (invalidPath: InvalidPathException) {
            ""
        } catch (noSuchFile: NoSuchFileException) {
            ""
        }
    }

    fun calculateCheckSum(content: String): String {
        return DigestUtils.md5Hex(content)
    }
}
