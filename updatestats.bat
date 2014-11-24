
set CP="E:\filenamestats\class"
set DIR="E:\orders"
set CNAME="name.wolneykien.filenamestats.FileNameStats"
set PAT="-([0-9]+)(\.[^.]+)?$"
set STAT="stats.txt"

java -cp %CP% %CNAME% %PAT% %STAT% %DIR%