package org.abedurftig.promoter

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import org.abedurftig.promoter.files.BlogPostReader
import org.abedurftig.promoter.files.ChecksumBuilder
import org.abedurftig.promoter.files.StatusService
import org.abedurftig.promoter.flow.Promoter
import org.abedurftig.promoter.flow.Settings
import org.slf4j.LoggerFactory

class ApplicationWrapper : CliktCommand(
    name = "BlogPostPromoter",
    printHelpOnEmptyArgs = true,
    help = """
        Syndicate your blog posts to Dev.to
        
        All options can be provided as ENV vars also. Use the BPP prefix. For example BPP_PROJECT_DIR. 
    """.trimIndent(),
    epilog = "Feel free to contribute or report any issues at https://github.com/abedurftig/blog-post-promoter/."
) {

    companion object {
        private val LOG = LoggerFactory.getLogger("BlogPostPromoter")
    }

    init {
        context {
            autoEnvvarPrefix = "BPP"
        }
    }

    private val projectDir: String by option(
        "--project-dir", "-p",
        help = "Path to project folder",
        envvar = "PROJECT_DIR").required()

    private val articlesDir: String by option(
        "--articles-dir", "-a",
        help = "Path to folder contains the blog posts in project",
        envvar = "ARTICLES_DIR").required()

    private val publishIf: String by option(
        "--publish-if", "-if",
        help = "The name of front matter attribute which needs to be " +
            "true before publishing the post; for example 'public'",
        envvar = "ARTICLES_DIR").required()

    private val devToken: String by option(
        "--dev-token", "-dt",
        help = "API token for Dev.to",
        envvar = "DEV_TOKEN").required()

    private val githubToken: String by option(
        "--github-token", "-gt",
        help = "API token for GitHub",
        envvar = "GITHUB_TOKEN").required()

    override fun run() {

        val settings = Settings(projectDir, articlesDir, githubToken, devToken, publishIf)
        val blogPostReader = BlogPostReader()
        val statusService = StatusService(settings)
        val checksumBuilder = ChecksumBuilder()
        Promoter(blogPostReader, statusService, checksumBuilder).execute(settings)
    }
}

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        ApplicationWrapper().main(args)
    }
}
