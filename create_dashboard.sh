#!/bin/bash

(cd data-parser && java -jar data-parser-1.1-fat.jar -s /sdb/SimRa/Regions)
(cd simra-project.github.io && ./commit.sh)
