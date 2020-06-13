package org.abedurftig.promoter.flow

data class Settings(
    val targetDir: String,
    val contentDir: String,
    val githubToken: String,
    val devToken: String,
    val publishIf: String
) {
    override fun toString(): String {
        return """
            Settings:
            ------------------
            project directory: $targetDir
            content directory: $contentDir
            github token:      *********
            dev.to token:      *********
            publish if:        $publishIf
            ------------------
        """.trimIndent()
    }
}
