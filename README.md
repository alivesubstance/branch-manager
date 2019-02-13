Plugin provide actions like checking out same branch in multiple Git repositories
and remove unneeded local branches. It's very useful in projects with several maven
or gradle modules loaded in the IDE.

Plugin has a GUI interface that allow to select repositories and branch to be created.

Also it's possible to switch repositories to already existed branch. Plugin find common branches
in selected repositories and suggest to choose one to be checked out.

Another feature is to remove unneeded branches. For this case plugin also provide GUI with
list of loaded repositories. It shows branches that do not have tracking remotes and
allow to delete them.

Plugin available at VCS -> Branch Manager
