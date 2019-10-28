package constants;

import java.io.File;

/**
 * This enum is responsible for storing the files and names of the music tracks we allow the user to select
 * @author Tait & Alex
 */
public enum Music {
    TRACK_NONE("None",null),
    TRACK_ONE("Rock'n'Roll",new File(Folder.MUSIC.get(),"Free_Music_And_Free_Beer_RocknRoll.mp3")),
    TRACK_TWO("Jazz",new File(Folder.MUSIC.get(),"Loving_Men_Jazz.mp3")),
    TRACK_THREE("Classic Rock",new File(Folder.MUSIC.get(),"Big_Star_Classic_Rock.mp3")),
    TRACK_FIVE("Country",new File(Folder.MUSIC.get(),"Summer_Joyride_Country.mp3"));

    private final String musicGenre;
    private final File musicFile;

    Music(String genre, File music) {
        musicGenre = genre;
        musicFile = music;
    }

    @Override
    public String toString() { // Allows us to display the music genre in the dropdown in NameView
        return musicGenre;
    }

    public File getMusicFile(){ // Accessing the file
        return musicFile;
    }
}
