#!/bin/sh -efu


PROG_NAME='jpegsum'
STATNAME="jpegsum.txt"
CLASSPATH="${0%/*}/class"
MAINCLASS='name.wolneykien.filenamestats.FileNameStats'
PATTERN='-([0-9]+).[Jj][Pp][Ee]?[Gg]$'

java -cp "$CLASSPATH" \
     -Dprogram.name="$PROG_NAME" \
     "$MAINCLASS" "$PATTERN" "$STATNAME" "$@"
