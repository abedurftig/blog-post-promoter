package org.abedurftig.promoter

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int

class ApplicationWrapper : CliktCommand(name = "BlogPostPromoter", printHelpOnEmptyArgs = true) {

    private val count: Int by option(help = "Number of greetings").int().required()

    override fun run() {
        println(count)
    }
}

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        ApplicationWrapper().main(args)
    }
}
