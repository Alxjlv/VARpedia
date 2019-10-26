# VARpedia Beta Release
Welcome to VARpedia! An application designed to help out senior citizens in memory retention, or even if they just wanted to learn something new!  
  
  We recommend checking out the important user information section below before using the application
##### Important user information:
1. This was run & developed on the UG4 lab computers

1. This relies on ffmpeg of version at least 4 - some lab computers run ffmpeg version 4, some run ffmppeg version 2. Check the ffmpeg version with `ffmpeg -version`. You may need to switch to another lab computer (sorry).

1. Included with the Jar is the hidden .bin folder. Inside here is the Music directory, and a font file called Montserrat-Regular.ttf. Please do not delete either of these files or the application will not run

1. This was developed on Java 8 with JavaFX. This code may not work if you have openjdk etc.

1. If you've extracted the Jar from a zip file you may need to run `chmod +x` to give it executable permissions to run

1. Our target audience is the Elderly

###### If you want to compile our code:
1. Add your public Flickr API key to a new file called keys.txt in the folder .bin. This should be in the format `FLICKR_PUBLIC = YOUR_KEY_HERE`. Only put the key in there, nothing else.

1. To compile, you will need to import the Gradle project in IntelliJ. We've included our build.gradle so hopefully that works for you. Then type gradle fatJar to compile with dependencies

1. The jar will be in the build/lib folder. `chmod +x` the jar and use `java -jar` to run it

#### Music Attribution:
1. Loving Men by The_Stereo_Inspectors (c) copyright 2018 Licensed under a Creative Commons Attribution Noncommercial  (3.0) license. http://dig.ccmixter.org/files/The_Stereo_Inspectors/58932 Ft: Ciggiburns
1. Big Star (Classic Rock) by Whitewolf (c) copyright 2019 Licensed under a Creative Commons Attribution Noncommercial  (3.0) license. http://dig.ccmixter.org/files/Whitewolf225/60046 Ft: Admiral Bob
1. Free Music And Free Beer by Aussens@iter (c) copyright 2018 Licensed under a Creative Commons Attribution Noncommercial  (3.0) license. http://dig.ccmixter.org/files/tobias_weber/57290 Ft: Admiral Bob, Bill Ray
1. Countryside Summer Joyride by Kara Square (c) copyright 2017 Licensed under a Creative Commons Attribution Noncommercial  (3.0) license. http://dig.ccmixter.org/files/mindmapthat/56281 Ft: Javolenus

#### Code Attribution:
1. Code for changeListener for stage: https://stackoverflow.com/questions/38216268/how-to-listen-resize-event-of-stage-in-javafx
1. Code for setting background image: https://stackoverflow.com/questions/9738146/javafx-how-to-set-scene-background-image