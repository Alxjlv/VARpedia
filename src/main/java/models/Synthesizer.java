package models;

import java.io.File;

public abstract class Synthesizer {

    /**
     * Audibly plays the specified text as speech
     * @param text The text to be spoken
     */
    public abstract void preview(String text);

    /**
     * Saves the specified text as speech to the specified audio file
     * @param text The text to be spoken
     * @param folder The folder to save audio.wav
     */
    public abstract void save(String text, File folder);
}
