#!/bin/bash

DATAPARSERDIR=/sdb/SimRa/dashboard/data-parser
WEBSITEDIR=/sdb/SimRa/dashboard/simra-project.github.io
(cd $DATAPARSERDIR && java -jar data-parser-1.1-fat.jar -s /sdb/SimRa/Regions -o true)
(cd $WEBSITEDIR && ./commit.sh)
