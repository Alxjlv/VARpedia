package models;

import java.io.File;

// TODO - Make SynthesizerBuilder? Used in synthesizer options popup window (use enums for dropdown?)

public abstract class Synthesizer {

    /**
     * Audibly plays the specified text as speech
     * @param text The text to be spoken
     */
    public abstract void preview(String text);

    /**
     * Saves the specified text as speech to the specified audio file
     * @param text The text to be saved
     * @param folder The folder to save audio.wav to
     */
    public abstract void save(String text, File folder);
}
