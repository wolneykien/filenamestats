#!/bin/sh -efu


PROG_NAME='filenamestats'
CLASSPATH="${0%/*}/class"
MAINCLASS='name.wolneykien.filenamestats.FileNameStats'

java -cp "$CLASSPATH" \
     -Dprogram.name="$PROG_NAME" \
     "$MAINCLASS" "$@"
