package models.synthesizer;

import main.ProcessRunner;

import java.io.Externalizable;
import java.io.File;
import java.io.IOException;

// TODO - Make SynthesizerBuilder? Used in synthesizer options popup window (use enums for dropdown?)

public abstract class Synthesizer implements Externalizable {

    private static final long serialVersionUID = -6877641899568882045L;

    /**
     * Audibly plays the specified text as speech
     * @param text The text to be spoken
     */
    public abstract ProcessRunner preview(String text) throws IOException;

    /**
     * Saves the specified text as speech to the specified audio file
     * @param text The text to be saved
     * @param folder The folder to save audio.wav to
     */
    public abstract void save(String text, File folder);

    @Override
    public abstract String toString();
}
