package org.abedurftig.promoter.flow

import io.vavr.control.Try

class GitClient(githubToken: String) {

    fun commitNewAndChangedFiles(path: String): Try<String> {

//        val git = Git.open(File(path))
//        Log.log("Found Git repo:" + git.repository.identifier)
//        val gitStatus = git.status().call()
//        gitStatus.untracked.forEach { Log.log(it) }
//        gitStatus.modified.forEach { Log.log(it) }
//        git.add().addFilepattern().call()
//        git.close()

        return Try.of { "Done." }
    }
}
