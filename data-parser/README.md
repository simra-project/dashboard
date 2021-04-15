# Data Parser

At VM2, create the latest dashboard.json with `java -jar data-parser-1.0-fat.jar -s sdb/SimRa/SimRa`.
You might need to create the results dir beforehand.

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
