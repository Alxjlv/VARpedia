package constants;

import java.io.File;

public enum Music {
    TRACK_NONE("None",null),
    TRACK_ONE("Rock'n'Roll",new File(".music","Free_Music_And_Free_Beer_RocknRoll.mp3")),
    TRACK_TWO("Jazz",new File(".music","Loving_Men_Jazz.mp3")),
    TRACK_THREE("Classic Rock",new File(".music","Big_Star_Classic_Rock.mp3")),
    TRACK_FOUR("Musical",new File(".music","Winter_Night_Summer_Day_Musical.mp3")),
    TRACK_FIVE("Country",new File(".music","Summer_Joyride_Country.mp3"));

    private final String musicGenre;
    private final File musicFile;

    Music(String genre, File music) {
        musicGenre = genre;
        musicFile = music;
    }

    @Override
    public String toString() {
        return musicGenre;
    }

    public File getMusicFile(){
        return musicFile;
    }
}
