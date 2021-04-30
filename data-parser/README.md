# Data Parser

The data-parser parses the SimRa ride files provided in the SOURCES directory.
Based on this, it creates a dashboard.json file that is stored in the RESULTS directory.
To not reprocess all files each time, the data-parser creates an index for each dashboard.json created on each day which is also stored in the RESULTS directory.

A copy of the latest dashboard is stored to the file at COPY. This is useful to update the most up to date version in the front-end.

## Instructions

The latest jar is build with Java 11 to `./data-parser-1.0-fat.jar`.
Rebuild with `./gradlew fatJar`

```
usage: [-h] [-s SOURCES] [-r RESULTS] [-o OVERWRITE] [-c COPY]

optional arguments:
  -h, --help              show this help message and exit

  -s SOURCES,             path to source files
  --sources SOURCES

  -r RESULTS,             path to dir in which results will be stored
  --results RESULTS

  -o OVERWRITE,           whether today's files should be overwritten
  --overwrite OVERWRITE

  -c COPY, --copy COPY    filepath to which a copy of the dashboard.json will
                          be stored

```

Default config: source files (../data), output directory (./results), today's file overwriting (false), dashboard
copy (../simra-project.github.io/dashboard/resources/dashboard.json)

## Output

The *dashboard.json* regions are based on the folders found in the source directory. This behavior should not be changed (since every name change would break the history). If you want to change the names shown to the end-users, change it in the front-end.

# SimRa VM setup

Create the latest dashboard.json with `java -jar data-parser-1.0-fat.jar -s sdb/SimRa/SimRa`.
You might need to create the results dir beforehand (`mkdir results`).
