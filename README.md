# README.md

After cloning, initialize submodules with `git submodule update --init`.

It makes sense to add a deploy-key for simra.project.github.io, so that we can push without having to enter passwords etc, e.g., by using *git-bot*.

```bash
# Commit latest changes (preferably changes to the dashboard.json) via git-bot
./git-bot/git-bot.sh ./simra.project.github.io >> ./git-bot/log.sh


```
