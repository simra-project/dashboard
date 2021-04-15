#!/bin/bash

(cd data-parser && java -jar data-parser-1.0-fat.jar -s /sdb/SimRa/SimRa)
(cd simra-project.github.io && ./commit.sh)
