package org.abedurftig.promoter

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import org.abedurftig.promoter.clients.devto.DevToClient
import org.abedurftig.promoter.clients.devto.DevToService
import org.abedurftig.promoter.files.BlogPostReader
import org.abedurftig.promoter.files.BlogPostWriter
import org.abedurftig.promoter.files.ChecksumBuilder
import org.abedurftig.promoter.files.StatusService
import org.abedurftig.promoter.clients.git.GitClient
import org.abedurftig.promoter.flow.Log
import org.abedurftig.promoter.flow.Promoter
import org.abedurftig.promoter.flow.PromotionException
import org.abedurftig.promoter.flow.Settings
import org.abedurftig.promoter.markdown.MarkdownComposer

class ApplicationWrapper : CliktCommand(
    name = "BlogPostPromoter",
    printHelpOnEmptyArgs = false,
    help = """
        Syndicate your blog posts to Dev.to
        
        All options can be provided as ENV vars also. Use the BPP prefix. For example BPP_PROJECT_DIR. 
    """.trimIndent(),
    epilog = "Feel free to contribute or report any issues at https://github.com/abedurftig/blog-post-promoter/."
) {

    init {
        context {
            autoEnvvarPrefix = "BPP"
        }
    }

    private val projectDir: String by option(
        "--project-dir", "-p",
        help = "Path to project folder (containing the Git repository)")
        .required()

    private val articlesDir: String by option(
        "--articles-dir", "-a",
        help = "Path to folder contains the blog posts in project")
        .required()

    private val publishIf: String by option(
        "--publish-if", "-if",
        help = "The name of front matter attribute which needs to be " +
            "true before publishing the post; the default is 'published'")
        .default("published")

    private val devToken: String by option(
        "--dev-token", "-dt",
        help = "API token for Dev.to")
        .required()

    private val githubToken: String by option(
        "--github-token", "-gt",
        help = "API token for GitHub")
        .required()

    override fun run() {

        val settings = Settings(projectDir, articlesDir, githubToken, devToken, publishIf)
        val blogPostReader = BlogPostReader()
        val markdownComposer = MarkdownComposer()
        val blogPostWriter = BlogPostWriter(markdownComposer)
        val statusService = StatusService(settings)
        val checksumBuilder = ChecksumBuilder()
        val devToService = DevToService(DevToClient(settings.devToken))
        val gitClient = GitClient(settings.githubToken)

        val promoter = Promoter(
            blogPostReader,
            blogPostWriter,
            statusService,
            checksumBuilder,
            devToService,
            markdownComposer,
            gitClient
        )

        try {
            promoter.execute(settings)
        } catch (exception: PromotionException) {
            Log.warn(exception.exceptionMessage)
        }
    }
}

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        ApplicationWrapper().main(args)
    }
}
