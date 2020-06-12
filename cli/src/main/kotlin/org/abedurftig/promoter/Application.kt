package org.abedurftig.promoter

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

class ApplicationWrapper : CliktCommand(
    name = "BlogPostPromoter",
    printHelpOnEmptyArgs = true,
    help = """
        Syndicate your blog posts to Dev.to
        
        All options can be provided as ENV vars also. Use the BPP prefix. For example BPP_PROJECT_DIR. 
    """.trimIndent(),
    epilog = "Feel free to contribute or report any issues at https://github.com/abedurftig/blog-post-promoter/.") {

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

    private val devToken: String by option(
        "--dev-token", "-dt",
        help = "API token for Dev.to",
        envvar = "DEV_TOKEN").required()

    private val githubToken: String by option(
        "--github-token", "-gt",
        help = "API token for GitHub",
        envvar = "GITHUB_TOKEN").required()

    override fun run() {
        println("This will be great!")
    }
}

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        ApplicationWrapper().main(args)
    }
}
