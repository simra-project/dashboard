# README.md

The purpose of this project is to build the dashboard part of the simra.project.github.io website.
After cloning, initialize submodules with `git submodule update --init`.

Build the latest JSON and commit it with `./create_dashboard.sh`. This requires some setup, so run through the manual instructions below first to validate that everything is working.


# Manual Instructions

1. Build the latest dashboard.json with the data-parser (pre-built jar in directory)
2. Build the latest dashboard javascript bundle with npm in the website director
3. Commit the newly created dashboard.json in simra-project.github.io

Additional tips and setup instructions can be found in the README.md of data-parser, website and simra-project.github.io

# SimRa VM setup

Build the latest JSON and commit it with `./create_dashboard.sh`. This requires some setup, so run through the manual instructions above first to validate that everything is working.

Crontab Example: `00 23 * * * cd /sdb/SimRa/dashboard && ./create_dashboard.sh >> /sdb/SimRa/dashboard/create_dashboard.log 2>&1`
