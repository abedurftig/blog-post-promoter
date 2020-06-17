package org.abedurftig.promoter.clients.git

import org.abedurftig.promoter.flow.Log
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File
import java.io.IOException

class GitClientException(message: String?) : Exception(message)

class GitClient(private val githubToken: String) {

    fun commitNewAndChangedFiles(path: String) {

        val git = try {
            Git.open(File(path))
        } catch (exception: IOException) {
            throw GitClientException(exception.message)
        }

        val remoteUrl = git.repository.config.getString("remote", "origin", "url")

        if (!remoteUrl.contains("github.com")) {
            throw GitClientException("Only GitHub repositories are supported.")
        }

        Log.log("---------------")
        Log.log("Found Git repo:" + git.repository.identifier)
        Log.log("Remote Git repo: $remoteUrl")
        Log.log("Will push new and changed files to the GitHub repo now.")

        try {
            val gitStatus = git.status().call()
            gitStatus.untracked.forEach { Log.log("Git; adding new file: $it") }
            gitStatus.modified.forEach { Log.log("Git; adding modified file: $it") }
            git.add().addFilepattern(".").call()
            git.commit().setMessage("Update after syndicating the blog posts").call()
            val userName = parseUsername(remoteUrl)
            val repoName = getRepoName(remoteUrl)
            git.push()
                .setRemote(getTokenRemoteUrl(userName, repoName))
                .setCredentialsProvider(UsernamePasswordCredentialsProvider(githubToken, ""))
                .call()
            Log.log("All files have been updated.")
        } catch (exception: GitAPIException) {
            throw GitClientException(exception.message)
        } finally {
            git.close()
        }
    }

    private fun getTokenRemoteUrl(username: String, repoName: String): String {
        return "https://$githubToken@github.com/$username/$repoName.git"
    }

    private fun getRepoName(url: String): String {
        return url.substringAfterLast("/").replace(".git", "")
    }

    internal fun parseUsername(url: String): String {
        val withoutRepo = url.substring(0, url.lastIndexOf("/"))
        return if (withoutRepo.startsWith("git")) {
            withoutRepo.substringAfterLast(":")
        } else {
            withoutRepo.substringAfterLast("/")
        }
    }
}
