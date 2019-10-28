#!/bin/bash

echo "Starting running VARpedia"
JAR="se206.ass3-all-1.7-FINAL.jar"
T=".bin/music/"
FONT="Montserrat-Regular.ttf"
MUSIC=("$TBig_Star_Classic_Rock" "$TFree_Music_And_Free_Beer_RocknRoll" "$TLoving_Men_Jazz" "$TSummer_Joyride_Country" )
 

if ! [ -f .bin/"$FONT" ]; then
	echo "The font file $FONT is missing. The program will use the default font for creations" >&2
	echo "To fix this issue, place a font file name $FONT in the .bin directory" >&2
fi

for i in ${MUSIC[@]};do
	echo "$i"
	if ! [ -f "$i.mp3" ]; then
		echo "$i.mp3 music file doesn't exist. This music file will not be avialable" >&2
		echo "To fix this error, create a music file $i.mp3" >&2
	fi
done

if ! [ -f .bin/User-Manual.pdf ]; then echo "ERROR: No User Manual included" >&2; fi
if ! [ -f .bin/keys.txt ]; then
  echo "ERROR: keys.txt is missing" >&2;
else
  if ! [ -s .bin/keys.txt ]; then
    echo "ERROR: keys.txt is empty" >&2;
  fi
fi


if ! [ -f "$JAR" ]; then
	echo "The jar file $JAR does not exist " >&2
else
	java -jar "$JAR"
fi
