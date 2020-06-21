# Blog Post Promoter

## Goal

I want to syndicate my blog posts on https://dev.to next to being published on my own blog, but I don't want to do this manually.
I found that DEV.to has a public API in beta, so I want to use that to automatically publish and update my blog posts on DEV.to.

I also want that the Front Matter of my blog posts get updated accordingly.
After that the updates should be pushed to my GitHub repo which contains the blog.  

## Help

```
Usage: BlogPostPromoter [OPTIONS]

  Syndicate your blog posts to Dev.to

  All options can be provided as ENV vars also. Use the BPP prefix. For
  example BPP_PROJECT_DIR.

Options:
  -p, --project-dir TEXT    Path to project folder (containing the Git
                            repository)
  -a, --articles-dir TEXT   Path to folder contains the blog posts in project
  -if, --publish-if TEXT    The name of front matter attribute which needs to
                            be true before publishing the post; the default is
                            'published'
  -dt, --dev-token TEXT     API token for Dev.to
  -gt, --github-token TEXT  API token for GitHub
  -h, --help                Show this message and exit

Feel free to contribute or report any issues at
https://github.com/abedurftig/blog-post-promoter/.
```

## Docker

Run the following command in your blog project.

```
docker run -v `pwd`:/home/app/project \
-e BPP_ARTICLES_DIR=/content/articles \
-e BPP_DEV_TOKEN=xNLp6SKLKPPiNcsbVy78NA1Z \
-e BPP_GITHUB_TOKEN=9b60644166bf4beaaadcac506eaf2471fd472f15 \
dasnervtdoch/blog-post-promoter:latest
```

## Validation

Your blog post won't be synced if you don't have a set the `canonical_url` attribute in the FrontMatter.

## Sample Output

```
Running with the following settings:
------------------
project directory: /home/app/project/
content directory: /content/articles
github token:      *********
dev.to token:      *********
publish if:        published
------------------
Found 4 blog posts, the last status update was '2020-06-17T23:21:22.510076'.
The blog post with title Third' is missing the 'canonical_url' attribute.
The blog post with title Quatre' is missing the 'canonical_url' attribute.
The blog post with title Integration tests with Gradle' is missing the 'canonical_url' attribute.
More detail: New=0, Updated=0, Unmodified=1, Untracked=0, Skipped=3
The blog post with title 'Why I decided to publish on a blog?' has not changed.
locking FileBasedConfig[/root/.gitconfig] failed after 5 retries
---------------
Found Git repo:/home/app/project/.git
Remote Git repo: git@github.com:abedurftig/personal-blog.git
Will push new and changed files to the GitHub repo now.
Git; adding modified file: content/articles/promoter.status
All files have been updated.
Done
```


