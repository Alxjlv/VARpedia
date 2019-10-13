# VARpedia Beta Release
Welcome to VARpedia! An application designed to help out senior citizens in memory retention, or even if they just wanted to learn something new!  
  
  We recommend checking out the important user information section below before using the application
##### Important user information:
1. This was run & developed on the UG4 lab computers

2. This relies on ffmpeg of version at least 4 - some lab computers run ffmpeg version 4, some run ffmppeg version 2. Check the ffmpeg version with `ffmpeg -version`. You may need to switch to another lab computer (sorry).

3. Included with the Jar is the hidden .bin folder. Inside here is the Music directory, and a font file called Montserrat-Regular.ttf. Please do not delete either of these files or the application will not run

4. This was developed on Java 8 with javafx. This code may not work if you have openjdk etc.

5. If you've extracted the Jar from a zip file you may need to run `chmod +x` to give it executable permissions to run

6. Our target audience is the Elderly

7. Beluga Ten (Background music track) is very quiet initially - a couple of minutes in you can notice it.

###### If you want to compile our code:
1. Add your public flickr API key to a new file in main/java/main in a new file called Keys.java with a new field `public static final String FLICKR_PUBLIC = "YOUR_KEY_HERE";`. Only put the key in there, nothing else. The code will not compile if you haven't done this.

2. To compile, you will need to import the gradle project in intellij. We've included our build.gradle so hopefully that works for you. Then type gradle fatJar to compile with dependencies

3. The jar will be in the build/lib folder. `chmod +x` the jar and use `java -jar` to run it

#### Music Attribution:
1. Loving Men by The_Stereo_Inspectors (c) copyright 2018 Licensed under a Creative Commons Attribution Noncommercial  (3.0) license. http://dig.ccmixter.org/files/The_Stereo_Inspectors/58932 Ft: Ciggiburns
2. Big Star (Classic Rock) by Whitewolf (c) copyright 2019 Licensed under a Creative Commons Attribution Noncommercial  (3.0) license. http://dig.ccmixter.org/files/Whitewolf225/60046 Ft: Admiral Bob
3. Free Music And Free Beer by Aussens@iter (c) copyright 2018 Licensed under a Creative Commons Attribution Noncommercial  (3.0) license. http://dig.ccmixter.org/files/tobias_weber/57290 Ft: Admiral Bob, Bill Ray
4. Countryside Summer Joyride by Kara Square (c) copyright 2017 Licensed under a Creative Commons Attribution Noncommercial  (3.0) license. http://dig.ccmixter.org/files/mindmapthat/56281 Ft: Javolenus
5. Winter Night, Summer Day by Beluga Ten (c) copyright 2019 Licensed under a Creative Commons Attribution Noncommercial  (3.0) license. http://dig.ccmixter.org/files/Beluga/59738 
